package controller;

import business.UserManagementBusiness;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.EmptyParameterException;
import exceptions.InvalidRoleException;
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

    @POST
    @Path("/newUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newUser(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            if (!jsonObject.has("username")
                    || !jsonObject.has("password")
                    || !jsonObject.has("role")) {
                return Response.status(400).entity("Username or password missing").build();
            }

            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            String role = jsonObject.getString("role");

            if (UserManagementBusiness.doesUserExist(username)) {
                return Response.status(400).entity("Username already in use").build();
            }

            UserManagementBusiness.newUser(username, password, role);
            return Response.status(201).entity("New user created!").build();
        } catch (JSONException e) {
            return Response.status(400).entity("Invalid JSON payload").build();
        } catch (EmptyParameterException e) {
            return Response.status(400).entity("There are empty parameters").build();
        } catch (InvalidRoleException e) {
            return Response.status(400).entity("Invalid role").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

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

    @DELETE
    @Path("/deleteUser/{userId}")
    //@Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("userId") int userId) {
        try {
            if (!UserManagementBusiness.doesUserExist(userId)) {
                return Response.status(400).entity("User with the specified id does not exist").build();
            }

            UserManagementBusiness.deleteUser(userId);
            return Response.ok().entity("User successfully deleted").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

}
