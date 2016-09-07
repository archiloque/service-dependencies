package net.archiloque.services_dependencies.db;

import io.dropwizard.hibernate.AbstractDAO;
import net.archiloque.services_dependencies.api.Log;
import org.hibernate.SessionFactory;

public class LogDAO extends AbstractDAO<Log> {

    public LogDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Log create(Log log) {
        return persist(log);
    }

}
