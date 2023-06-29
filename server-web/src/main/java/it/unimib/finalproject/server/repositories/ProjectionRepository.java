package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.model.domain.Movie;
import it.unimib.finalproject.server.model.domain.Projection;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class ProjectionRepository {
    @Inject
    DbConnector db;

    @Inject
    JsonMapper mapper;

    public List<Projection> getProjectionsByMovie(int movieId) throws NumberFormatException, IOException, RESPError {
        List<Projection> projections = getProjections();
        List<Projection> projectionOfMovie = new ArrayList<>();

        for(Projection projection: projections){
            if(projection.getMovie_id() == movieId)
                projectionOfMovie.add(projection);
        }
        return projectionOfMovie;
    }

    public Projection getProjectionById(int proj_id) throws NumberFormatException, IOException, RESPError {
        Optional<String> resp = db.hgetString("projections", ""+proj_id);

        if(!resp.isPresent() || resp.get().isEmpty())  
            return null;
        
        Projection projection = mapper.readValue(resp.get(), Projection.class);
        return projection;
    }

    public List<Projection> getProjections() throws NumberFormatException, IOException, RESPError{
        var projectionsString = this.db.hvals("projections");

        return Stream.of(projectionsString).map(s -> {
            try {
                return mapper.readValue(s, Projection.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }
    
}
