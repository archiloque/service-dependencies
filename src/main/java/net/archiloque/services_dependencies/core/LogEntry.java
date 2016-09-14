package net.archiloque.services_dependencies.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * A log entry.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "net.archiloque.services_dependencies.core.LogEntry.findByCorrelationIdAndApplication",
                query = "FROM LogEntry le where le.log.application = :application and le.correlationId = :correlationId and le.swaggerService is not null"
        ),
        @NamedQuery(
                name = "net.archiloque.services_dependencies.core.LogEntry.listForDependencies",
                query = "select distinct count(*), le.log.id, le.swaggerService.id, le.originApplication.id, le.originSwaggerService.id FROM LogEntry le group by le.log.id, le.swaggerService.id, le.originApplication.id, le.originSwaggerService.id"
        ),
})@Table(name = "log_entry")
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    private Log log;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "swagger_service_id", nullable = true)
    private SwaggerService swaggerService;

    @Column(name = "correlation_id")
    private String correlationId;

    @NotNull
    private DateTime timestamp;

    @NotNull
    private String method;

    @NotNull
    private String url;

    @NotNull
    @Column(name = "origin_ip")
    private String originIp;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_application_id", nullable = true)
    private Application originApplication;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_swagger_service_id", nullable = true)
    private SwaggerService originSwaggerService;

    @NotNull
    @Column(name = "status_code")
    private String statusCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    @JsonProperty
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @JsonProperty
    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @JsonProperty
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty
    public String getOriginIp() {
        return originIp;
    }

    public void setOriginIp(String originIp) {
        this.originIp = originIp;
    }

    @JsonProperty
    public SwaggerService getSwaggerService() {
        return swaggerService;
    }

    public void setSwaggerService(SwaggerService swaggerService) {
        this.swaggerService = swaggerService;
    }

    @JsonProperty
    public Application getOriginApplication() {
        return originApplication;
    }

    public void setOriginApplication(Application originApplication) {
        this.originApplication = originApplication;
    }

    public SwaggerService getOriginSwaggerService() {
        return originSwaggerService;
    }

    @JsonProperty
    public void setOriginSwaggerService(SwaggerService originSwaggerService) {
        this.originSwaggerService = originSwaggerService;
    }
}
