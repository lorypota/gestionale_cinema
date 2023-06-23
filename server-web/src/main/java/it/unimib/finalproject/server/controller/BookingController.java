package it.unimib.finalproject.server.controller;

import jakarta.ws.rs.core.*;
import java.net.URI;
import java.net.URISyntaxException;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseExcpetion;
import it.unimib.finalproject.server.repositories.BookingRepository;
import it.unimib.finalproject.server.service.BookingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

@Path("booking")
public class BookingController {

    static BookingService bookingService = new BookingService();

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBooking(String body) {
        try {
            int id = bookingService.createBooking(body);
            var uri = new URI("/contacts/" + id);

            return Response.created(uri).build();
        } catch (ServerErrorResponseExcpetion e) {
            return Response.serverError().build();
        } catch(BadRequestResponseException e){
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch(URISyntaxException e){
            return Response.serverError().build();
        }
    }
}
