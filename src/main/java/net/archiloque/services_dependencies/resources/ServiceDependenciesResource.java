package net.archiloque.services_dependencies.resources;

import io.dropwizard.hibernate.UnitOfWork;
import net.archiloque.services_dependencies.api.ServiceCall;
import net.archiloque.services_dependencies.api.ServicesDependencies;
import net.archiloque.services_dependencies.core.Application;
import net.archiloque.services_dependencies.core.SwaggerService;
import net.archiloque.services_dependencies.db.ApplicationDAO;
import net.archiloque.services_dependencies.db.LogDAO;
import net.archiloque.services_dependencies.db.LogEntryDAO;
import net.archiloque.services_dependencies.db.SwaggerServiceDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.stream.Collectors;

/**
 * Access dependencies data
 */
@Path("/dependencies")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceDependenciesResource {

    private final ApplicationDAO applicationDAO;

    private final LogDAO logDAO;

    private final LogEntryDAO logEntryDAO;

    private final SwaggerServiceDAO swaggerServiceDAO;

    public ServiceDependenciesResource(
            ApplicationDAO applicationDAO,
            LogDAO logDAO,
            LogEntryDAO logEntryDAO,
            SwaggerServiceDAO swaggerServiceDAO) {
        this.applicationDAO = applicationDAO;
        this.logDAO = logDAO;
        this.logEntryDAO = logEntryDAO;
        this.swaggerServiceDAO = swaggerServiceDAO;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public ServicesDependencies list() {
        ServicesDependencies servicesDependencies = new ServicesDependencies();
        servicesDependencies.setApplications(applicationDAO.findAll());
        servicesDependencies.setServiceCalls(logEntryDAO.listForDependencies().stream().map(pseudoLogEntry -> {
            // transform the array of longs into something usable
            // 0 is count
            Long count = (Long) pseudoLogEntry[0];
            // 1 is log id of the entry
            Long logEntryLogId = (Long) pseudoLogEntry[1];
            // 2 is swagger of the called service
            Long swaggerServiceToId = (Long) pseudoLogEntry[2];
            // 3 is the origin application id
            Long originalApplicationId = (Long) pseudoLogEntry[3];
            // 4 is swagger of the calling service
            Long originalSwaggerServiceId = (Long) pseudoLogEntry[4];

            Application applicationTo = logDAO.findById(logEntryLogId).getApplication();
            SwaggerService swaggerServiceTo = (swaggerServiceToId == null) ? null : swaggerServiceDAO.findById(swaggerServiceToId);
            Application applicationFrom = applicationDAO.findById(originalApplicationId);
            SwaggerService swaggerServiceFrom = (originalSwaggerServiceId == null) ? null : swaggerServiceDAO.findById(originalSwaggerServiceId);

            return new ServiceCall(
                    count,
                    applicationFrom,
                    applicationTo,
                    swaggerServiceFrom,
                    swaggerServiceTo
            )       ;
        }).collect(Collectors.toList()));
        return servicesDependencies;
    }

}
