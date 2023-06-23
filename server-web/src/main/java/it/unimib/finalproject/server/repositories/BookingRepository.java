package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.net.UnknownHostException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.unimib.finalproject.server.model.Booking;
import it.unimib.finalproject.server.utils.DbConnector;
import it.unimib.finalproject.server.utils.dbclient.resp.types.RESPError;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Response;

@Singleton
public class BookingRepository {
    @Inject
     DbConnector dbConnector;

    public BookingRepository(){
        try {
            dbConnector = new DbConnector();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int createBooking(Booking booking) {
        try {
            var mapper = new JsonMapper();
            String jsonBooking = mapper.writeValueAsString(booking);

            int id = dbConnector.incr("bookings_id");
            int created = dbConnector.hset("movies", ""+id, jsonBooking);
            
            System.out.println(created);
            System.out.println(id);

            getBookings(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (RESPError e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public Booking getBookings(int id){
        try {
            System.out.print(dbConnector.getString(""+id));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
}
