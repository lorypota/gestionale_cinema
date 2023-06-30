package it.unimib.finalproject.server.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.unimib.finalproject.server.repositories.ProjectionRepository;
import it.unimib.finalproject.server.utils.CustomMapper;
import it.unimib.finalproject.server.utils.helpers.ProjectionHelper;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.model.domain.Projection;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class ProjectionService {
    @Inject
    ProjectionRepository projectionRepository;

    @Inject
    ProjectionHelper projectionHelper;
    
    public List<Projection> getProjectionsByMovie(int movieId){
        return projectionRepository.getProjectionsByMovie(movieId);
    }

    public List<Projection> getProjections() {
        List<Projection> projections = projectionRepository.getProjections();
        List<Projection> newProjections = new ArrayList<>();

        //logic to discard already projected movies
        LocalDateTime currentDateTime = LocalDateTime.now();
        for(Projection projection: projections){
            LocalDateTime projectionDateTime = projection.buildProjectionDateTime();
            if(projectionDateTime.isAfter(currentDateTime))
                newProjections.add(projection);
        }

        return newProjections;
    }

    public Projection getProjectionById(int projectionId){
        return projectionRepository.getProjectionById(projectionId);
    }

    public int updateProjection(int projectionId, String body) {
        if(projectionHelper.exists(projectionId)){
            //mapping the json body to a projection object
            CustomMapper objectMapper = new CustomMapper();
            Projection projection = objectMapper.mapProjection(body);
            projection.setId(projectionId);

            //checks if the body of the projection is valid
            projectionHelper.validateProjection(projection);

            int created =  projectionRepository.updateProjection(projectionId, body);
            if(created == DatabaseStatus.OBJECT_UPDATED)
                return created;
        }

        return -1;  
    }

    public int deleteProjection(int projectionId) {
        //Retrieve the projection with the specified ID from the database
        Projection projection = projectionRepository.getProjectionById(projectionId);

        //Throws an error if does not exist
        if(projection == null)
            throw new NotFoundResponseException("projection not found");
        
        //Deletes the projection
        return projectionRepository.deleteProjection(projectionId);
    }

    public Projection createProjection(String body) {
        //mapping the json body to a projection object
        CustomMapper objectMapper = new CustomMapper();
        Projection projection = objectMapper.mapProjection(body);

        //checks if the body of the projection is valid
        projectionHelper.validateProjection(projection);

        //sends the request to create the projection in the database
        //returns the id of the newly created projection.
        int id = projectionRepository.createProjection(projection);
        projection.setId(id);

        return projection;
    }
}
