package it.unimib.finalproject.server.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseExcpetion;
import it.unimib.finalproject.server.model.Booking;
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
        try{
            booking = bookingService.getBooking(bookingId);
        }catch(NumberFormatException | IOException | RESPError e){
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if(booking == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(booking).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookings(){
        List<Booking> bookings = null;
        try{
            bookings = bookingService.getBookings();
        }catch(NumberFormatException | IOException | RESPError e){
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if(bookings == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        
        if(bookings.isEmpty())
            return Response.status(Response.Status.NO_CONTENT).build();

        return Response.ok(bookings).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBooking(String body) {
        try {
            int id = bookingService.createBooking(body);
            var uri = new URI("/contacts/" + id);

            return Response.created(uri).build();
        } catch (ServerErrorResponseExcpetion | URISyntaxException e) {
            return Response.serverError().build();
        } catch (BadRequestResponseException | NumberFormatException | IOException | RESPError | ObjectNotCreatedException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
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
