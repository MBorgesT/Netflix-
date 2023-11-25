package controller;

import business.ContentManagementBusiness;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import projectExceptions.FileAlreadyUploadedException;
import utils.AuthUtil;

import java.io.*;
import java.util.Map;

@Singleton
@Path("/contentManagement")
public class ContentManagementController {

    @POST
    @Path("/uploadMedia")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadMedia(@FormDataParam("file") InputStream fileInputStream,
                                  @FormDataParam("file") FormDataContentDisposition fileMetaData) {
        try {
            ContentManagementBusiness.uploadMedia(fileInputStream, fileMetaData);
            return Response.ok("Data uploaded successfully!").build();
        } catch (FileAlreadyUploadedException e) {
            return Response.status(205, "File already uploaded.").build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Make upload statuses a model and serialize the list of them here in the controller
    @GET
    @Path("/uploadStatuses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadStatuses() {
        try {
            Map<String, String> map = ContentManagementBusiness.getUploadStatuses();
            return Response.status(Response.Status.OK).entity(map).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

}
