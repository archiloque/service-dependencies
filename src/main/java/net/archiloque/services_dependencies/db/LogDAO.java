package net.archiloque.services_dependencies.db;

import io.dropwizard.hibernate.AbstractDAO;
import net.archiloque.services_dependencies.core.Log;
import org.hibernate.SessionFactory;

import javax.validation.constraints.NotNull;

public class LogDAO extends AbstractDAO<Log> {

    public LogDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Log findById(@NotNull Long id) {
        return get(id);
    }
    public Log create(Log log) {
        return persist(log);
    }

}
