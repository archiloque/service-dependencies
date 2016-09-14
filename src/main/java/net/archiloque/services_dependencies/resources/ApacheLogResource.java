package net.archiloque.services_dependencies.resources;

import io.dropwizard.hibernate.UnitOfWork;
import net.archiloque.services_dependencies.logic.apachelog.ApacheLogLogic;
import net.archiloque.services_dependencies.core.Application;
import net.archiloque.services_dependencies.db.ApplicationDAO;
import net.archiloque.services_dependencies.db.LogDAO;
import net.archiloque.services_dependencies.db.LogEntryDAO;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * For apache logs
 */
@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
public class ApacheLogResource {

    private final ApplicationDAO applicationDAO;

    private final ApacheLogLogic apacheLogBusiness;

    public ApacheLogResource(LogDAO logDAO, LogEntryDAO logEntryDAO, ApplicationDAO applicationDAO) {
        apacheLogBusiness = new ApacheLogLogic(logDAO, logEntryDAO, applicationDAO);
        this.applicationDAO = applicationDAO;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @Path("{id : \\d+}/logs/apache")
    public Response createLog(
            @PathParam("id") String applicationId,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
        // find the related application
        Application application = applicationDAO.findById(Long.parseLong(applicationId));
        if (application != null) {
            return Response.ok(apacheLogBusiness.createLog(application, uploadedInputStream, fileDetail.getFileName())).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
