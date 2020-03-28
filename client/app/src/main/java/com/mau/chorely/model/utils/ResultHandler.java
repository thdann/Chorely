/**
 * This class is meant to manage threads waiting for a response to a request.
 * The thread waits until the response has ben processed.
 * @version 1-0
 * @author Timothy Denison
 */

package com.mau.chorely.model.utils;


import shared.transferable.NetCommands;

public class ResultHandler {
    private volatile NetCommands resultCommand;

    public synchronized NetCommands waitForResponse(){
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
