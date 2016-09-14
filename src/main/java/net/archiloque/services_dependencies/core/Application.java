package net.archiloque.services_dependencies.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * An application instance
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "net.archiloque.services_dependencies.core.Application.findAll",
                query = "FROM Application a"
        ),
        @NamedQuery(
                name = "net.archiloque.services_dependencies.core.Application.findByName",
                query = "FROM Application a where a.name = :name"
        )
})
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "application")
    private Set<Log> logs;

    @OneToMany(mappedBy = "application")
    private Set<SwaggerService> swaggerServices;

    private String swagger;

    public Application() {
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getSwagger() {
        return swagger;
    }

    public void setSwagger(String swagger) {
        this.swagger = swagger;
    }

    @JsonProperty
    public Set<SwaggerService> getSwaggerServices() {
        return swaggerServices;
    }

    public void setSwaggerServices(Set<SwaggerService> swaggerServices) {
        this.swaggerServices = swaggerServices;
    }

    @JsonIgnore
    public Set<Log> getLogs() {
        return logs;
    }

    public void setLogs(Set<Log> logs) {
        this.logs = logs;
    }
}
