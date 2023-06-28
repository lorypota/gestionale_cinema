package it.unimib.finalproject.server.controller;

import jakarta.ws.rs.core.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.finalproject.server.service.ProjectionService;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.model.Projection;

@Path("projections")
public class ProjectionController {
    @Inject
    ProjectionService projectionService;

    @GET
    @Path("/{movieId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjections(@PathParam("movieId") int movieId) {
        List<Projection> projections = null;
        
        try {
            projections = projectionService.getProjectionsByMovie(movieId);
        } catch (NumberFormatException | IOException | RESPError e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        
        if(projections == null){
            return Response.noContent().build();
        }
         
        return Response.ok(projections).build();
    }
       
}
