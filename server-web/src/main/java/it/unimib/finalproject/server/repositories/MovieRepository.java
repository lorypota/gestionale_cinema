package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import it.unimib.finalproject.server.config.DatabaseConfigs;
import it.unimib.finalproject.server.model.Movie;
import it.unimib.finalproject.server.utils.DbConnector;

public class MovieRepository {
    private static ArrayList<Movie> movies = new ArrayList<Movie>() {{
        add(new Movie("Io sono leggenda", "descrizione di prova"));
        add(new Movie("Megamind", "prova di descrizione"));
    }};

    public ArrayList<Movie> getMovies(){
        try {
            DbConnector dbConnector = new DbConnector(DatabaseConfigs.DATABASE_URL, 3030);
            System.out.println("ping: ");
            Random random = new Random();
            System.out.println(dbConnector.ping() + random.nextInt(100));
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movies;
    }
}
