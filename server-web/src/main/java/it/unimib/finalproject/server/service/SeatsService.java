package it.unimib.finalproject.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.model.domain.Seat;
import it.unimib.finalproject.server.repositories.BookingRepository;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class SeatsService {
    @Inject
    BookingRepository bookingRepository;

    public List<Seat> getProjectionSeats(int projectionId) throws NumberFormatException, IOException, RESPError{
        List<Booking> bookings = bookingRepository.getBookings();
        List<Seat> bookedSeats = new ArrayList<>();

        for(Booking booking: bookings){
            if(booking.getProj_id() == projectionId)
                for(Seat seat: booking.getSeats())
                    bookedSeats.add(seat);
        }

        return bookedSeats;
    }
}
