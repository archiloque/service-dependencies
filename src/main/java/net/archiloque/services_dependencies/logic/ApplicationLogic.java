package net.archiloque.services_dependencies.logic;

import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;
import net.archiloque.services_dependencies.core.Application;
import net.archiloque.services_dependencies.core.SwaggerService;
import net.archiloque.services_dependencies.db.ApplicationDAO;
import net.archiloque.services_dependencies.db.SwaggerServiceDAO;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class ApplicationLogic {

    private final ApplicationDAO applicationDAO;

    private final SwaggerServiceDAO swaggerServiceDAO;

    public ApplicationLogic(ApplicationDAO applicationDAO, SwaggerServiceDAO swaggerServiceDAO) {
        this.applicationDAO = applicationDAO;
        this.swaggerServiceDAO = swaggerServiceDAO;
    }

    /**
     * Uploading a swagger file.
     * @param application the application.
     * @param uploadedInputStream the swagger content.
     * @return the swagger content
     */
    public String uploadSwagger(Application application, InputStream uploadedInputStream) throws IOException {
        String swaggerContent = IOUtils.toString(uploadedInputStream);
        Swagger swagger = new Swagger20Parser().parse(swaggerContent);
        application.setSwagger(swaggerContent);
        application.getSwaggerServices().clear();
        applicationDAO.update(application);
        swagger.getPaths().forEach((stringPath, path) -> {
            path.getOperationMap().forEach((httpMethod, operation) -> {
                SwaggerService swaggerService = new SwaggerService();
                swaggerService.setVerb(httpMethod.toString());
                swaggerService.setPath(stringPath);
                swaggerService.setName(operation.getOperationId());
                swaggerService.setApplication(application);
                swaggerServiceDAO.create(swaggerService);
            });
        });
        return swaggerContent;
    }
}
