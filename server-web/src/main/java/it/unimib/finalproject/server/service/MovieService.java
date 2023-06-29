package it.unimib.finalproject.server.service;

import java.io.IOException;
import java.util.List;

import it.unimib.finalproject.server.model.domain.Movie;
import it.unimib.finalproject.server.repositories.MovieRepository;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MovieService {
    @Inject
    private MovieRepository movieRepository;
    
    public List<Movie> getAllMovies() throws NumberFormatException, IOException, RESPError{
        return movieRepository.getMovies();
    }

    public Movie getMovieById(int movieId) throws NumberFormatException, IOException, RESPError{
        return movieRepository.getMovieById(movieId);
    }
}
