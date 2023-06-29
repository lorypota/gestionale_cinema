package it.unimib.finalproject.server.controller;

import jakarta.ws.rs.core.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import java.io.IOException;
import java.util.List;

import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.model.domain.Projection;
import it.unimib.finalproject.server.model.domain.Seat;
import it.unimib.finalproject.server.service.ProjectionService;
import it.unimib.finalproject.server.service.SeatsService;

@Path("projections")
public class ProjectionController {
    @Inject
    ProjectionService projectionService;

    @Inject
    SeatsService seatService;

    @GET
    @Path("/{movieId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectionByMovieId(@PathParam("movieId") int movieId) {
        List<Projection> projections = null;
        projections = projectionService.getProjectionsByMovie(movieId);

        if(projections == null || projections.isEmpty()){
            return Response.noContent().build();
        }
         
        return Response.ok(projections).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjections() {
        List<Projection> projections = null;
        projections = projectionService.getProjections();
        
        if(projections == null || projections.isEmpty()){
            return Response.noContent().build();
        }
         
        return Response.ok(projections).build();
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
}
