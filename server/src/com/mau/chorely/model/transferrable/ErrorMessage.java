package com.mau.chorely.model.transferrable;



public class ErrorMessage implements Transferable {
    private String message;
    private Exception exception;

    public ErrorMessage(String message){
        this.message = message;
    }

    public ErrorMessage(Exception exception){
        this.exception = exception;
    }

    public String getMessage(){
        String ret ="";
        if(message != null){
            ret += message;
        }
        if(exception != null){
            ret += exception.getMessage();
        }
        return ret;
    }

    
    public String toString() {
        return getMessage();
    }
}
