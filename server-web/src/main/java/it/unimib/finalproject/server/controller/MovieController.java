package it.unimib.finalproject.server.controller;

import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.service.MovieService;
import it.unimib.finalproject.server.model.domain.Movie;

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
        movieList = movieService.getAllMovies();

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
        movie = movieService.getMovieById(movieId);

        if(movie == null){
            throw new NotFoundResponseException();
        }
         
        return Response.ok(movie).build();
    }
}
