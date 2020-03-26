package com.mau.chorely.model.transferrable;

import androidx.annotation.NonNull;

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

    @NonNull
    @Override
    public String toString() {
        return getMessage();
    }
}
