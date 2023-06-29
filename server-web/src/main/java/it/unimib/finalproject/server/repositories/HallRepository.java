package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.model.domain.Hall;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BadRequestException;

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

        if(!resp.isPresent() || resp.get().isEmpty())  
            return null;
        
        Hall hall;
        try {
            hall = mapper.readValue(resp.get(), Hall.class);
        } catch (JsonProcessingException e) {
            throw new ServerErrorResponseException("Couldn't parse the hall");
        }
        return hall;
    }
    
}
