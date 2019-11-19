package kornienko.handler;

public class NonValidAccessTokenException extends Exception {
    public NonValidAccessTokenException(String message){
        super(message);
    }
}
