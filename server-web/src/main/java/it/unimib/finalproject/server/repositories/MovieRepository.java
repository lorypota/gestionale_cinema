package it.unimib.finalproject.server.repositories;


import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.Optional;
import java.util.List;

import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.model.domain.Movie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;

@Singleton
public class MovieRepository {
    @Inject
    DbConnector db;

    @Inject
    JsonMapper mapper;

    public List<Movie> getMovies(){
        try {
            var moviesString = this.db.hvals("movies");
            return Stream.of(moviesString).map(s -> {
                try {
                    return mapper.readValue(s, Movie.class);
                } catch (JsonProcessingException e) {
                    throw new BadRequestResponseException("server couldn't parse movies");
                }
            }).collect(Collectors.toList());
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        }
    }

    public Movie getMovieById(int movieId){
        Optional<String> resp;
        try {
            resp = db.hgetString("movies", ""+movieId);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        } 

        if(!resp.isPresent() || resp.get() == null ||  resp.get().isEmpty())  
            return null;
        
        Movie movie;
        try {
            movie = mapper.readValue(resp.get(), Movie.class);
        } catch (JsonProcessingException e) {
            throw new ServerErrorResponseException("couldn't parse the movie");
        }

        movie.setId(movieId);
        return movie;
    }

    public int deleteMovie(int movieId) {
        int removed = 0;

        try {
            removed = db.hdel("movies", "" + movieId);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        } 
        
        return removed;
    }


    public int updateMovie(int movieId, String body) {
        int created;
        
        try {
            //adds the id to the body
            Movie movie = mapper.readValue(body, Movie.class);
            movie.setId(movieId);
            body = mapper.writeValueAsString(movie);

            created = db.hset("movies", ""+movieId, body);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException("error during update of movie");
        }

        return created;
    }


    public int createMovie(Movie movie) {
        try {
            int id = db.incr("movies_id");
            
            movie.setId(id);
            String jsonMovie = mapper.writeValueAsString(movie);

            int created = db.hset("movies", "" + id, jsonMovie);

            if(created == DatabaseStatus.OBJECT_NOT_CREATED)
                throw new ObjectNotCreatedException("Object not created");

            return id;
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        } 
    }
}
