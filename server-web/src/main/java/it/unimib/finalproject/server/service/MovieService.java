package it.unimib.finalproject.server.service;

import java.util.List;

import it.unimib.finalproject.server.repositories.MovieRepository;
import it.unimib.finalproject.server.model.domain.Movie;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class MovieService {
    @Inject
    private MovieRepository movieRepository;
    
    public List<Movie> getAllMovies(){
        return movieRepository.getMovies();
    }

    public Movie getMovieById(int movieId){
        return movieRepository.getMovieById(movieId);
    }
}
