package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.model.domain.Movie;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;

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
                    e.printStackTrace();
                    return null;
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
        } catch (NumberFormatException | IOException e) {
            throw new ServerErrorResponseException();
        } catch (RESPError e){
            throw new NotFoundResponseException();
        }

        if(!resp.isPresent() || resp.get().isEmpty())  
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
}
