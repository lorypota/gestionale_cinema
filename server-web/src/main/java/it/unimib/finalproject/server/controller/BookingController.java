package it.unimib.finalproject.server.controller;

import java.net.URISyntaxException;
import java.util.List;
import java.net.URI;

import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.NoContentResponseException;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;

import it.unimib.finalproject.server.service.BookingService;
import it.unimib.finalproject.server.model.domain.Booking;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.GET;
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
            Booking booking = bookingService.createBooking(body);
            var uri = new URI("/bookings/" + booking.getId());

            return Response.status(Status.CREATED).entity(booking).location(uri).build();
        } catch (URISyntaxException e) {
            throw new ServerErrorResponseException();
        }
    }

    @PUT
    @Path("/{bookingId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBooking(String body, @PathParam("bookingId") int bookingId){
        int updatedBooking = bookingService.updateBooking(bookingId, body);
        
        if (updatedBooking == DatabaseStatus.OBJECT_CREATED) {
            //Object updated successfully
            return Response.noContent().build(); // 204 No Content
        } else {
            //Object not found
            throw new NotFoundResponseException();
        }
    }

    @DELETE
    @Path("/{bookingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBooking(@PathParam("bookingId") int bookingId){
        int deleted = bookingService.deleteBooking(bookingId);

        if(deleted == DatabaseStatus.OBJECT_DELETED){
            //Object deleted, server processed the request successfully
            return Response.noContent().build(); // 204 No Content
        }else{
            //Object to delete not found
            throw new NotFoundResponseException();
        }
        
    }
}
