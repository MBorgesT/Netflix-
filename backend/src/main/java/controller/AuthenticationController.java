package controller;

import business.AuthenticationBusiness;
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

            if (AuthenticationBusiness.login(username, password, false)) {
                return Response.ok().entity("{\"message\": \"Welcome!\"}").build();
            } else {
                return Response.status(401).entity("{\"message\": \"Wrong credentials\"}").build();
            }
        } catch (JSONException e) {
            return Response.status(400).entity("Invalid JSON payload").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/newSubscriber")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newSubscriber(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            if (!jsonObject.has("username") || !jsonObject.has("password")) {
                return Response.status(400).entity("Username or password missing").build();
            }

            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");

            if (AuthenticationBusiness.doesUserExist(username)) {
                return Response.status(400).entity("Username already in use").build();
            }

            AuthenticationBusiness.newUser(username, password, false);
            return Response.ok().entity("{\"message\": \"New subscriber created!\"}").build();
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

            if (AuthenticationBusiness.login(username, password, true)) {
                return Response.ok().entity("{\"message\": \"Welcome!\"}").build();
            } else {
                return Response.status(401).entity("{\"message\": \"Wrong credentials\"}").build();
            }
        } catch (JSONException e) {
            return Response.status(400).entity("Invalid JSON payload").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/newAdmin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newAdmin(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            if (!jsonObject.has("username") || !jsonObject.has("password")) {
                return Response.status(400).entity("Username or password missing").build();
            }

            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");

            if (AuthenticationBusiness.doesUserExist(username)) {
                return Response.status(400).entity("Username already in use").build();
            }

            AuthenticationBusiness.newUser(username, password, true);
            return Response.ok().entity("{\"message\": \"New admin created!\"}").build();
        } catch (JSONException e) {
            return Response.status(400).entity("Invalid JSON payload").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

}
