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

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;


public class Model implements NetworkListener{

    public static final int COMMAND_ELEMENT = 0;
    public static final int ID_ELEMENT = 1;
    private LinkedBlockingDeque<Message> taskToHandle = new LinkedBlockingDeque<>();
    private volatile boolean isLoggedIn = false;
    private ClientNetworkManager network;
    private Thread modelThread = new Thread(new ModelThread());
    private ErrorMessage errorMessage;
    private PersistentStorage storage = new PersistentStorage();
    private Model model;
    public Model(){
        System.out.println("Model created");
        network = new ClientNetworkManager(this);
        modelThread.start();
        model = this;
    }


    public Group[] getGroups(){
        return null;
    }

    public boolean isLoggedIn(){
        return true;//isLoggedIn;
    }


    public boolean isConnected(){
        return true; //network.isConnected();
    }

    /**
     * Callback method for any request that doesn't require a response.
      * @param msg Message
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
     * Main model thread. Contains switch statement to handle all NetCommands
     */

    private class ModelThread implements Runnable{
        @Override
        public void run() {

            network = new ClientNetworkManager(model);
            BridgeInstances.getPresenter().updateCurrent();

            while (!Thread.interrupted()){
                try {
                    Message curWorkingOn = taskToHandle.take();
                    System.out.println(curWorkingOn.getCommand());
                    NetCommands command = curWorkingOn.getCommand();
                    switch (command) {
                        case connectionStatus:
                            break;
                        case register:
                            break;
                        case registrationOk:
                            isLoggedIn = true;
                            BridgeInstances.getPresenter().updateCurrent();
                            break;
                        case internalClientError:
                            BridgeInstances.getPresenter().toastCurrent("Error.");
                            break;
                        default:
                            System.out.println("Unrecognized command: " + command);
                            BridgeInstances.getPresenter().toastCurrent("Hejsan!!!!");

                    }
                } catch (InterruptedException e){
                    System.out.println("Thread interrupted in main model queue");
                }
            }
        }
    }
}
