package controller;

import business.ContentManagementBusiness;
import business.UserManagementBusiness;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

}
