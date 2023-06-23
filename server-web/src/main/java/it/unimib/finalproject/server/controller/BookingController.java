package it.unimib.finalproject.server.controller;

import jakarta.ws.rs.core.*;

import java.net.URI;
import java.net.URISyntaxException;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseExcpetion;
import it.unimib.finalproject.server.service.BookingService;
import jakarta.ws.rs.*;

public class BookingController {
    private final BookingService  bookingService = new BookingService(); 

    @POST
    @Path("/{movieId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBooking(String body) {
        try {
            int id = bookingService.createBooking(body);
        } catch (ServerErrorResponseExcpetion e) {
            
        } catch(BadRequestResponseException e){

        }

        try {
            var uri = new URI("/contacts/");

            return Response.created(uri).build();
        } catch (URISyntaxException e) {
            System.out.println(e);
            return Response.serverError().build();
        }
    }
}
