package it.unimib.finalproject.server.service;

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

        //TODO: add logic to discard already projected movies

        return projections;
    }

    public Projection getProjectionById(int projectionId){
        return projectionRepository.getProjectionById(projectionId);
    }
}
