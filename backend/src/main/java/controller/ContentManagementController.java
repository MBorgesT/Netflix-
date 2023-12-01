package controller;

import business.ContentManagementBusiness;
import business.UserManagementBusiness;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.MediaMetadata;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import exceptions.FileAlreadyUploadedException;
import org.json.JSONObject;

import java.io.*;
import java.util.List;
import java.util.Map;

@Singleton
@Path("/contentManagement")
public class ContentManagementController {

    @POST
    @Path("/uploadMedia")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadMedia(@FormDataParam("json") String jsonPayload,
                                @FormDataParam("file") InputStream fileInputStream) {
        try {
            if (jsonPayload == null) {
                return Response.status(400).entity("Media metadata missing").build();
            }
            JSONObject jsonObject = new JSONObject(jsonPayload);
            if (!jsonObject.has("title")) {
                return Response.status(400).entity("Title missing").build();
            }

            String title = jsonObject.getString("title");
            String description = null;
            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }

            ContentManagementBusiness.uploadMedia(title, description, fileInputStream);
            return Response.ok("Data uploaded successfully!").build();
        } catch (FileAlreadyUploadedException e) {
            return Response.status(205, "File already uploaded.").build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GET
        @Path("/getMediaMetadatas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdminsInfo() {
        try {
            List<MediaMetadata> metadatas = ContentManagementBusiness.getMediaMetadataList();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(metadatas);

            return Response.status(Response.Status.OK).entity(json).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

//    // TODO: Make upload statuses a model and serialize the list of them here in the controller
//    @GET
//    @Path("/uploadStatuses")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response uploadStatuses() {
//        try {
//            Map<String, String> map = ContentManagementBusiness.getUploadStatuses();
//            return Response.status(Response.Status.OK).entity(map).build();
//        } catch (IOException e) {
//            return Response.status(500).build();
//        }
//    }

}
