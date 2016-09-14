package net.archiloque.services_dependencies.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.archiloque.services_dependencies.core.Application;
import net.archiloque.services_dependencies.core.SwaggerService;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Represents a call between two services
 */
public class ServiceCall {

    private final Long count;

    private final String applicationFrom;

    private final String applicationTo;

    private final String serviceFrom;

    private final String serviceTo;

    public ServiceCall(
            @NotNull Long count,
            @NotNull Application applicationFrom,
            @NotNull Application applicationTo,
            @Nullable SwaggerService swaggerServiceFrom,
            @Nullable SwaggerService swaggerServiceTo) {
        this.count = count;
        this.applicationFrom = applicationFrom.getName();
        this.applicationTo = applicationTo.getName();
        this.serviceFrom = (swaggerServiceFrom == null) ? null : swaggerServiceFrom.getName();
        this.serviceTo = (swaggerServiceTo == null) ? null : swaggerServiceTo.getName();
    }

    @JsonProperty
    public Long getCount() {
        return count;
    }

    @JsonProperty
    public String getApplicationFrom() {
        return applicationFrom;
    }

    @JsonProperty
    public String getApplicationTo() {
        return applicationTo;
    }

    @JsonProperty
    public String getServiceFrom() {
        return serviceFrom;
    }

    @JsonProperty
    public String getServiceTo() {
        return serviceTo;
    }

}
