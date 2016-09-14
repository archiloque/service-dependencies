package net.archiloque.services_dependencies.db;

import io.dropwizard.hibernate.AbstractDAO;
import net.archiloque.services_dependencies.core.Application;
import net.archiloque.services_dependencies.core.LogEntry;
import org.hibernate.SessionFactory;

import javax.validation.constraints.NotNull;
import java.util.List;

public class LogEntryDAO extends AbstractDAO<LogEntry> {

    public LogEntryDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public LogEntry create(LogEntry logEntry) {
        return persist(logEntry);
    }

    public LogEntry findByCorrelationIdAndApplication(@NotNull String correlationId, @NotNull Application application){
        return uniqueResult(
                namedQuery("net.archiloque.services_dependencies.core.LogEntry.findByCorrelationIdAndApplication").
                        setString("correlationId", correlationId).
                        setEntity("application", application));
    }

    /**
     * List possible services calls.
     * @return a cosmic horror : an array of Long, see the query to see what they correspond to.
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> listForDependencies(){
        return namedQuery("net.archiloque.services_dependencies.core.LogEntry.listForDependencies").list();
    }

}
