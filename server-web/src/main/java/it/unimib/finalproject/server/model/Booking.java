package it.unimib.finalproject.server.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

// TODO: Auto detect, cè da vedere come tratta id qunando lo prendi in input
// DOCS: https://github.com/FasterXML/jackson-docs

@JsonAutoDetect
public class Booking {
    private int id;
    private int proj_id;
    private String name;
    private String surname;
    private String email;
    private List<Seat> seats;

    public List<Seat> getSeats() {
        return seats;
    }
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getProj_id() {
        return proj_id;
    }
    public void setProj_id(int proj_id) {
        this.proj_id = proj_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
