package net.archiloque.services_dependencies.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.archiloque.services_dependencies.core.Application;

import java.util.List;

/**
 * A list of applications
 */
public class Applications {

    private List<Application> applications;

    public Applications(){
    }

    @JsonProperty
    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
