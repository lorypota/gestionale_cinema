package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.model.Booking;
import it.unimib.finalproject.server.model.Movie;

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

    public List<Movie> getMovies() throws NumberFormatException, IOException, RESPError{
        var moviesString = this.db.hvals("movies");

        return Stream.of(moviesString).map(s -> {
            try {
                return mapper.readValue(s, Movie.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    public Movie getMovieById(int movieId) throws NumberFormatException, IOException, RESPError {
        Optional<String> resp = db.hgetString("movies", ""+movieId);

        if(!resp.isPresent() || resp.get().isEmpty())  
            return null;
        
        Movie movie = mapper.readValue(resp.get(), Movie.class);
        movie.setId(movieId);
        return movie;
    }
}
