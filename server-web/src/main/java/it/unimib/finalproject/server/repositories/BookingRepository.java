package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.model.domain.Booking;
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

    public int createBooking(Booking booking){
        String jsonBooking;
        try {
            jsonBooking = mapper.writeValueAsString(booking);
        } catch (JsonProcessingException e) {
            throw new BadRequestResponseException();
        }

        try {
            int id = db.incr("bookings_id");
            int created = db.hset("bookings", "" + id, jsonBooking);

            if(created == DatabaseStatus.OBJECT_NOT_CREATED)
                throw new ObjectNotCreatedException("Object not created");

            return id;
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        }
    }

    public List<Booking> getBookings(){
        try {
            var bookingsStrings = this.db.hvals("bookings");
            return Stream.of(bookingsStrings).map(s -> {
                try {
                    return mapper.readValue(s, Booking.class);
                }  catch (JsonProcessingException e) {
                    throw new BadRequestResponseException("server couldn't parse bookings");
                }
            }).collect(Collectors.toList());
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        }
    }

    public Booking getBookingById(int bookingId){
        Optional<String> resp;
        try {
            resp = db.hgetString("bookings", ""+bookingId);
        } catch (NumberFormatException | IOException e) {
            throw new ServerErrorResponseException();
        } catch(RESPError e){
            throw new NotFoundResponseException();
        }

        if(!resp.isPresent() || resp.get().isEmpty())  
            throw new NotFoundResponseException();
        
        Booking booking;
        try {
            booking = mapper.readValue(resp.get(), Booking.class);
        } catch (JsonProcessingException e) {
            throw new ServerErrorResponseException("server couldn't parse the booking");
        }
        booking.setId(bookingId);
        return booking;
    }
}
