package it.unimib.finalproject.server.service;

import java.io.IOException;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseExcpetion;
import it.unimib.finalproject.server.model.Booking;
import it.unimib.finalproject.server.repositories.BookingRepository;
import it.unimib.finalproject.server.utils.CustomMapper;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class BookingService {
    @Inject
    BookingRepository bookingRepository;

    public int createBooking(String body) throws ServerErrorResponseExcpetion, BadRequestResponseException {
        //mapping the json body to a booking object
        CustomMapper objectMapper = new CustomMapper();
        Booking booking = objectMapper.mapBooking(body);

        //sends the request to create the booking in the database
        //returns the id of the newly created booking.
        int id = bookingRepository.createBooking(booking);
        return id;
    }

    public Booking[] getBookings() throws NumberFormatException, IOException, RESPError {
        return this.bookingRepository.getBookings();
    }
    
}
