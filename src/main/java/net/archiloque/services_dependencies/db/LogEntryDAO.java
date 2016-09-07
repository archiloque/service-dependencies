package net.archiloque.services_dependencies.db;

import io.dropwizard.hibernate.AbstractDAO;
import net.archiloque.services_dependencies.api.LogEntry;
import org.hibernate.SessionFactory;

public class LogEntryDAO extends AbstractDAO<LogEntry> {

    public LogEntryDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public LogEntry create(LogEntry logEntry) {
        return persist(logEntry);
    }

}
