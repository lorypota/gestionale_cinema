package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.model.Booking;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class BookingRepository {
    @Inject
    DbConnector db;

    @Inject
    JsonMapper mapper;

    public int createBooking(Booking booking) throws NumberFormatException, IOException, 
    RESPError, ObjectNotCreatedException {
        String jsonBooking = mapper.writeValueAsString(booking);

        int id = db.incr("bookings_id");
        int created = db.hset("bookings", "" + id, jsonBooking);

        if(created == DatabaseStatus.OBJECT_NOT_CREATED)
            throw new ObjectNotCreatedException("Object not created");

        return id;
    }

    // TODO: Error handling
    public List<Booking> getBookings() throws NumberFormatException, IOException, RESPError {
        var bookingsStrings = this.db.hvals("bookings");

        return Stream.of(bookingsStrings).map(s -> {
            try {
                return mapper.readValue(s, Booking.class);
            }  catch (JsonProcessingException e) {
                return null;
            }
        }).collect(Collectors.toList());
    }

    public Booking getBookingById(int bookingId) throws NumberFormatException, IOException, RESPError {
        Optional<String> resp = db.hgetString("bookings", ""+bookingId);

        if(!resp.isPresent() || resp.get().isEmpty())  
            return null;
        
        Booking booking = mapper.readValue(resp.get(), Booking.class);
        booking.setId(bookingId);
        return booking;
    }
}
