package it.unimib.finalproject.server.exceptions;

public class ServerErrorResponseExcpetion extends Exception {
    public ServerErrorResponseExcpetion(){
        super();
    }
    public ServerErrorResponseExcpetion(String message){
        super(message);
    }
}
