package it.unimib.finalproject.server.controller;

import jakarta.ws.rs.core.*;
import jakarta.ws.rs.*;

import java.util.ArrayList;
import java.util.List;

import it.unimib.finalproject.server.service.ProjectionService;
import it.unimib.finalproject.server.model.Projection;

@Path("projections")
public class ProjectionController {
    
    private final ProjectionService projectionService = new ProjectionService(); 

    @POST
    @Path("/{movieId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getProjections(@PathParam("movieId") int movieId) {
        List<Projection> lista = projectionService.getProjectionsByMovie(movieId);
        
        if(lista == null){
            Response.noContent().build();
        }
         
        return Response.ok(lista).build();
    }
       
}
