package it.unimib.finalproject.server.service;

import java.util.ArrayList;

import it.unimib.finalproject.server.model.Movie;
import it.unimib.finalproject.server.repositories.MovieRepository;

public class MovieService {
    private final MovieRepository movieRepository;
    
    public MovieService(){
        movieRepository = new MovieRepository();
    }

    public ArrayList<Movie> getAllMovies(){
        return movieRepository.getMovies();
    }
}
