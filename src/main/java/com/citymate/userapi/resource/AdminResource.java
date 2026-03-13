package com.citymate.userapi.resource;

import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.service.AdminService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {

    @Autowired
    private AdminService adminService;

    @GET
    @Path("/users")
    public Response getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        return Response.ok(users).build();
    }

    @PUT
    @Path("/users/{username}/activate")
    public Response activateUser(@PathParam("username") String username) {
        UserDTO user = adminService.setUserActive(username, true);
        return Response.ok(user).build();
    }

    @PUT
    @Path("/users/{username}/deactivate")
    public Response deactivateUser(@PathParam("username") String username) {
        UserDTO user = adminService.setUserActive(username, false);
        return Response.ok(user).build();
    }

    @PUT
    @Path("/users/{username}/role")
    public Response changeRole(
            @PathParam("username") String username,
            Map<String, String> body) {
        String roleName = body.get("role");
        if (roleName == null || roleName.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Le champ 'role' est requis"))
                    .build();
        }
        UserDTO user = adminService.changeUserRole(username, roleName.toUpperCase());
        return Response.ok(user).build();
    }
}
