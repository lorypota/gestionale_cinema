package it.unimib.finalproject.server.utils.helpers;

import java.util.List;

import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.model.domain.Movie;
import it.unimib.finalproject.server.model.domain.Projection;
import it.unimib.finalproject.server.repositories.ProjectionRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ProjectionHelper {
    @Inject
    HallHelper hallHelper;

    @Inject
    MovieHelper movieHelper;

    @Inject
    ProjectionRepository projectionRepository;

    public void validateProjection(Projection projection) {
        //check if movie id is valid
        boolean isMovieValid = isMovieValid(projection.getMovie_id());
        if(!isMovieValid)
            throw new NotFoundResponseException("movie not found");
        
        //check if hall id is valid
        boolean isHallValid = isHallValid(projection.getHall_id());
        if(!isHallValid)
            throw new NotFoundResponseException("hall not found");
    }

    public boolean exists(int myProjectionId) {
        List<Projection> projections = projectionRepository.getProjections();
        for(Projection projection: projections){
            if(projection.getId() == myProjectionId) return true;
        }

        return false;
    }

    public boolean isHallValid(int hallId){
        return hallHelper.exists(hallId);
    }

    public boolean isMovieValid(int movieId){
        return movieHelper.exists(movieId);
    }

    public int getMovieIdFromProjectionId(int proj_id) {
        Projection projection = projectionRepository.getProjectionById(proj_id);
        
        if(projection == null)
            throw new NotFoundResponseException();
        return projection.getMovie_id();
    }
}
