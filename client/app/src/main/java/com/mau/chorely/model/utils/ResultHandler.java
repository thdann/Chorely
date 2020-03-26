package com.mau.chorely.model.utils;


import com.mau.chorely.model.common.NetCommands;

public class ResultHandler {
    private volatile NetCommands resultCommand;

    public synchronized NetCommands waitForResult(){
        try {
            wait();
            return resultCommand;
        } catch (InterruptedException e){
            System.out.println("Thread interrupted waiting for a result." + e.getMessage());
            return NetCommands.internalClientError;
        }
    }
    public synchronized void notifyResult(NetCommands resultCommand){
        this.resultCommand = resultCommand;
        notifyAll();
    }
}
