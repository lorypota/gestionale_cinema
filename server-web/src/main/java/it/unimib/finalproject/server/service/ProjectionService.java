package it.unimib.finalproject.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.model.domain.Projection;
import it.unimib.finalproject.server.repositories.ProjectionRepository;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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
