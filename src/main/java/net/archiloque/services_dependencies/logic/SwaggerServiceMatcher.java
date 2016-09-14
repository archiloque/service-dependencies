package net.archiloque.services_dependencies.logic;

import net.archiloque.services_dependencies.core.LogEntry;
import net.archiloque.services_dependencies.core.SwaggerService;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Predicate to test if a LogEntry match a SwaggerService.
 */
public class SwaggerServiceMatcher implements Predicate<LogEntry> {

    private final SwaggerService swaggerService;

    private final Predicate<String> pathPredicate;

    public SwaggerServiceMatcher(SwaggerService swaggerService) {
        this.swaggerService = swaggerService;
        String swaggerServicePath = swaggerService.getPath();
        if (swaggerServicePath.indexOf('{') == -1) {
            pathPredicate = new PlainPathPredicate(swaggerServicePath);
        } else {
            pathPredicate = new PatternPathPredicate(swaggerServicePath);
        }
    }

    @Override
    public boolean test(LogEntry logEntry) {
        return
                logEntry.getMethod().equals(swaggerService.getVerb()) &&
                        pathPredicate.test(logEntry.getUrl());
    }

    public SwaggerService getSwaggerService() {
        return swaggerService;
    }

    /**
     * Predicate for plain pathes.
     */
    private final static class PlainPathPredicate implements Predicate<String> {

        private final String stringToTest;

        private PlainPathPredicate(String stringToTest) {
            this.stringToTest = stringToTest;
        }

        @Override
        public boolean test(String s) {
            return s.equals(stringToTest);
        }
    }

    /**
     * Predicate for path containing patterns.
     */
    private final static class PatternPathPredicate implements Predicate<String> {

        private static final Pattern SPLITTING_PATTERN = Pattern.compile("/");

        private final String templatePath;

        private final Pattern pathPattern;

        private PatternPathPredicate(String templatePath) {
            this.templatePath = templatePath;
            String regexString = Arrays.stream(SPLITTING_PATTERN.split(templatePath)).map(new Function<String, String>() {
                @Override
                public String apply(String s) {
                    if (s.indexOf('{') != -1) {
                        if (s.indexOf('{') != 0) {
                            throw new IllegalArgumentException("Path is invalid [" + templatePath + "]");
                        }
                        if (s.indexOf('}') != (s.length() - 1)) {
                            throw new IllegalArgumentException("Path is invalid [" + templatePath + "]");
                        }
                        return "([^\\\\]+)";
                    } else {
                        return s;
                    }

                }
            }).collect(Collectors.joining("/"));
            pathPattern = Pattern.compile("\\A" + regexString + "\\z");
        }

        @Override
        public boolean test(String s) {
            return pathPattern.matcher(s).matches();
        }
    }
}
