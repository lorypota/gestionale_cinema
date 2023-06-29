package it.unimib.finalproject.server.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response;

public class NoContentResponseException extends WebApplicationException{
    public NoContentResponseException(){
        super(Response.status(Status.NO_CONTENT).build());
    }
    public NoContentResponseException(String message){
        super(message, Response.status(Status.NO_CONTENT).build());
    }
}
