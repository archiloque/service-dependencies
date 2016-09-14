package net.archiloque.services_dependencies.logic.apachelog;

import net.archiloque.services_dependencies.core.Application;
import net.archiloque.services_dependencies.core.Log;
import net.archiloque.services_dependencies.core.LogEntry;
import net.archiloque.services_dependencies.db.ApplicationDAO;
import net.archiloque.services_dependencies.db.LogDAO;
import net.archiloque.services_dependencies.db.LogEntryDAO;
import net.archiloque.services_dependencies.logic.SwaggerServiceMatcher;
import nl.basjes.parse.httpdlog.dissectors.TimeStampDissector;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public class ApacheLogLogic {

    private final LogDAO logDAO;
    private final LogEntryDAO logEntryDAO;
    private final ApplicationDAO applicationDAO;

    private final ApacheLogParser apacheLogParser;
    private final DateTimeFormatter dateParser;

    public ApacheLogLogic(
            @NotNull LogDAO logDAO,
            @NotNull LogEntryDAO logEntryDAO,
            @NotNull ApplicationDAO applicationDAO) {
        this.logDAO = logDAO;
        this.logEntryDAO = logEntryDAO;
        this.applicationDAO = applicationDAO;
        apacheLogParser = new ApacheLogParser();
        dateParser = DateTimeFormat.forPattern(TimeStampDissector.DEFAULT_APACHE_DATE_TIME_PATTERN).withLocale(Locale.US);
    }

    public Log createLog(
            @NotNull Application application,
            @NotNull InputStream uploadedInputStream,
            @NotNull String fileName) throws IOException {

        final Log log = new Log();
        log.setType(Log.APACHE_TYPE);
        log.setApplication(application);
        log.setName(fileName);
        final Log savedLog = logDAO.create(log);

        final Collection<SwaggerServiceMatcher> swaggerServiceMatchers;

        if(! application.getSwaggerServices().isEmpty()) {
            swaggerServiceMatchers = application.getSwaggerServices().stream().
                    map(SwaggerServiceMatcher::new).
                    collect(Collectors.toCollection(ArrayList::new));
        } else {
            swaggerServiceMatchers = null;
        }

        apacheLogParser.parseFile(uploadedInputStream, record -> {
            String serviceOrigin = (record.getServiceOrigin() != null) ? record.getServiceOrigin() : record.getIp();
            String correlationId = record.getCorrelationId();
            Application originApplication = applicationDAO.findOrCreateByName(serviceOrigin);

            LogEntry logEntry = new LogEntry();
            logEntry.setLog(savedLog);
            logEntry.setMethod(record.getMethod().toUpperCase());
            logEntry.setCorrelationId(correlationId);
            logEntry.setOriginApplication(originApplication);
            logEntry.setOriginIp(record.getIp());
            logEntry.setStatusCode(record.getStatusCode());
            logEntry.setUrl(record.getUrl());
            logEntry.setTimestamp(dateParser.parseDateTime(record.getTimestamp()));

            // identify the service
            if(swaggerServiceMatchers != null) {
                // find a swagger service that match the log entry
                Optional<SwaggerServiceMatcher> optionalSwaggerServiceMatcher = swaggerServiceMatchers.stream().
                        filter(swaggerServiceMatcher -> swaggerServiceMatcher.test(logEntry)).
                        findFirst();
                if(optionalSwaggerServiceMatcher.isPresent()) {
                    logEntry.setSwaggerService(optionalSwaggerServiceMatcher.get().getSwaggerService());
                }
            }

            // Identify the calling LogEntry from the origin and the correlation id
            LogEntry originLogEntryCandidate = logEntryDAO.findByCorrelationIdAndApplication(correlationId, originApplication);
            if(originLogEntryCandidate != null) {
                logEntry.setOriginSwaggerService(originLogEntryCandidate.getSwaggerService());
            }

            logEntryDAO.create(logEntry);
        });

        return savedLog;
    }

}
