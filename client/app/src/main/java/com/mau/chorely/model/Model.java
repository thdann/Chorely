
package com.mau.chorely.model;

import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.User;

import com.mau.chorely.activities.utils.BridgeInstances;
import com.mau.chorely.model.persistentStorage.PersistentStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This class is a bit of a mix between a controller and a model class.
 * It will together with the different activities hold most, if not all business logic.
 * The focus of the class is to recieve requests from both the activities, and the network, in the
 * form of arraylists with a netCommand on index 0.
 * <p>
 * When a request is made from an activity, the model will block the thread from which the request was
 * sent, until it has received a response from the network, and the data is ready to be fetched.
 * <p>
 * There is also an unfinished implementation of error-handling, where all blocked threads will be released.
 *
 * @author Timothy Denison
 * @version 1.0
 */
public class Model {
    private LinkedBlockingDeque<Message> taskToHandle = new LinkedBlockingDeque<>();
    private volatile boolean isLoggedIn = false;
    private volatile boolean isConnected = false;
    private PersistentStorage storage;
    private ClientNetworkManager network;
    private Thread modelThread = new Thread(new ModelThread()); //TODO ändra konstruktion

    private Model(){};
    public Model(File filesDir) {
        network = new ClientNetworkManager(this);
        modelThread.start();
        storage = new PersistentStorage(filesDir);
    }

    public User getUser() {
        return null;
    }

    public ArrayList<Group> getGroups() {
        return null;
    }

    public boolean isLoggedIn() {
        return storage.getUser() != null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Callback method for any request that doesn't require a response.
     *
     * @param msg Message
     */

    public void handleTask(Message msg) {
        try {
            taskToHandle.put(msg);
        } catch (InterruptedException e) {
            System.out.println("Error in model callback" + e.getMessage());
        }
    }

    /**
     * Main model thread. Contains switch statement to handle all NetCommands
     */
    private class ModelThread implements Runnable {
        @Override
        public void run() {

            while (!Thread.interrupted()) {
                try {
                    System.out.println("Model is blocking for new message");
                    Message currentTask = taskToHandle.take();
                    System.out.println(currentTask.getCommand());
                    NetCommands command = currentTask.getCommand();

                    switch (command) {
                        case registerUser:
                            network.sendMessage(currentTask);
                            break;

                        case registrationOk:
                            isLoggedIn = true;
                            storage.updateUser(currentTask.getUser());
                            BridgeInstances.getPresenter().updateCurrent();
                            break;

                        case internalClientError:
                            BridgeInstances.getPresenter().toastCurrent("Error.");
                            break;

                        case connectionFailed:
                            isConnected = false;
                            BridgeInstances.getPresenter().updateCurrent();
                            BridgeInstances.getPresenter().toastCurrent("Reconnecting to server.");
                            break;

                        case connected:
                            isConnected = true;
                            BridgeInstances.getPresenter().updateCurrent();
                            break;

                        case registrationDenied:
                            BridgeInstances.getPresenter().updateCurrent();
                            BridgeInstances.getPresenter().toastCurrent(currentTask.getErrorMessage().getMessage());
                            break;

                        default:
                            System.out.println("Unrecognized command: " + command);
                            BridgeInstances.getPresenter().toastCurrent("Unknown command: " +command);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted in main model queue");
                }
            }
        }
    }
}
