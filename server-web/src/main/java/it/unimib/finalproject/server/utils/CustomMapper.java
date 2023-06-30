package it.unimib.finalproject.server.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unimib.finalproject.server.exceptions.BadRequestResponseException;
import it.unimib.finalproject.server.exceptions.ServerErrorResponseException;
import it.unimib.finalproject.server.model.domain.Booking;
import it.unimib.finalproject.server.model.domain.Hall;
import it.unimib.finalproject.server.model.domain.Movie;
import it.unimib.finalproject.server.model.domain.Projection;

public class CustomMapper {

    public Booking mapBooking(String body) {
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

    public Hall mapHall(String body) {
        Hall hall;
        try {
            var mapper = new ObjectMapper();
            hall = mapper.readValue(body, Hall.class);

        } catch (JsonParseException | JsonMappingException e) {
            System.out.println(e);
            throw new BadRequestResponseException("body is not formatted correctly");
        } catch (IOException e) {
            System.out.println(e);
            throw new ServerErrorResponseException("server error");
        }

        return hall;
    }

    public Movie mapMovie(String body) {
        Movie movie;
        try {
            var mapper = new ObjectMapper();
            movie = mapper.readValue(body, Movie.class);

        } catch (JsonParseException | JsonMappingException e) {
            System.out.println(e);
            throw new BadRequestResponseException("body is not formatted correctly");
        } catch (IOException e) {
            System.out.println(e);
            throw new ServerErrorResponseException("server error");
        }

        return movie;
    }

    public Projection mapProjection(String body) {
        Projection projection;
        try {
            var mapper = new ObjectMapper();
            projection = mapper.readValue(body, Projection.class);

        } catch (JsonParseException | JsonMappingException e) {
            System.out.println(e);
            throw new BadRequestResponseException("body is not formatted correctly");
        } catch (IOException e) {
            System.out.println(e);
            throw new ServerErrorResponseException("server error");
        }

        return projection;
    }

}
