package it.unimib.finalproject.server.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.service.HallService;
import it.unimib.finalproject.server.model.domain.Hall;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHall(String body) {
        try {
            Hall hall = hallService.createHall(body);
            var uri = new URI("/halls/" + hall.getId());

            return Response.status(Status.CREATED).entity(hall).location(uri).build();
        } catch (URISyntaxException e) {
            throw new ServerErrorResponseException();
        }
    }


    @PUT
    @Path("/{hallId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHall(String body, @PathParam("hallId") int hallId){
        int updatedHall = hallService.updateHall(hallId, body);
        
        if (updatedHall == DatabaseStatus.OBJECT_UPDATED) {
            //Object updated successfully
            return Response.noContent().build(); // 204 No Content
        } else {
            //Object not found
            throw new NotFoundResponseException();
        }
    }

    @DELETE
    @Path("/{hallId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHall(@PathParam("hallId") int hallId){
        int deleted = hallService.deleteHall(hallId);

        if(deleted == DatabaseStatus.OBJECT_DELETED){
            //Object deleted, server processed the request successfully
            return Response.noContent().build(); // 204 No Content
        }else{
            //Object to delete not found
            throw new NotFoundResponseException();
        }
        
    }
}
