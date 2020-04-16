
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
 * form of messages.
 * @author Timothy Denison
 * @version 2.0
 */
public class Model {
    private LinkedBlockingDeque<Message> taskToHandle = new LinkedBlockingDeque<>();
    private volatile boolean isLoggedIn = false;
    private volatile boolean isConnected = false;
    private PersistentStorage storage;
    private ClientNetworkManager network;
    private User lastSearchedUser;
    private Thread modelThread; // TODO: 2020-04-16 Se över om vi ska stoppa den hör tråden nånstans, annars behövs ingen referens. 

    private Model(){};
    public Model(File filesDir) {
        network = new ClientNetworkManager(this);
        modelThread = new Thread(new ModelThread());
        modelThread.start();
        storage = new PersistentStorage(filesDir);
    }

    /**
     * Getter to get the stored client user.
     * @return current user.
     */
    public User getUser() {
        return storage.getUser();
    }

    /**
     * Getter to get all stored groups.
     * @return arraylist of groups.
     */
    public ArrayList<Group> getGroups() {
        return storage.getGroups();
    }

    /**
     * Method checks if there is a stored user on the client.
     * @return true if there is a stored user.
     */
    public boolean isLoggedIn() {
        return storage.getUser() != null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * This method returns a reference to the last searched user, and sets it to null.
     * @return last searched User.
     */
    public User removeLastSearchedUser(){
        User temp = lastSearchedUser;
        lastSearchedUser = null;
        return temp;
    }

    /**
     * Callback method. Puts the message in a queue to be handled by the model thread.
     * @param msg this is the task to handle, complete with a command, and data.
     */

    public void handleTask(Message msg) {
        try {
            taskToHandle.put(msg);
        } catch (InterruptedException e) {
            System.out.println("Error in model callback" + e.getMessage());
        }
    }

    /**
     * This method handles updates to existing groups on the client side.
     * @param message message containing the group to update.
     */
    private void updateGroup (Message message){
        if(storage.saveOrUpdateGroup((Group)message.getData().get(0))){
            message.setCommand(NetCommands.updateGroup);
            network.sendMessage(message);
            BridgeInstances.getPresenter().updateCurrent();
        }
        //If not, group is already up to date.
    }

    /**
     * This method handles creation of new groups on the client side.
     * @param message message containing the new group.
     */
    private void createGroup(Message message){
        if(storage.saveOrUpdateGroup((Group)message.getData().get(0))){
            network.sendMessage(message);
            BridgeInstances.getPresenter().updateCurrent();
        }
    }
    /**
     * Main model thread. This contains the main switch statement of the client, and all tasks
     * are diverted throughout the application.
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
                        case searchForUser:
                            /*Falls through*/
                        case registerUser:
                            network.sendMessage(currentTask);
                            break;

                        case clientInternalGroupUpdate:
                            updateGroup(currentTask);
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
                            BridgeInstances.getPresenter().toastCurrent("Återansluter till servern.");
                            break;

                        case connected:
                            isConnected = true;
                            BridgeInstances.getPresenter().updateCurrent();
                            break;

                        case userExists:
                            lastSearchedUser = (User) currentTask.getData().get(0);
                            BridgeInstances.getPresenter().updateCurrent();
                            BridgeInstances.getPresenter().toastCurrent("Användare hittad!");
                            break;

                        case registerNewGroup:
                            createGroup(currentTask);
                            break;

                        case registrationDenied:
                            /*Falls through*/
                        case userDoesNotExist:
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
