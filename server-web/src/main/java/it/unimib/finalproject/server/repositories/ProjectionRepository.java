package it.unimib.finalproject.server.repositories;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
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
        } catch (NumberFormatException | IOException e) {
            throw new ServerErrorResponseException();
        }catch (RESPError e){
            throw new NotFoundResponseException();
        }

        if(!resp.isPresent() || resp.get().isEmpty())  
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
}
