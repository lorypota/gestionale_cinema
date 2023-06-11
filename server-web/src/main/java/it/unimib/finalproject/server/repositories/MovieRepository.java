package it.unimib.finalproject.server.repositories;

import java.util.ArrayList;
import java.util.Arrays;

import it.unimib.finalproject.server.model.Movie;

public class MovieRepository {
    private static ArrayList<Movie> movies = new ArrayList<Movie>() {{
        add(new Movie("Io sono leggenda", "descrizione di prova"));
        add(new Movie("Megamind", "prova di descrizione"));
    }};

    public ArrayList<Movie> getMovies(){
        return movies;
    }
}
