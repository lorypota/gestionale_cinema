package it.unimib.finalproject.server.model;

import jakarta.ws.rs.Path;

/*
 * "id": 1,
    "name": "Pulp Fiction",
    "description": "Un intricato intreccio di storie criminali",
    "duration": "2h 34m",
    "image": "https://pad.mymovies.it/filmclub/2006/08/102/locandinapg1.jpg"
 */
@Path("movies")
public class Movie {
    private int id;
    private String name;
    private String description;
    private String duration;
    private String image;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    
}
