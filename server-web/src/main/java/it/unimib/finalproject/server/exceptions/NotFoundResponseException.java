package it.unimib.finalproject.server.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response;

public class NotFoundResponseException extends WebApplicationException{
    public NotFoundResponseException(){
        super(Response.status(Status.NOT_FOUND).build());
    }
    public NotFoundResponseException(String message){
        super(message, Response.status(Status.NOT_FOUND).build());
    }
}
