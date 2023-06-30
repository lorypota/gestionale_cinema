package it.unimib.finalproject.server.utils.helpers;

import java.util.List;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.DuplicatedObjectResponseError;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.model.domain.Hall;
import it.unimib.finalproject.server.model.domain.Movie;
import it.unimib.finalproject.server.model.domain.Projection;
import it.unimib.finalproject.server.model.domain.Seat;
import it.unimib.finalproject.server.repositories.BookingRepository;
import it.unimib.finalproject.server.repositories.HallRepository;
import it.unimib.finalproject.server.service.ProjectionService;
import it.unimib.finalproject.server.service.SeatsService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class BookingHelper {
    @Inject
    BookingRepository bookingRepository;
    
    @Inject
    ProjectionService projectionService;

    @Inject
    HallRepository hallRepository;

    @Inject
    ProjectionHelper projectionHelper;

    @Inject 
    SeatsService seatsService;

    private boolean areSeatsValid(List<Seat> seats, int proj_id){
        Projection projection = projectionService.getProjectionById(proj_id);

        if(projection == null)
            throw new NotFoundResponseException();
        
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

    public void validateSeats(Booking booking){
        //checks if the row and the column of the seats are valid:
        //row and column are valid when they are smaller than the hall's row and column
        if(!areSeatsValid(booking.getSeats(), booking.getProj_id()))
            throw new BadRequestResponseException("seats are not valid.");
        
        //checks if the seats are still available
        if(!areSeatsAvailable(booking.getProj_id(), booking.getSeats(), booking.getId()))
            throw new DuplicatedObjectResponseError("seats are no longer available.");
    }

    public int getMovieIdFromBooking(Booking booking) {
        return projectionHelper.getMovieIdFromProjectionId(booking.getProj_id());
    }
}
