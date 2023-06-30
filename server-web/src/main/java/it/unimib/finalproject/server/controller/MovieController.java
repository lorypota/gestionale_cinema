package it.unimib.finalproject.server.controller;

import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.service.MovieService;
import it.unimib.finalproject.server.model.domain.Movie;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Response.Status;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMovie(String body) {
        try {
            Movie movie = movieService.createMovie(body);
            var uri = new URI("/movies/" + movie.getId());

            return Response.status(Status.CREATED).entity(movie).location(uri).build();
        } catch (URISyntaxException e) {
            throw new ServerErrorResponseException();
        }
    }


    @PUT
    @Path("/{movieId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMovie(String body, @PathParam("movieId") int movieId){
        int updatedMovie = movieService.updateMovie(movieId, body);
        
        if (updatedMovie == DatabaseStatus.OBJECT_UPDATED) {
            //Object updated successfully
            return Response.noContent().build(); // 204 No Content
        } else {
            //Object not found
            throw new NotFoundResponseException();
        }
    }

    @DELETE
    @Path("/{movieId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovie(@PathParam("movieId") int movieId){
        int deleted = movieService.deleteMovie(movieId);

        if(deleted == DatabaseStatus.OBJECT_DELETED){
            //Object deleted, server processed the request successfully
            return Response.noContent().build(); // 204 No Content
        }else{
            //Object to delete not found
            throw new NotFoundResponseException();
        }
        
    }
}
