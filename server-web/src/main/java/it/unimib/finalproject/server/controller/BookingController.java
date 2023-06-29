package it.unimib.finalproject.server.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.NoContentResponseException;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.service.BookingService;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("bookings")
public class BookingController {
    @Inject
    BookingService bookingService;

    @GET
    @Path("/{bookingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookings(@PathParam("bookingId") int bookingId){
        Booking booking = null;
        booking = bookingService.getBooking(bookingId);

        if(booking == null)
            throw new NotFoundResponseException();

        return Response.ok(booking).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookings(){
        List<Booking> bookings = null;
        bookings = bookingService.getBookings();

        if(bookings == null)
            throw new NotFoundResponseException();
        
        if(bookings.isEmpty())
            throw new NoContentResponseException();

        return Response.ok(bookings).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBooking(String body) {
        try {
            int id = bookingService.createBooking(body);
            var uri = new URI("/contacts/" + id);

            return Response.created(uri).build();
        } catch (URISyntaxException e) {
            throw new ServerErrorResponseException();
        }
    }

    /* 
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBooking(String body){
        Booking updatedBooking = bookingService.updateBooking(body);
        return null;
    }

    @DELETE
    @Path("/{bookingId}")
    public Response deleteBooking(@PathParam("bookingId") int bookingId){
        return null;
    }*/
}
