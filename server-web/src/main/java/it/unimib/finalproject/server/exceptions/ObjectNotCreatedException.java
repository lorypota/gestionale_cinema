package it.unimib.finalproject.server.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response;

public class ObjectNotCreatedException extends WebApplicationException {
    public ObjectNotCreatedException(){
        super(Response.status(Status.BAD_REQUEST).build());
    }
    public ObjectNotCreatedException(String message){
        super(message, Response.status(Status.BAD_REQUEST).build());
    }
}
