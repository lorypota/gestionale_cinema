package it.unimib.finalproject.server.repositories;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.Optional;
import java.util.List;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;


import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.NotFoundResponseException;
import it.unimib.finalproject.server.exceptions.ObjectNotCreatedException;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.config.DatabaseStatus;
import it.unimib.finalproject.server.model.domain.Booking;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

@Singleton
public class BookingRepository {
    @Inject
    DbConnector db;

    @Inject
    JsonMapper mapper;

    public int createBooking(Booking booking){
        try {
            int id = db.incr("bookings_id");
            
            booking.setId(id);
            String jsonBooking = mapper.writeValueAsString(booking);

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
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException();
        } 

        if(!resp.isPresent() || resp.get().isEmpty())  
            throw new NotFoundResponseException();
        
        Booking booking;
        try {
            booking = mapper.readValue(resp.get(), Booking.class);
        } catch (JsonProcessingException e) {
            System.out.println(resp.get());
            e.printStackTrace();
            throw new ServerErrorResponseException("server couldn't parse the booking");
        }
        booking.setId(bookingId);
        return booking;
    }

    public int updateBooking(int bookingId, String body) {
        int created;
        
        try {
            //adds the id to the body
            Booking booking = mapper.readValue(body, Booking.class);
            booking.setId(bookingId);
            body = mapper.writeValueAsString(booking);

            created = db.hset("bookings", ""+bookingId, body);
        } catch (NumberFormatException | IOException | RESPError e) {
            throw new ServerErrorResponseException("error during update of booking");
        }

        return created;
    }

    public int deleteBooking(int bookingId) {
        int removed = 0;

        try {
            removed = db.hdel("bookings", "" + bookingId);
        } catch (NumberFormatException | IOException e) {
            throw new ServerErrorResponseException();
        } catch (RESPError e) {
            throw new NotFoundResponseException();
        }

        return removed;
    }
}
