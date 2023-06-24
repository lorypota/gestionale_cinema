package it.unimib.finalproject.server.exceptions;

public class BadRequestResponseException extends Exception {
    public BadRequestResponseException(){
        super();
    }
    public BadRequestResponseException(String message){
        super(message);
    }
}
