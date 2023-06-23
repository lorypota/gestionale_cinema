package it.unimib.finalproject.server.utils;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseExcpetion;
import it.unimib.finalproject.server.model.Booking;

public class CustomMapper {

    public Booking mapBooking(String body) throws ServerErrorResponseExcpetion, BadRequestResponseException {
        try {
            var mapper = new ObjectMapper();
            Booking booking = mapper.readValue(body, Booking.class);

            // 
            if (booking.getName() == null || booking.getSurname() == null ||
                booking.getEmail() == null)
                throw new BadRequestResponseException();

        } catch (JsonParseException | JsonMappingException e) {
            System.out.println(e);
            throw new BadRequestResponseException();
        } catch (IOException e) {
            System.out.println(e);
            throw new ServerErrorResponseExcpetion();
        }

        return null;
    }
    
}
