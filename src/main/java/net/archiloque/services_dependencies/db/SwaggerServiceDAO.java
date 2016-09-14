package net.archiloque.services_dependencies.db;

import io.dropwizard.hibernate.AbstractDAO;
import net.archiloque.services_dependencies.core.SwaggerService;
import org.hibernate.SessionFactory;

import javax.validation.constraints.NotNull;

public class SwaggerServiceDAO extends AbstractDAO<SwaggerService> {

    public SwaggerServiceDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public SwaggerService findById(@NotNull Long id) {
        return get(id);
    }

    public SwaggerService create(@NotNull SwaggerService swaggerService){
        return persist(swaggerService);
    }

}
