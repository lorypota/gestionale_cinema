package it.unimib.finalproject.server.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.unimib.finalproject.server.repositories.ProjectionRepository;
import it.unimib.finalproject.server.model.domain.Projection;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class ProjectionService {
    @Inject
    ProjectionRepository projectionRepository;
    
    public List<Projection> getProjectionsByMovie(int movieId){
        return projectionRepository.getProjectionsByMovie(movieId);
    }

    public List<Projection> getProjections() {
        List<Projection> projections = projectionRepository.getProjections();
        List<Projection> newProjections = new ArrayList<>();

        //logic to discard already projected movies
        LocalDateTime currentDateTime = LocalDateTime.now();
        for(Projection projection: projections){
            LocalDateTime projectionDateTime = projection.buildProjectionDateTime();
            if(projectionDateTime.isAfter(currentDateTime))
                newProjections.add(projection);
        }

        return newProjections;
    }

    public Projection getProjectionById(int projectionId){
        return projectionRepository.getProjectionById(projectionId);
    }
}
