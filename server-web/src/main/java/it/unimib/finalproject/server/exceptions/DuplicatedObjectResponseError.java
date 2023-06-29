package it.unimib.finalproject.server.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class DuplicatedObjectResponseError extends WebApplicationException{
    public DuplicatedObjectResponseError(){
        super(Response.status(Response.Status.CONFLICT).build());
    }
    public DuplicatedObjectResponseError(String message){
        super(message,Response.status(Response.Status.CONFLICT).build());
    }
}
