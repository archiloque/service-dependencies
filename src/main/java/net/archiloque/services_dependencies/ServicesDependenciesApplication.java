package net.archiloque.services_dependencies;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.archiloque.services_dependencies.db.ApplicationDAO;
import net.archiloque.services_dependencies.db.LogDAO;
import net.archiloque.services_dependencies.db.LogEntryDAO;
import net.archiloque.services_dependencies.db.SwaggerServiceDAO;
import net.archiloque.services_dependencies.resources.ApacheLogResource;
import net.archiloque.services_dependencies.resources.ApplicationResource;
import net.archiloque.services_dependencies.resources.ServiceDependenciesResource;

public class ServicesDependenciesApplication extends io.dropwizard.Application<ServicesDependenciesConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ServicesDependenciesApplication().run(args);
    }

    private final ScanningHibernateBundle<ServicesDependenciesConfiguration> hibernateBundle =
            new ScanningHibernateBundle<ServicesDependenciesConfiguration>(
                    "net.archiloque.services_dependencies.core") {

                @Override
                public DataSourceFactory getDataSourceFactory(ServicesDependenciesConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };


    @Override
    public String getName() {
        return "services-dependencies";
    }

    @Override
    public void initialize(final Bootstrap<ServicesDependenciesConfiguration> bootstrap) {
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MigrationsBundle<ServicesDependenciesConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ServicesDependenciesConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }

    @Override
    public void run(final ServicesDependenciesConfiguration configuration,
                    final Environment environment) {
        final LogDAO logDAO = new LogDAO(hibernateBundle.getSessionFactory());
        final LogEntryDAO logEntryDAO = new LogEntryDAO(hibernateBundle.getSessionFactory());
        final ApplicationDAO applicationDAO = new ApplicationDAO(hibernateBundle.getSessionFactory());
        final SwaggerServiceDAO swaggerServiceDAO = new SwaggerServiceDAO(hibernateBundle.getSessionFactory());

        environment.jersey().register(new ApplicationResource(applicationDAO, swaggerServiceDAO));
        environment.jersey().register(new ApacheLogResource(logDAO, logEntryDAO, applicationDAO));
        environment.jersey().register(new ServiceDependenciesResource(applicationDAO, logDAO, logEntryDAO, swaggerServiceDAO));
    }

}
