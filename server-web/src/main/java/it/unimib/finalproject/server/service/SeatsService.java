package it.unimib.finalproject.server.service;

import java.util.ArrayList;
import java.util.List;

import it.unimib.finalproject.server.repositories.BookingRepository;
import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.model.domain.Seat;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class SeatsService {
    @Inject
    BookingRepository bookingRepository;

    public List<Seat> getProjectionSeats(int projectionId, int bookingId){
        List<Booking> bookings = bookingRepository.getBookings();
        List<Seat> bookedSeats = new ArrayList<>();

        for(Booking booking: bookings){
            if(booking.getId() != bookingId && booking.getProj_id() == projectionId)
                for(Seat seat: booking.getSeats())
                    bookedSeats.add(seat);
        }

        return bookedSeats;
    }

    public List<Seat> getProjectionSeats(int projectionId){
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
