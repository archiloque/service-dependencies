package net.archiloque.services_dependencies.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.archiloque.services_dependencies.core.Application;

import java.util.List;

/**
 * Data for the dependencies graphs
 */
public class ServicesDependencies {

    private List<Application> applications;

    private List<ServiceCall> serviceCalls;

    public ServicesDependencies(){
    }

    @JsonProperty
    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    @JsonProperty
    public List<ServiceCall> getServiceCalls() {
        return serviceCalls;
    }

    public void setServiceCalls(List<ServiceCall> serviceCalls) {
        this.serviceCalls = serviceCalls;
    }
}
