package net.archiloque.services_dependencies.db;

import io.dropwizard.hibernate.AbstractDAO;
import net.archiloque.services_dependencies.core.Application;
import org.hibernate.SessionFactory;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ApplicationDAO extends AbstractDAO<Application> {

    public ApplicationDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Application create(String applicationName) {
        Application application = new Application();
        application.setName(applicationName);
        return persist(application);
    }

    public Application findById(@NotNull Long id) {
        return get(id);
    }

    public Application findByName(@NotNull String applicationName) {
        return uniqueResult(namedQuery("net.archiloque.services_dependencies.core.Application.findByName").setString("name", applicationName));
    }

    public Application findOrCreateByName(@NotNull String applicationName){
        Application application = findByName(applicationName);
        if(application != null) {
            return application;
        } else {
            return create(applicationName);
        }
    }

    public Application update(@NotNull Application application){
        return persist(application);
    }

    public List<Application> findAll() {
        return list(namedQuery("net.archiloque.services_dependencies.core.Application.findAll"));
    }


}
