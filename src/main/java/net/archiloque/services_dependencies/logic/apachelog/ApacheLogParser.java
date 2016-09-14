package net.archiloque.services_dependencies.logic.apachelog;

import nl.basjes.parse.core.Field;
import nl.basjes.parse.core.Parser;
import nl.basjes.parse.core.exceptions.DissectionFailure;
import nl.basjes.parse.core.exceptions.InvalidDissectorException;
import nl.basjes.parse.core.exceptions.MissingDissectorsException;
import nl.basjes.parse.httpdlog.ApacheHttpdLoglineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Parse Apache log
 */
public class ApacheLogParser {

    public static final String LOG_FORMAT = "%h %{X-SERVICE-ORIGIN}i %{X-CORRELATION-ID}i %t \"%r\" %>s";

    private final Parser<ApacheParsedLogRecord> parser;

    public ApacheLogParser(){
         parser = new ApacheHttpdLoglineParser<>(ApacheParsedLogRecord.class, LOG_FORMAT);
    }

    public void parseFile(InputStream inputStream, Consumer<ApacheParsedLogRecord> consumer) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))){
            bufferedReader.lines().forEach(s -> {
                try {
                    ApacheParsedLogRecord apacheParsedLogRecord = parser.parse(s);
                    consumer.accept(apacheParsedLogRecord);
                } catch (DissectionFailure | MissingDissectorsException | InvalidDissectorException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public final static class ApacheParsedLogRecord {

        private String correlationId;

        private String timestamp;

        private String method;

        private String url;

        private String ip;

        private String serviceOrigin;

        private String statusCode;

        @Field("HTTP.HEADER:request.header.x-correlation-id")
        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        @Field("TIME.STAMP:request.receive.time")
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Field("HTTP.URI:request.firstline.uri")
        public void setUrl(String url) {
            this.url = url;
        }

        @Field("IP:connection.client.host")
        public void setIp(String ip) {
            this.ip = ip;
        }

        @Field("HTTP.METHOD:request.firstline.method")
        public void setMethod(String method) {
            this.method = method;
        }

        @Field("HTTP.HEADER:request.header.x-service-origin")
        public void setServiceOrigin(String serviceOrigin) {
            this.serviceOrigin = serviceOrigin;
        }

        @Field("STRING:request.status.last")
        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getMethod() {
            return method;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public String getServiceOrigin() {
            return serviceOrigin;
        }

        public String getIp() {
            return ip;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public String getUrl() {
            return url;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

}
