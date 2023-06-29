package it.unimib.finalproject.server.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.model.domain.Booking;

public class CustomMapper {

    public Booking mapBooking(String body) throws ServerErrorResponseException, BadRequestResponseException {
        Booking booking;
        try {
            var mapper = new ObjectMapper();
            booking = mapper.readValue(body, Booking.class);

            if (booking.getName() == null || booking.getSurname() == null ||
                    booking.getEmail() == null || booking.getSeats() == null)
                throw new BadRequestResponseException("body is not formatted correctly");

        } catch (JsonParseException | JsonMappingException e) {
            System.out.println(e);
            throw new BadRequestResponseException("body is not formatted correctly");
        } catch (IOException e) {
            System.out.println(e);
            throw new ServerErrorResponseException("server error");
        }

        return booking;
    }

}
