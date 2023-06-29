package it.unimib.finalproject.server.service;

import java.util.List;

import it.unimib.finalproject.server.model.domain.Hall;
import it.unimib.finalproject.server.repositories.HallRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class HallService {
    @Inject
    HallRepository hallRepository;

    public Hall getHallById(int hallId) {
        return hallRepository.getHallById(hallId);
    }

    public List<Hall> getAllHalls(){
        return hallRepository.getAllHalls();
    }
}
