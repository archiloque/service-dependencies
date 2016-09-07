package net.archiloque.services_dependencies.resources;

import io.dropwizard.hibernate.UnitOfWork;
import net.archiloque.services_dependencies.api.Log;
import net.archiloque.services_dependencies.api.LogEntry;
import net.archiloque.services_dependencies.db.LogDAO;
import net.archiloque.services_dependencies.db.LogEntryDAO;
import net.archiloque.services_dependencies.logparser.ApacheLogParser;
import nl.basjes.parse.httpdlog.dissectors.TimeStampDissector;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 *
 */
@Path("/logs/apache")
@Produces(MediaType.APPLICATION_JSON)
public class ApacheLogResource {

    private final LogDAO logDAO;
    private final LogEntryDAO logEntryDAO;
    private final ApacheLogParser apacheLogParser;
    private final DateTimeFormatter dateParser;

    public ApacheLogResource(LogDAO logDAO, LogEntryDAO logEntryDAO) {
        this.logDAO = logDAO;
        this.logEntryDAO = logEntryDAO;
        apacheLogParser = new ApacheLogParser();
        dateParser = DateTimeFormat.forPattern(TimeStampDissector.DEFAULT_APACHE_DATE_TIME_PATTERN).withLocale(Locale.US);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Log createLog(
                         @FormDataParam("file") InputStream uploadedInputStream,
                         @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
        String name = fileDetail.getFileName();
        final Log log = new Log();
        log.setType(Log.APACHE_TYPE);
        log.setName(name);
        final Log savedLog = logDAO.create(log);
        apacheLogParser.parseFile(uploadedInputStream, apacheParsedLogRecord -> {
            LogEntry logEntry = new LogEntry();
            logEntry.setLog(savedLog);
            logEntry.setMethod(apacheParsedLogRecord.getMethod());
            logEntry.setCorrelationId(apacheParsedLogRecord.getCorrelationId());
            logEntry.setOrigin((apacheParsedLogRecord.getServiceOrigin() != null) ? apacheParsedLogRecord.getServiceOrigin() : apacheParsedLogRecord.getIp());
            logEntry.setStatusCode(apacheParsedLogRecord.getStatusCode());
            logEntry.setUrl(apacheParsedLogRecord.getUrl());
            logEntry.setTimestamp(dateParser.parseDateTime(apacheParsedLogRecord.getTimestamp()));
            logEntryDAO.create(logEntry);
        });
        return savedLog;
    }
}
