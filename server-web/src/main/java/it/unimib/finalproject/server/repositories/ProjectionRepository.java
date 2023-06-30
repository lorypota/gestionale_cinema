package it.unimib.finalproject.server.repositories;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.model.domain.Projection;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class ProjectionRepository {
    @Inject
    DbConnector db;

    @Inject
    JsonMapper mapper;

    public List<Projection> getProjectionsByMovie(int movieId){
        List<Projection> projections = getProjections();
        List<Projection> projectionOfMovie = new ArrayList<>();

        for(Projection projection: projections){
            if(projection.getMovie_id() == movieId)
                projectionOfMovie.add(projection);
        }
        return projectionOfMovie;
    }

    public Projection getProjectionById(int proj_id){
        Optional<String> resp;
        try {
            resp = db.hgetString("projections", ""+proj_id);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        }

        if(!resp.isPresent() || resp.get() == null || resp.get().isEmpty())  
            return null;
        
        Projection projection;
        try {
            projection = mapper.readValue(resp.get(), Projection.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestResponseException();
        }
        return projection;
    }

    public List<Projection> getProjections(){
        try {
            var projectionsString = this.db.hvals("projections");
            return Stream.of(projectionsString).map(s -> {
                try {
                    return mapper.readValue(s, Projection.class);
                } catch (JsonProcessingException e) {
                    throw new BadRequestResponseException("server couldn't parse projections");
                }
            }).collect(Collectors.toList());
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        }
    }

    public int deleteProjection(int projectionId) {
        int removed = 0;

        try {
            removed = db.hdel("projections", "" + projectionId);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        } 
        
        return removed;
    }


    public int updateProjection(int projectionId, String body) {
        int created;
        
        try {
            //adds the id to the body
            Projection projection = mapper.readValue(body, Projection.class);
            projection.setId(projectionId);
            body = mapper.writeValueAsString(projection);

            created = db.hset("projections", ""+projectionId, body);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException("error during update of movie");
        }

        return created;
    }


    public int createProjection(Projection projection) {
        try {
            int id = db.incr("projections_id");
            
            projection.setId(id);
            String jsonProjection = mapper.writeValueAsString(projection);

            int created = db.hset("projections", "" + id, jsonProjection);

            if(created == DatabaseStatus.OBJECT_NOT_CREATED)
                throw new ObjectNotCreatedException("Object not created");

            return id;
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        } 
    }
}
