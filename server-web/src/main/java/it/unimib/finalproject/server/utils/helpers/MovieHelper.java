package it.unimib.finalproject.server.utils.helpers;

import java.util.List;

import it.unimib.finalproject.server.repositories.MovieRepository;
import it.unimib.finalproject.server.model.domain.Movie;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MovieHelper {
    @Inject
    MovieRepository movieRepository;

    public boolean exists(int myMovieId) {
        List<Movie> movies = movieRepository.getMovies();
        for(Movie movie: movies){
            if(movie.getId() == myMovieId) return true;
        }

        return false;
    }
}
