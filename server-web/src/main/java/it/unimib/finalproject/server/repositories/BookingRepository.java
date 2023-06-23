package it.unimib.finalproject.server.repositories;

import java.io.IOException;
import java.net.UnknownHostException;

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
            int id = dbConnector.incr("bookings_id");
            int created = dbConnector.;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (RESPError e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String  ping(){
        try {
            return dbConnector.ping();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "null";
    }
    
}
