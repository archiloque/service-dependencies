package net.archiloque.services_dependencies.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * A declared swagger service
 */
@Entity
@Table(name = "swagger_service")
public class SwaggerService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    @OneToMany(mappedBy = "swaggerService")
    private Set<LogEntry> logEntries;

    @NotNull
    private String path;

    @NotNull
    private String verb;

    @NotNull
    private String name;

    @JsonProperty
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty
    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @JsonIgnore
    public Set<LogEntry> getLogEntries() {
        return logEntries;
    }

    public void setLogEntries(Set<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }
}
