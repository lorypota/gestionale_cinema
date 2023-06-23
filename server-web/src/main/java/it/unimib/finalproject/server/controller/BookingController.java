package it.unimib.finalproject.server.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("bookings")
public class BookingController {
    @Inject
    private BookingService bookingService; 

    @GET
    @Path("{movieId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjections(@PathParam("movieId") int movieId) {
        return Response.ok(bookingService.ciao()).build();
    }
}
