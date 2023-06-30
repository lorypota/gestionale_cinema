package it.unimib.finalproject.server.service;

import java.util.List;

import it.unimib.finalproject.server.repositories.MovieRepository;
import it.unimib.finalproject.server.utils.CustomMapper;
import it.unimib.finalproject.server.utils.helpers.MovieHelper;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.model.domain.Movie;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class MovieService {
    @Inject
    private MovieRepository movieRepository;

    @Inject
    private MovieHelper movieHelper;
    
    public List<Movie> getAllMovies(){
        return movieRepository.getMovies();
    }

    public Movie getMovieById(int movieId){
        return movieRepository.getMovieById(movieId);
    }

    public int updateMovie(int movieId, String body) {
        if(movieHelper.exists(movieId)){
            //mapping the json body to a movie object
            CustomMapper objectMapper = new CustomMapper();
            Movie movie = objectMapper.mapMovie(body);
            movie.setId(movieId);

            int created =  movieRepository.updateMovie(movieId, body);
            if(created == DatabaseStatus.OBJECT_UPDATED)
                return created;
        }

        return -1;  
    }

    public int deleteMovie(int movieId) {
        //Retrieve the movie with the specified ID from the database
        Movie movie = movieRepository.getMovieById(movieId);

        //Throws an error if does not exist
        if(movie == null)
            throw new NotFoundResponseException("movie not found");
        
        //Deletes the movie
        return movieRepository.deleteMovie(movieId);
    }

    public Movie createMovie(String body) {
        //mapping the json body to a movie object
        CustomMapper objectMapper = new CustomMapper();
        Movie movie = objectMapper.mapMovie(body);

        //sends the request to create the movie in the database
        //returns the id of the newly created movie.
        int id = movieRepository.createMovie(movie);
        movie.setId(id);

        return movie;
    }
}
