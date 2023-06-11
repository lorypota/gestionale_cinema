package it.unimib.finalproject.server.model;

import java.time.LocalDateTime;

public class Projection{
    private LocalDateTime projectionDate;
    private Hall hall;

    public Projection(LocalDateTime projectionDate, Hall hall) {
        this.projectionDate = projectionDate;
        this.hall = hall;
    }

    public String getProjectionDate() {
        return 
            projectionDate.getYear() + "-" +
            projectionDate.getMonthValue() + "-" + 
            projectionDate.getDayOfMonth() + " " +
            projectionDate.getHour() + ":" +
            projectionDate.getMinute();
    }

    public Hall getHall() {
        return hall;
    }  
}
