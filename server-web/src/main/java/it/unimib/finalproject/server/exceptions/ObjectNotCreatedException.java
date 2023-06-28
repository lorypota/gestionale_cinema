package it.unimib.finalproject.server.exceptions;

public class ObjectNotCreatedException extends Exception {
     public ObjectNotCreatedException(){
        super();
    }
    public ObjectNotCreatedException(String message){
        super(message);
    }
}
