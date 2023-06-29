package it.unimib.finalproject.server.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class BadRequestResponseException extends WebApplicationException {
    public BadRequestResponseException(){
        super(Response.status(Response.Status.BAD_REQUEST).build());
    }
    public BadRequestResponseException(String message){
        super(message,Response.status(Response.Status.BAD_REQUEST).build());
    }
}
