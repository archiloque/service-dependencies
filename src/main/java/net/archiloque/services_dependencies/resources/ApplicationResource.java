package net.archiloque.services_dependencies.resources;

import io.dropwizard.hibernate.UnitOfWork;
import net.archiloque.services_dependencies.api.Applications;
import net.archiloque.services_dependencies.core.Application;
import net.archiloque.services_dependencies.db.ApplicationDAO;
import net.archiloque.services_dependencies.db.SwaggerServiceDAO;
import net.archiloque.services_dependencies.logic.ApplicationLogic;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Resources for applications
 */
@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource {

    private final ApplicationLogic applicationLogic;

    private final ApplicationDAO applicationDAO;

    public ApplicationResource(ApplicationDAO applicationDAO, SwaggerServiceDAO swaggerServiceDAO) {
        this.applicationDAO = applicationDAO;
        this.applicationLogic = new ApplicationLogic(applicationDAO, swaggerServiceDAO);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Applications list() {
        Applications applications = new Applications();
        applications.setApplications(applicationDAO.findAll());
        return applications;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id : \\d+}")
    @UnitOfWork
    public Response getApplicationById(@PathParam("id") String id) {
        Application application = applicationDAO.findById(Long.parseLong(id));
        if (application != null) {
            return Response.ok(application).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response addApplication(Application application) {
        String candidateName = application.getName();
        if (candidateName == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new Error("Name is missing")).build();
        } else if (applicationDAO.findByName(application.getName()) != null) {
            return Response.status(Response.Status.CONFLICT).entity(new Error("Name already exists")).build();
        } else {
            Application createdApplication = applicationDAO.create(candidateName);
            return Response.created(URI.create("/applications/" + createdApplication.getId())).entity(createdApplication).build();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @Path("{id : \\d+}/swagger")
    public Response uploadSwagger(
            @PathParam("id") String applicationId,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
        // find the related application
        Application application = applicationDAO.findById(Long.parseLong(applicationId));
        if (application != null) {
            return Response.ok(applicationLogic.uploadSwagger(application, uploadedInputStream)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
