package it.unimib.finalproject.server.repositories;

import java.util.ArrayList;

import it.unimib.finalproject.server.config.DatabaseConfigs;
import it.unimib.finalproject.server.model.Movie;
import it.unimib.finalproject.server.utils.DbConnector;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;

public class MovieRepository {
    private static ArrayList<Movie> movies = new ArrayList<Movie>() {{
        add(new Movie("Io sono leggenda", "descrizione di prova"));
        add(new Movie("Megamind", "prova di descrizione"));
    }};

    public ArrayList<Movie> getMovies(){
        try {
            DbConnector dbConnector = new DbConnector(DatabaseConfigs.DATABASE_HOST, 3030);
            System.out.println("ping: ");
            System.out.println(dbConnector.incr("ciao"));
            dbConnector.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        } catch (RESPError e) {
            e.printStackTrace();
        }

        return movies;
    }
}
