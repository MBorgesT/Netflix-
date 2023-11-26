package controller;

import business.UserManagementBusiness;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@Singleton
@Path("/userManagement")
public class UserManagementController {

    @GET
    @Path("/getAdminsInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdminsInfo() {
        try {
            List<User> admins = UserManagementBusiness.getUserList(true);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(admins);

            return Response.status(Response.Status.OK).entity(json).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/getSubscribersInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscribersInfo() {
        try {
            List<User> admins = UserManagementBusiness.getUserList(false);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(admins);

            return Response.status(Response.Status.OK).entity(json).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/deleteUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            if (!jsonObject.has("id")) {
                return Response.status(400).entity("Id missing").build();
            }

            int id = jsonObject.getInt("id");

            if (!UserManagementBusiness.doesUserExist(id)) {
                return Response.status(400).entity("User with the specified id does not exist").build();
            }

            UserManagementBusiness.deleteUser(id);
            return Response.ok().entity("{\"message\": \"New admin created!\"}").build();
        } catch (JSONException e) {
            return Response.status(400).entity("Invalid JSON payload").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

}
