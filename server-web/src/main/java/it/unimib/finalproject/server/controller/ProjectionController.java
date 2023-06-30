package it.unimib.finalproject.server.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import it.unimib.finalproject.server.service.ProjectionService;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.model.domain.Projection;
import it.unimib.finalproject.server.service.SeatsService;
import it.unimib.finalproject.server.model.domain.Seat;

@Path("projections")
public class ProjectionController {
    @Inject
    ProjectionService projectionService;

    @Inject
    SeatsService seatService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjections(@DefaultValue("-1") @QueryParam("movie_id") int movieId) {
        List<Projection> projections = null;

        if(movieId == -1)
            projections = projectionService.getProjections();
        else   
            projections = projectionService.getProjectionsByMovie(movieId);
 
        if(projections == null || projections.isEmpty()){
            return Response.noContent().build();
        } 
            
        return Response.ok(projections).build();     
    }

    @GET
    @Path("/{projectionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectionById(@PathParam("projectionId") int projectionId) {
        Projection projection = null;
        projection = projectionService.getProjectionById(projectionId);

        if(projection == null)
            throw new NotFoundResponseException();
        
        return Response.ok(projection).build();
    }
    
    @GET
    @Path("/{projectionId}/seats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectionBookedSeats(@PathParam("projectionId") int projectionId) {
        List<Seat> seats = null;
        seats = seatService.getProjectionSeats(projectionId);
        
        if(seats == null || seats.isEmpty()){
            return Response.noContent().build();
        }
         
        return Response.ok(seats).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProjection(String body) {
        try {
            Projection projection = projectionService.createProjection(body);
            var uri = new URI("/projections/" + projection.getId());

            return Response.status(Status.CREATED).entity(projection).location(uri).build();
        } catch (URISyntaxException e) {
            throw new ServerErrorResponseException();
        }
    }


    @PUT
    @Path("/{projectionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProjection(String body, @PathParam("projectionId") int projectionId){
        int updatedProjection = projectionService.updateProjection(projectionId, body);
        
        if (updatedProjection == DatabaseStatus.OBJECT_UPDATED) {
            //Object updated successfully
            return Response.noContent().build(); // 204 No Content
        } else {
            //Object not found
            throw new NotFoundResponseException();
        }
    }

    @DELETE
    @Path("/{projectionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovie(@PathParam("projectionId") int projectionId){
        int deleted = projectionService.deleteProjection(projectionId);

        if(deleted == DatabaseStatus.OBJECT_DELETED){
            //Object deleted, server processed the request successfully
            return Response.noContent().build(); // 204 No Content
        }else{
            //Object to delete not found
            throw new NotFoundResponseException();
        }
        
    }
}
