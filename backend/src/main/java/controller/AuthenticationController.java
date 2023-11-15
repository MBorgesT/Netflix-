package controller;

import business.AuthenticationBusiness;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.security.NoSuchAlgorithmException;

@Singleton
@Path("/auth")
public class AuthenticationController {

    @POST
    @Path("/subscriberLogin")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response subscriberLogin(@FormDataParam("username") String username,
                            @FormDataParam("password") String password) {
        try {
            String msg;
            if (AuthenticationBusiness.subscriberLogin(username, password)) {
                return Response.ok("Welcome!").build();
            } else {
                return Response.status(401, "Wrong credentials").build();
            }
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/newSubscriber")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response newSubscriber(@FormDataParam("username") String username,
                                @FormDataParam("password") String password) {
        try {
            return Response.ok(AuthenticationBusiness.newSubscriber(username, password)).build();
        } catch (NoSuchAlgorithmException e) {
            return Response.status(500).build();
        }
    }

}
