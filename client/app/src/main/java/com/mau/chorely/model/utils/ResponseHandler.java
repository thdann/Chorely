/**
 * This class is meant to manage threads waiting for a response to a request.
 * The thread waits until the response has ben processed.
 * @version 1-0
 * @author Timothy Denison
 */

package com.mau.chorely.model.utils;


import com.mau.chorely.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import shared.transferable.NetCommands;
import shared.transferable.RequestID;
import shared.transferable.Transferable;

public class ResponseHandler {
    private volatile NetCommands resultCommand;
    private static HashMap<RequestID, ResponseHandler> threadsWaitingForResponse = new HashMap<>();


    public synchronized NetCommands waitForResponse(ArrayList<Transferable> request){
        threadsWaitingForResponse.put(((RequestID) request.get(Model.ID_ELEMENT)), this);
        try {
            wait();
            return resultCommand;
        } catch (InterruptedException e){
            System.out.println("Thread interrupted waiting for a result." + e.getMessage());
            return NetCommands.internalClientError;
        }
    }
    private synchronized void notifyResult(NetCommands resultCommand){
        this.resultCommand = resultCommand;
        notifyAll();
    }
    // TODO: 2020-03-30 Why cant method be synchronized?

    public static void handleResponse(ArrayList<Transferable> response) throws InvalidRequestIDException {
        for (int i = 0; i < 3; i++) { //loop to check for race condition 3 times before error-task is created.
            System.out.println(response.get(1).toString());
            if (threadsWaitingForResponse.containsKey(((RequestID)response.get(Model.ID_ELEMENT)))) {
                ResponseHandler waitingThread = threadsWaitingForResponse.remove(((RequestID) response.get(Model.ID_ELEMENT)));
                waitingThread.notifyResult((NetCommands) response.get(Model.COMMAND_ELEMENT));
                return;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Model thread interrupted trying to handle response");
                    return;
                }
            }
        }

        throw new InvalidRequestIDException("Response with invalid request id! Id: " + ((RequestID) response.get(Model.ID_ELEMENT)).getIdString());

    }





    // FIXME: 2020-04-01 Behöver vi någon form av errorhandling här? Det kan i så fall vara att response handler behöver referens till model.


    public static void releaseAllThreads (String message, RequestID ID){
        ArrayList<Transferable> errorList = new ArrayList<>();
        errorList.add(NetCommands.internalClientError);
        errorList.add(ID);

        for (Map.Entry<RequestID, ResponseHandler> entry : threadsWaitingForResponse.entrySet()){
            ResponseHandler thread = entry.getValue();
                thread.notifyResult((NetCommands)errorList.get(Model.COMMAND_ELEMENT));
        }
    }

}
