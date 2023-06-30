package it.unimib.finalproject.server.service;

import java.util.List;

import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.repositories.BookingRepository;

import it.unimib.finalproject.server.repositories.HallRepository;
import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.utils.CustomMapper;
import it.unimib.finalproject.server.utils.helpers.BookingHelper;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class BookingService {
    @Inject
    BookingRepository bookingRepository;

    @Inject
    BookingHelper bookingHelper;

    public Booking createBooking(String body){
        //mapping the json body to a booking object
        CustomMapper objectMapper = new CustomMapper();
        Booking booking = objectMapper.mapBooking(body);

        //sets the id to -1 to avoid not checking the booking with id 0
        booking.setId(-1);

        //checks if the seats are still available/not wrong
        bookingHelper.validateSeats(booking);

        //sends the request to create the booking in the database
        //returns the id of the newly created booking.
        int id = bookingRepository.createBooking(booking);
        booking.setId(id);

        return booking;
    }

    public List<Booking> getBookings(){
        var list = this.bookingRepository.getBookings();
        list.sort((a,b) -> a.getId() < b.getId() ? -1 : 1);
        return list;
    }

    public Booking getBooking(int bookingId){
        Booking booking = bookingRepository.getBookingById(bookingId);
        return booking;
    }

    public int updateBooking(int bookingId, String body) {
        if(bookingHelper.exists(bookingId)){
            //mapping the json body to a booking object
            CustomMapper objectMapper = new CustomMapper();
            Booking booking = objectMapper.mapBooking(body);
            booking.setId(bookingId);

            //checks if the seats are still available/not wrong
            bookingHelper.validateSeats(booking);

            int created =  bookingRepository.updateBooking(bookingId, body);
            if(created == DatabaseStatus.OBJECT_UPDATED)
                return created;
        }

        return -1;  
    }

    public int deleteBooking(int bookingId) {
        //Retrieve the booking with the specified ID from the database
        Booking booking = bookingRepository.getBookingById(bookingId);

        //Throws an error if does not exist
        if(booking == null)
            throw new NotFoundResponseException("booking not found");
        
        //Deletes the booking
        return bookingRepository.deleteBooking(bookingId);
    }
}
