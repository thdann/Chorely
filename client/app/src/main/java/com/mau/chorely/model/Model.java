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
import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.TransferList;
import shared.transferable.Transferable;

import com.mau.chorely.activities.utils.BridgeInstances;
import com.mau.chorely.activities.utils.Presenter;
import com.mau.chorely.model.persistentStorage.PersistentStorage;
import com.mau.chorely.model.utils.InvalidRequestIDException;
import com.mau.chorely.model.utils.ResponseHandler;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;


public class Model implements NetworkListener{

    public static final int COMMAND_ELEMENT = 0;
    public static final int ID_ELEMENT = 1;
    private LinkedBlockingDeque<Message> taskToHandle = new LinkedBlockingDeque<>();
    private ClientNetworkManager network;
    private Thread modelThread = new Thread(new ModelThread());
    private ErrorMessage errorMessage;
    private PersistentStorage storage = new PersistentStorage();
    private Model model;
    public Model(){
        System.out.println("Model created");
        //network = new ClientNetworkManager(this);
        modelThread.start();
        model = this;
    }

    /**
     * Method to compile an error task for the main thread.
     * @param message String message.
     */

    //TODO släng skiten
    public void modelError(String message){
       ErrorMessage errorMessage = new ErrorMessage(message);
       ArrayList<Transferable> errorList = new ArrayList<>();
       errorList.add(NetCommands.internalClientError);
       errorList.add(errorMessage);
       notify(errorList);
    }

    /** TODO släng skiten
     * Overridden error task method, to take exception instead of string.
     * @param exception Error message.
     */
    private void modelError(Exception exception){
       ErrorMessage errorMessage = new ErrorMessage(exception);
       ArrayList<Transferable> errorList = new ArrayList<>();
       errorList.add(NetCommands.internalClientError);
       errorList.add(errorMessage);
       //notify(errorList);


    }

    public Group[] getGroups(){
        return null;
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
    public  void notify(Message msg) {
        try {
            taskToHandle.put(msg);

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
    public synchronized NetCommands notifyForResponse(Message transferred){
        try {
            taskToHandle.put(transferred);
            ResponseHandler threadLockObject = new ResponseHandler();
            return threadLockObject.waitForResponse(transferred); //Blocks the thread until notified.
        } catch (InterruptedException e){
            System.out.println("Exception putting in notifyForResult" + e.getMessage());
            return NetCommands.internalClientError;
        }
    }



    /**
     * Main model thread. Contains switch statement to handle all NetCommands
     */

    private class ModelThread implements Runnable{
        @Override
        public void run() {

            network = new ClientNetworkManager(model);

            while (!Thread.interrupted()){
                try {
                    Message curWorkingOn = taskToHandle.take();
                    System.out.println(curWorkingOn.getCommand());
                    NetCommands command = curWorkingOn.getCommand();
                    switch (command) {
                        case connectionStatus:
                            ResponseHandler.handleResponse(network.connectAndCheckStatus((TransferList)curWorkingOn));
                            break;
                        case register:
                            storage.updateData("/user.cho", curWorkingOn.getCommand());
                            network.sendData(curWorkingOn);
                            break;
                        case registrationOk:
                            ResponseHandler.handleResponse(curWorkingOn);
                            break;
                        case internalClientError:
                            ResponseHandler.handleResponse(curWorkingOn);
                            break;
                        default:
                            System.out.println("Unrecognized command: " + command);
                            BridgeInstances.getPresenter().toastCurrent("Hejsan!!!!");
                            ResponseHandler.handleResponse(curWorkingOn);
                    }
                } catch (InterruptedException e){
                    System.out.println("Thread interrupted in main model queue");
                } catch (InvalidRequestIDException e){
                    modelError(e);
                }
            }
        }
    }
}
