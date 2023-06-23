package it.unimib.finalproject.server.controller;

import jakarta.ws.rs.core.*;
import jakarta.ws.rs.*;

public class BookingController {
    private final BookingService  bookingService = new BookingService(); 

    @POST
    @Path("/{movieId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjections(@PathParam("movieId") int movieId) {
        p
    }
}
