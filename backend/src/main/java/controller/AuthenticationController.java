package controller;

import business.UserManagementBusiness;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;


@Singleton
@Path("/auth")
public class AuthenticationController {

    @POST
    @Path("/subscriberLogin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscriberLogin(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            if (!jsonObject.has("username") || !jsonObject.has("password")) {
                return Response.status(400).entity("Username or password missing").build();
            }

            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");

            if (UserManagementBusiness.login(username, password, false)) {
                return Response.ok().entity("Welcome!").build();
            } else {
                return Response.status(401).entity("Wrong credentials").build();
            }
        } catch (JSONException e) {
            return Response.status(400).entity("Invalid JSON payload").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/adminLogin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminLogin(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            if (!jsonObject.has("username") || !jsonObject.has("password")) {
                return Response.status(400).entity("Username or password missing").build();
            }

            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");

            if (UserManagementBusiness.login(username, password, true)) {
                return Response.ok().entity("Welcome!").build();
            } else {
                return Response.status(401).entity("Wrong credentials").build();
            }
        } catch (JSONException e) {
            return Response.status(400).entity("Invalid JSON payload").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

}
