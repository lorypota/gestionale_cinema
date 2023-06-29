package it.unimib.finalproject.server.controller;

import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.model.domain.Movie;
import it.unimib.finalproject.server.service.MovieService;

import java.io.IOException;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.*;

@Path("movies")
public class MovieController {
    @Inject
    MovieService movieService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMovies() {
        List<Movie> movieList = null;
        try {
            movieList = movieService.getAllMovies();
        } catch (NumberFormatException | IOException | RESPError e) {
            Response.serverError().build();
        }

        if(movieList == null || movieList.isEmpty()){
            Response.noContent().build();
        }
         
        return Response.ok(movieList).build();
    }

    @Path("/{movieId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieById(@PathParam("movieId") int movieId) {
        Movie movie = null;
        try {
            movie = movieService.getMovieById(movieId);
        } catch (NumberFormatException | IOException | RESPError e) {
            Response.serverError().build();
        }

        if(movie == null){
            Response.status(Response.Status.NOT_FOUND).build();
        }
         
        return Response.ok(movie).build();
    }
}
