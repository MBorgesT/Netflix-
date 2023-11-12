package controller;

import business.AuthenticationBusiness;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;
import projectExceptions.WrongCredentialsException;

@Singleton
@Path("/auth")
public class AuthenticationController {

    @POST
    @Path("/login")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response login(@FormDataParam("username") String username,
                            @FormDataParam("password") String password) {
        try {
            String authCode = AuthenticationBusiness.login(username, password);
            return Response.ok(authCode).build();
        } catch (WrongCredentialsException e) {
            return Response.status(401, "Wrong credentials").build();
        }
    }

    @POST
    @Path("/newUser")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response newUser(@FormDataParam("username") String username,
                                @FormDataParam("password") String password) {
        return Response.ok(AuthenticationBusiness.newUser(username, password)).build();
    }

}
