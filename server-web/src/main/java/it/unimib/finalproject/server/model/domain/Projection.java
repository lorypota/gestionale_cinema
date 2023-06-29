package it.unimib.finalproject.server.model.domain;

import java.time.LocalDateTime;

/*
 * "id": 1,
    "hall_id": 1,
    "movie_id": 1,
    "date": "2023-06-26",
    "timetable": "20:30"
 */
public class Projection{
    private int id;
    private int hall_id;
    private int movie_id;
    private String date;
    private String timetable;
    
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
        return date;
    }

    public void setDate(String date) {
        //TODO: add logic to convert date to DateTime
        this.date = date;
    }

    public String getTimetable() {
        return timetable;
    }

    public void setTimetable(String timetable) {
        //TODO: add logic to convert date to TimeTable
        this.timetable = timetable;
    }  
}
