package it.unimib.finalproject.server.model.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Projection{
    private int id;
    private int hall_id;
    private int movie_id;
    private LocalDate date;
    private LocalTime timetable;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHall_id() {
        return hall_id;
    }

    public void setHall_id(int hall_id) {
        this.hall_id = hall_id;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getDate() {
        return this.date.toString();
    }

    public void setDate(String date) {
        this.date = LocalDate.parse(date);
    }

    public String getTimetable() {
        return this.timetable.toString();
    }

    public void setTimetable(String timetable) {
        this.timetable = LocalTime.parse(timetable);
    }  

    public LocalDateTime buildProjectionDateTime(){
        return LocalDateTime.of(date, timetable);
    }
}
