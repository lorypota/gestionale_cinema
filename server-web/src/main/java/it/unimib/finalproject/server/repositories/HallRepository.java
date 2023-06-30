package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.model.domain.Hall;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class HallRepository {
    @Inject
    DbConnector db;

    @Inject
    JsonMapper mapper;

    public Hall getHallById(int hallId){
        Optional<String> resp;
        try {
            resp = db.hgetString("halls", ""+hallId);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        } 

        if(!resp.isPresent() || resp.get() == null || resp.get().isEmpty())  
            return null;
        
        Hall hall;
        try {
            hall = mapper.readValue(resp.get(), Hall.class);
        } catch (JsonProcessingException e) {
            throw new ServerErrorResponseException("Couldn't parse the hall");
        }
        return hall;
    }
    

    public List<Hall> getAllHalls(){
        try {
            var hallsString = this.db.hvals("halls");
            return Stream.of(hallsString).map(s -> {
                try {
                    return mapper.readValue(s, Hall.class);
                } catch (JsonProcessingException e) {
                    throw new BadRequestResponseException("server couldn't parse halls");
                }
            }).collect(Collectors.toList());
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        }
    }


    public int deleteHall(int hallId) {
        int removed = 0;

        try {
            removed = db.hdel("halls", "" + hallId);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        } 
        
        return removed;
    }


    public int updateHall(int hallId, String body) {
        int created;
        
        try {
            //adds the id to the body
            Hall hall = mapper.readValue(body, Hall.class);
            hall.setId(hallId);
            body = mapper.writeValueAsString(hall);

            created = db.hset("halls", ""+hallId, body);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException("error during update of hall");
        }

        return created;
    }


    public int createHall(Hall hall) {
        try {
            int id = db.incr("halls_id");
            
            hall.setId(id);
            String jsonHall = mapper.writeValueAsString(hall);

            int created = db.hset("halls", "" + id, jsonHall);

            if(created == DatabaseStatus.OBJECT_NOT_CREATED)
                throw new ObjectNotCreatedException("Object not created");

            return id;
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        } 
    }
}
