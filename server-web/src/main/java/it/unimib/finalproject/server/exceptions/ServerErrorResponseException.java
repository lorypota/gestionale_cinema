package it.unimib.finalproject.server.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ServerErrorResponseException extends WebApplicationException {
    public ServerErrorResponseException(){
        super(Response.serverError().build());
    }
    public ServerErrorResponseException(String message){
        super(message, Response.serverError().build());
    }
}
