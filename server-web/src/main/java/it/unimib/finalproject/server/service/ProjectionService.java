package it.unimib.finalproject.server.service;

import java.util.ArrayList;

import it.unimib.finalproject.server.repositories.ProjectionRepository;
import it.unimib.finalproject.server.model.Projection;

public class ProjectionService {
    private final ProjectionRepository projectionRepository;
    
    public ProjectionService(){
        projectionRepository = new ProjectionRepository();
    }
    
    public ArrayList<Projection> getProjectionsByMovie(int movieId){
        ArrayList<Projection> list = projectionRepository.getProjectionsByMovie(movieId);

        return list;
    }
}
