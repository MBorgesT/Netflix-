package controller;

import business.ContentManagementBusiness;
import business.MeshStreamBusiness;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.EmptyParameterException;
import jakarta.inject.Singleton;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.MediaMetadata;
import org.glassfish.jersey.media.multipart.FormDataParam;
import exceptions.FileAlreadyUploadedException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

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

            if (ContentManagementBusiness.doesTitleExist(title)) {
                return Response.status(400).entity("Title already in use").build();
            }

            ContentManagementBusiness.uploadMedia(title, description, fileInputStream);
            return Response.ok("Data uploaded successfully!").build();
        } catch (FileAlreadyUploadedException e) {
            return Response.status(205, "File already uploaded.").build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PUT
    @Path("/updateMedia")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMedia(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            if (!jsonObject.has("id")
                    || !jsonObject.has("title")) {
                return Response.status(400).entity("Id or title missing").build();
            }

            int id = jsonObject.getInt("id");
            String title = jsonObject.getString("title");
            String description = jsonObject.getString("description");

            if (ContentManagementBusiness.doesTitleExistAtAnotherId(title, id)) {
                return Response.status(400).entity("Title already in use").build();
            }

            ContentManagementBusiness.updateMedia(id, title, description);
            return Response.status(200).entity("Media updated successfully!").build();
        } catch (JSONException e) {
            return Response.status(400).entity("Invalid JSON payload").build();
        } catch (EmptyParameterException e) {
            return Response.status(400).entity("There are empty parameters").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/getMediaMetadatas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMediaMetadatas() {
        try {
            List<MediaMetadata> metadatas = ContentManagementBusiness.getMediaMetadataList();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(metadatas);

            return Response.status(Response.Status.OK).entity(json).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/getMediasReadyToPlay")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMediasReadyToPlay() {
        try {
            List<MediaMetadata> metadatas = ContentManagementBusiness.getMediasReadyToPlay();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(metadatas);

            return Response.status(Response.Status.OK).entity(json).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("getMediaById/{mediaId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMediaInfo(@PathParam("mediaId") int mediaId) {
        try {
            MediaMetadata media = ContentManagementBusiness.getMediaById(mediaId);
            if (media == null) {
                return Response.status(400).entity("Media with the specified id does not exist").build();
            }
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(media);
            return Response.ok().entity(json).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @DELETE
    @Path("/deleteMedia/{mediaId}")
    public Response deleteUser(@PathParam("mediaId") int mediaId) {
        try {
            if (!ContentManagementBusiness.doesMediaExist(mediaId)) {
                return Response.status(400).entity("Media with the specified id does not exist").build();
            }

            ContentManagementBusiness.deleteMedia(mediaId);
            return Response.ok().entity("Media successfully deleted").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/getChunkUris/{mediaId}")
    public Response getChunkUris(@PathParam("mediaId") int mediaId) {
        try {
            if (!ContentManagementBusiness.doesMediaExist(mediaId)) {
                return Response.status(400).entity("Media with the specified id does not exist").build();
            }

            List<String> uris = ContentManagementBusiness.getChunkUris(mediaId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(uris);
            return Response.ok().entity(json).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/signalStreamAvailability/{mediaId}")
    public Response signalMeshAvailability(@PathParam("mediaId") int mediaId, @Context HttpServletRequest request) {
        try {
            if (!ContentManagementBusiness.doesMediaExist(mediaId)) {
                return Response.status(400).entity("Media with the specified id does not exist").build();
            }

            String clientIP = request.getRemoteAddr();
            MeshStreamBusiness.signalStreamAvailability(mediaId, clientIP);
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/signalStreamUnavailability/{mediaId}")
    public Response signalMeshUnavailability(@PathParam("mediaId") int mediaId, @Context HttpServletRequest request) {
        try {
            if (!ContentManagementBusiness.doesMediaExist(mediaId)) {
                return Response.status(400).entity("Media with the specified id does not exist").build();
            }

            String clientIP = request.getRemoteAddr();
            MeshStreamBusiness.signalStreamUnavailability(mediaId, clientIP);
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/getStreamingSources/{mediaId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStreamingSources(@PathParam("mediaId") int mediaId) {
        try {
            List<String> sources = MeshStreamBusiness.getStreamingSources(mediaId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(sources);

            return Response.status(Response.Status.OK).entity(json).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

}
