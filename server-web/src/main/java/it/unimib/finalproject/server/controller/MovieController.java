package it.unimib.finalproject.server.controller;

import it.unimib.finalproject.server.service.MovieService;
import it.unimib.finalproject.server.model.Movie;

import java.util.List;

import jakarta.ws.rs.core.*;
import jakarta.ws.rs.*;

@Path("movies")
public class MovieController {
    private final MovieService movieService = new MovieService();

    /**
     * Implementazione di GET "/movies".
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContacts(@PathParam("username") String username, @PathParam("number") int number) {
        List<Movie> lista = movieService.getAllMovies();

        if(lista == null){
            Response.noContent().build();
        }
         
        return Response.ok(lista).build();
    }
}
