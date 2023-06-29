package it.unimib.finalproject.server.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class ServerErrorResponseException extends WebApplicationException {
    public ServerErrorResponseException(){
        super(Response.status(Status.INTERNAL_SERVER_ERROR).build());
    }
    public ServerErrorResponseException(String message){
        super(message, Response.status(Status.INTERNAL_SERVER_ERROR).build());
    }
}
