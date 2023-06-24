package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.model.Booking;
import it.unimib.finalproject.server.utils.dbclient.DbConnector;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class BookingRepository {
    @Inject
    DbConnector dbConnector;

    @Inject
    JsonMapper mapper;

    public int createBooking(Booking booking) {
        try {
            String jsonBooking = mapper.writeValueAsString(booking);

            int id = dbConnector.incr("bookings_id");
            int created = dbConnector.hset("movies", "" + id, jsonBooking);

            System.out.println(created);
            System.out.println(id);

            getBookings();
        } catch (IOException | NumberFormatException | RESPError e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Booking[] getBookings() throws NumberFormatException, IOException, RESPError {
        var bookingsStrings = this.dbConnector.hvals("bookings");
        return Stream.of(bookingsStrings).map(s -> {
            try {
                return mapper.readValue(s, Booking.class);
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).toArray(Booking[]::new);
    }
}
