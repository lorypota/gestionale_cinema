package it.unimib.finalproject.server.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

// TODO: Auto detect, cè da vedere come tratta id qunando lo prendi in input
// DOCS: https://github.com/FasterXML/jackson-docs

@JsonAutoDetect
public class Booking {
    public int id;
    public int proj_id;
    public String name;
    public String surname;
    public String email;
    public int row;
    public int column;
}
