package it.unimib.finalproject.server.service;

import java.io.IOException;
import java.util.List;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.model.domain.Hall;
import it.unimib.finalproject.server.model.domain.Projection;
import it.unimib.finalproject.server.model.domain.Seat;
import it.unimib.finalproject.server.repositories.BookingRepository;
import it.unimib.finalproject.server.repositories.HallRepository;
import it.unimib.finalproject.server.repositories.ProjectionRepository;
import it.unimib.finalproject.server.utils.CustomMapper;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

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

    public int createBooking(String body) throws ServerErrorResponseException, BadRequestResponseException, 
    NumberFormatException, IOException, ObjectNotCreatedException, RESPError {
        //mapping the json body to a booking object
        CustomMapper objectMapper = new CustomMapper();
        Booking booking = objectMapper.mapBooking(body);

        //checks if the row and the column of the seats are valid:
        //row and column are valid when they are smaller than the hall's row and column
        if(!areSeatsValid(booking.getSeats(), booking.getProj_id()))
            throw new BadRequestResponseException("seats are not valid.");
        
            //checks if the seats are still available
        if(!areSeatsAvailable(booking.getProj_id(), booking.getSeats()))
            throw new BadRequestResponseException("seats are no longer available.");

        //sends the request to create the booking in the database
        //returns the id of the newly created booking.
        int id = bookingRepository.createBooking(booking);
        return id;
    }

    public List<Booking> getBookings() throws NumberFormatException, IOException, RESPError {
        var list = this.bookingRepository.getBookings();
        list.sort((a,b) -> a.getId() < b.getId() ? -1 : 1);
        return list;
    }

    public Booking getBooking(int bookingId) throws NumberFormatException, IOException, RESPError {
        Booking booking = bookingRepository.getBookingById(bookingId);
        return booking;
    }

    private boolean areSeatsValid(List<Seat> seats, int proj_id) throws NumberFormatException, IOException, RESPError {
        Projection projection = projectionService.getProjectionById(proj_id);
        
        int hallId = projection.getHall_id();
        Hall hall = hallRepository.getHallById(hallId);

        //checks if the seats are valid by confronting the hall's size with the seats' position
        for(Seat seat: seats){
            if(seat.getColumn() > hall.getColumns() || seat.getRow() > hall.getRows())
                return false;
        }
        return true;
    }
    
    private boolean areSeatsAvailable(int proj_id, List<Seat> seats) throws NumberFormatException, IOException, RESPError{
        List<Seat> bookedSeats = seatsService.getProjectionSeats(proj_id);
  
        for(Seat seat: bookedSeats)
            for(Seat mySeats: seats)
                if(seat.equals(mySeats)) return false;
                        
        return true;
    }

    public Booking updateBooking(String body) {
        //TODO: check if the booking exists

        //TODO: update the booking

        //TODO: return the booking
        return null;
    }
}
