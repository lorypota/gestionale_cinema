package it.unimib.finalproject.server.service;

import java.util.List;

import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.repositories.HallRepository;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.utils.CustomMapper;
import it.unimib.finalproject.server.utils.helpers.HallHelper;
import it.unimib.finalproject.server.model.domain.Hall;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;


@Singleton
public class HallService {
    @Inject
    HallRepository hallRepository;

    @Inject
    HallHelper hallHelper;

    public Hall getHallById(int hallId) {
        return hallRepository.getHallById(hallId);
    }

    public List<Hall> getAllHalls(){
        return hallRepository.getAllHalls();
    }

    public int updateHall(int hallId, String body) {
        if(hallHelper.exists(hallId)){
            //mapping the json body to a hall object
            CustomMapper objectMapper = new CustomMapper();
            Hall hall = objectMapper.mapHall(body);
            hall.setId(hallId);

            int created =  hallRepository.updateHall(hallId, body);
            if(created == DatabaseStatus.OBJECT_UPDATED)
                return created;
        }

        return -1;  
    }

    public int deleteHall(int hallId) {
        //Retrieve the hall with the specified ID from the database
        Hall hall = hallRepository.getHallById(hallId);

        //Throws an error if does not exist
        if(hall == null)
            throw new NotFoundResponseException("hall not found");
        
        //Deletes the hall
        return hallRepository.deleteHall(hallId);
    }

    public Hall createHall(String body) {
        //mapping the json body to a hall object
        CustomMapper objectMapper = new CustomMapper();
        Hall hall = objectMapper.mapHall(body);

        //sends the request to create the hall in the database
        //returns the id of the newly created hall.
        int id = hallRepository.createHall(hall);
        hall.setId(id);

        return hall;
    }
}
