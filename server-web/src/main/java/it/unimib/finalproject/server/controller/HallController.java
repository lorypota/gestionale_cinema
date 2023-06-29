package it.unimib.finalproject.server.controller;

import java.util.List;

import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.service.HallService;
import it.unimib.finalproject.server.model.domain.Hall;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
@Path("halls")
public class HallController {
    @Inject
    HallService hallService;

    @GET
    @Path("/{hallId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHallById(@PathParam("hallId") int hallId) {
        Hall hall = null;
        hall = hallService.getHallById(hallId);

        if(hall == null){
            throw new NotFoundResponseException();
        }
         
        return Response.ok(hall).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllHalls() {
        List<Hall> halls = null;
        halls = hallService.getAllHalls();

        if(halls == null || halls.isEmpty()){
            return Response.noContent().build();
        }
         
        return Response.ok(halls).build();
    }
}
