package it.unimib.finalproject.server.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;

import it.unimib.finalproject.server.model.Hall;
import it.unimib.finalproject.server.model.Projection;


public class ProjectionRepository {
    private static ArrayList<Projection> projections = new ArrayList<Projection>() {{
        add(new Projection(LocalDateTime.now(), new Hall(3, 5, 5)));
        add(new Projection(LocalDateTime.now(), new Hall(2, 5, 5)));
    }};

    public ArrayList<Projection> getProjectionsByMovie(int movieId) {
        return projections;
    }
    
}
