package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.model.Hall;
import it.unimib.finalproject.server.model.Projection;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class HallRepository {
    @Inject
    DbConnector db;

    @Inject
    JsonMapper mapper;

    public Hall getHallById(int hallId) throws NumberFormatException, IOException, RESPError {
        Optional<String> resp = db.hgetString("halls", ""+hallId);

        if(!resp.isPresent() || resp.get().isEmpty())  
            return null;
        
        Hall hall = mapper.readValue(resp.get(), Hall.class);
        return hall;
    }
    
}
