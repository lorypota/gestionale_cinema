package it.unimib.finalproject.server.service;

import java.util.List;

import it.unimib.finalproject.server.exceptions.DuplicatedObjectResponseError;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.repositories.BookingRepository;

import it.unimib.finalproject.server.repositories.HallRepository;
import it.unimib.finalproject.server.model.domain.Projection;
import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.utils.CustomMapper;
import it.unimib.finalproject.server.model.domain.Hall;
import it.unimib.finalproject.server.model.domain.Seat;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class BookingService {
    @Inject
    ProjectionService projectionService;

    @Inject
    SeatsService seatsService;

    @Inject
    BookingRepository bookingRepository;

    @Inject
    HallRepository hallRepository;

    public Booking createBooking(String body){
        //mapping the json body to a booking object
        CustomMapper objectMapper = new CustomMapper();
        Booking booking = objectMapper.mapBooking(body);

        //sets the id to -1 to avoid not checking the booking with id 0
        booking.setId(-1);

        //checks if the seats are still available/not wrong
        validateSeats(booking);

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
        if(exists(bookingId)){
            //mapping the json body to a booking object
            CustomMapper objectMapper = new CustomMapper();
            Booking booking = objectMapper.mapBooking(body);
            booking.setId(bookingId);

            //checks if the seats are still available/not wrong
            validateSeats(booking);

            int created =  bookingRepository.updateBooking(bookingId, body);
            if(created == DatabaseStatus.OBJECT_CREATED)
                return created;
        }

        return -1;  
    }

    public int deleteBooking(int bookingId) {
        //Retrieve the booking with the specified ID from the database
        //Throws an error if does not exist
        Booking booking = getBooking(bookingId);
        
        //Deletes the booking
        return bookingRepository.deleteBooking(booking.getId());
    }

    private boolean areSeatsValid(List<Seat> seats, int proj_id){
        Projection projection = projectionService.getProjectionById(proj_id);
        
        int hallId = projection.getHall_id();
        Hall hall = hallRepository.getHallById(hallId);

        if(hall == null){
            throw new NotFoundResponseException();
        }

        //checks if the seats are valid by confronting the hall's size with the seats' position
        for(Seat seat: seats){
            if(seat.getColumn() > hall.getColumns() || seat.getRow() > hall.getRows() || seat.getRow() < 1 || seat.getColumn() < 1)
                return false;
        }
        return true;
    }
    
    private boolean areSeatsAvailable(int proj_id, List<Seat> seats, int bookingId){
        List<Seat> bookedSeats = seatsService.getProjectionSeats(proj_id, bookingId);
  
        for(Seat seat: bookedSeats)
            for(Seat mySeats: seats)
                if(seat.equals(mySeats)) return false;
                        
        return true;
    }

    public boolean exists(int myBooking) {
        List<Booking> bookings = bookingRepository.getBookings();
        for(Booking booking: bookings){
            if(booking.getId() == myBooking) return true;
        }

        return false;
    }

    private void validateSeats(Booking booking){
        //checks if the row and the column of the seats are valid:
        //row and column are valid when they are smaller than the hall's row and column
        if(!areSeatsValid(booking.getSeats(), booking.getProj_id()))
            throw new BadRequestResponseException("seats are not valid.");
        
        //checks if the seats are still available
        if(!areSeatsAvailable(booking.getProj_id(), booking.getSeats(), booking.getId()))
            throw new DuplicatedObjectResponseError("seats are no longer available.");
    }
}
