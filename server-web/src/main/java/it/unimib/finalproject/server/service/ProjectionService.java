package it.unimib.finalproject.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.repositories.ProjectionRepository;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import it.unimib.finalproject.server.model.Projection;
@Singleton
public class ProjectionService {
    @Inject
    ProjectionRepository projectionRepository;
    
    public List<Projection> getProjectionsByMovie(int movieId) throws NumberFormatException, IOException, RESPError{
        return projectionRepository.getProjectionsByMovie(movieId);
    }
}
