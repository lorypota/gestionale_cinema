package it.unimib.finalproject.server.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseExcpetion;
import it.unimib.finalproject.server.model.Booking;
import it.unimib.finalproject.server.service.BookingService;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("bookings")
public class BookingController {
    @Inject
    BookingService bookingService;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> getBookings() throws NumberFormatException, IOException, RESPError {

        throw new RESPError("sex");

        // return bookingService.getBookings();
    }

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
        } catch (BadRequestResponseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (URISyntaxException e) {
            return Response.serverError().build();
        }
    }

}
