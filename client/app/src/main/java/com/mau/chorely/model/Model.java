/**
 * This class is a bit of a mix between a controller and a model class.
 * It will together with the different activities hold most, if not all business logic.
 * The focus of the class is to recieve requests from both the activities, and the network, in the
 * form of arraylists with a netCommand on index 0.
 *
 * When a request is made from an activity, the model will block the thread from which the request was
 * sent, until it has received a response from the network, and the data is ready to be fetched.
 *
 * There is also an unfinished implementation of error-handling, where all blocked threads will be released.
 *
 * @version 1.0
 * @author Timothy Denison
 */

package com.mau.chorely.model;


import shared.transferable.ErrorMessage;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import com.mau.chorely.model.persistentStorage.PersistentStorage;
import com.mau.chorely.model.utils.MultiMap;
import com.mau.chorely.model.utils.ResultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;


public class Model implements NetworkListener{

    private MultiMap<NetCommands, ResultHandler> threadsWaitingForResponse = new MultiMap<>();
    private HashMap<NetCommands, NetCommands> resultAndRequestPairs = new HashMap<>();
    private LinkedBlockingDeque<ArrayList<Transferable>> taskToHandle = new LinkedBlockingDeque<>();
    private ClientNetworkManager network;
    private Thread modelThread = new Thread(new ModelThread());
    private ErrorMessage errorMessage;
    private PersistentStorage storage = new PersistentStorage();

    Model(){
        System.out.println("Model created");
        network = new ClientNetworkManager(this);
        modelThread.start();
        registerCommandPairs();
    }

    /**
     *the method is the first try of an implementation of mapping of requests and responses.
     */
    private void registerCommandPairs(){
        resultAndRequestPairs.put(NetCommands.registrationOk, NetCommands.register);
        resultAndRequestPairs.put(NetCommands.registrationDenied, NetCommands.register);
    }

    public void stop(){
        network.disconnect();
        modelThread.interrupt();
    }

    /**
     * Getter for the ErrorMessage created in the case of NetCommands.internalClientError
     * @return ErrorMessage
     */
    public synchronized ErrorMessage getErrorMessage(){
        // FIXME: 2020-03-28 Det kan bli problem med att klassen bara håller ett ErrorMessage, om det är flera trådar som släpps vid internalClientError
        ErrorMessage ret = errorMessage;
        errorMessage = null;
        return ret;
    }


    /**
     * Callback method for any request that doesn't require a response.
      * @param transferred Arraylist containing NetCommand, and data.
     */

    @Override
    public  void notify(ArrayList<Transferable> transferred) {
        try {
            taskToHandle.put(transferred);

        } catch (InterruptedException e){
            System.out.println("Error in model callback" + e.getMessage());
        }
    }

    /**
     * The method is used from an activity, whenever a response to a request is required.
     * The thread is then blocked until a response has been received.
     *
     * @param transferred Arraylist with a NetCommand on index 0. Typically sent from an activity.
     * @return The method returns the response command, set by handleResponse.
     */
    public synchronized NetCommands notifyForResponse(ArrayList<Transferable> transferred){
        try {
            // FIXME: 2020-03-28 There is a race condition, if the model thread handles request and notifies the supposedly sleeping thread before the thread is sleeping.
            // FIXME: 2020-03-28 If this happens, the lock object is discarded, and the thread will never be able to wake.
            taskToHandle.put(transferred);
            ResultHandler threadLockObject = new ResultHandler();
            threadsWaitingForResponse.put(((NetCommands)transferred.get(0)), threadLockObject);
            return threadLockObject.waitForResponse(); //Blocks the thread until notified.
        } catch (InterruptedException e){
            System.out.println("Exception putting in notifyForResult" + e.getMessage());
            return NetCommands.internalClientError;
        }
    }

    /**
     * Method to handle any command that is sent as a response to a request.
     * @param responseCommand Any command sent as response.
     */
    private void handleResponse(NetCommands responseCommand){
        ArrayList<ResultHandler> waitingThreads = threadsWaitingForResponse.get(resultAndRequestPairs.get(responseCommand));
        for (ResultHandler thread : waitingThreads) {
            thread.notifyResult(responseCommand);
            System.out.println(responseCommand.toString());
        }
    }

    /**
     * First attempt at implementing error handling.
     * This is meant as a means to be able to communicate different errors to the activities, and
     * release any threads blocking for a result.
     * @param errorList Arraylist containing an error command, and a error message.
     */
    private void handleError(ArrayList<Transferable> errorList){
        errorMessage = (ErrorMessage) errorList.get(1);
        HashMap<NetCommands, ArrayList<ResultHandler>> tempMap = threadsWaitingForResponse.getHashMap();
        for (Map.Entry<NetCommands, ArrayList<ResultHandler>> entry : tempMap.entrySet()){
            ArrayList<ResultHandler> mapData = entry.getValue();
            for(ResultHandler thread : mapData){
                thread.notifyResult((NetCommands)errorList.get(0));
            }
        }
    }


    /**
     * Main model thread. Contains switch statement to handle all NetCommands
     */

    private class ModelThread implements Runnable{
        @Override
        public void run() {

            while (!Thread.interrupted()){
                System.out.println("Entering queue");
                try {
                    ArrayList<Transferable> curWorkingOn = taskToHandle.take();

                    switch ((NetCommands) curWorkingOn.get(0)) {
                        case register:
                            storage.updateData("/user.cho", curWorkingOn.get(0));
                            network.sendData(curWorkingOn);
                            break;
                        case registrationOk:
                            handleResponse((NetCommands) curWorkingOn.get(0));
                            break;
                        case internalClientError:
                            handleError(curWorkingOn);

                    }
                } catch (InterruptedException e){
                    System.out.println("Thread interrupted in main model queue");
                }
            }
        }
    }

}
