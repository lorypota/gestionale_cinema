package it.unimib.finalproject.server.model;

public class Booking {
    private int proj_id;
    private String name;
    private String surname;
    private String email;
    private int row;
    private int column;
    
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
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }

}
