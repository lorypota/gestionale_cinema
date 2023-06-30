package it.unimib.finalproject.server.utils.helpers;

import java.util.List;

import it.unimib.finalproject.server.repositories.HallRepository;
import it.unimib.finalproject.server.model.domain.Hall;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class HallHelper {
    @Inject
    HallRepository hallRepository;

    public boolean exists(int myHallId) {
        List<Hall> halls = hallRepository.getAllHalls();
        for(Hall hall: halls){
            if(hall.getId() == myHallId) return true;
        }

        return false;
    }
}
