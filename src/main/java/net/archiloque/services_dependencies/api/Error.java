package net.archiloque.services_dependencies.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An error
 */
public class Error {

    private final String message;

    public Error(String message) {
        this.message = message;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }
}
