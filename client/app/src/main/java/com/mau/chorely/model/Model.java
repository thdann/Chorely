
package com.mau.chorely.model;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import shared.transferable.Chore;
import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Reward;
import shared.transferable.Transferable;
import shared.transferable.User;

import com.mau.chorely.R;
import com.mau.chorely.activities.utils.Presenter;
import com.mau.chorely.model.persistentStorage.PersistentStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This class is a bit of a mix between a controller and a model class.
 * It will together with the different activities hold most, if not all business logic.
 * The focus of the class is to recieve requests from both the activities, and the network, in the
 * form of messages.
 *
 * @author Timothy Denison, Emma Svensson, Theresa Dannberg, Johan Salomonsson, Måns Harnesk
 * @version 2.0
 */
public class Model {
    private LinkedBlockingDeque<Message> taskToHandle = new LinkedBlockingDeque<>();
    private volatile boolean isLoggedIn = false;
    private volatile boolean isConnected = false;
    private PersistentStorage storage;
    private ClientNetworkManager network;
    private User lastSearchedUser;
    private static Model model;
    private Context context;

    private Model() {
    }

    ;


    public static Model getInstance(File appFilesDir, Context context) {
        if (model == null) {
            model = new Model(appFilesDir, context);
            Exception e = new Exception("MODEL UNREFERENCED");
            e.printStackTrace();
        }
        return model;
    }

    public Model(File filesDir, Context context) {
        this.context = context;
        network = new ClientNetworkManager(this);
        Thread modelThread = new Thread(new ModelThread());
        modelThread.start();
        storage = new PersistentStorage(filesDir);
    }

    /**
     * Getter to get the stored client user.
     *
     * @return current user.
     */
    public User getUser() {
        return storage.getUser();
    }

    /**
     * Getter fpr te stored selected group.
     * @return selected group.
     */

    public Group getSelectedGroup() {
        return storage.getSelectedGroup();
    }

    /**
     * Setter for the selected group.
     * @param group group to set as selected.
     */
    public void setSelectedGroup(Group group) {
        storage.setSelectedGroup(group);
    }

    /**
     * Getter to get all stored groups.
     *
     * @return arraylist of groups.
     */
    public ArrayList<Group> getGroups() {
        return storage.getGroups();
    }

    /**
     * Method checks if there is a stored user on the client.
     *
     * @return true if there is a stored user.
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean hasStoredUser() {
        return storage.getUser() != null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * This method returns a reference to the last searched user, and sets it to null.
     *
     * @return last searched User.
     */
    public User removeLastSearchedUser() {
        User temp = lastSearchedUser;
        lastSearchedUser = null;
        return temp;
    }

    /**
     * Callback method. Puts the message in a queue to be handled by the model thread.
     *
     * @param msg this is the task to handle, complete with a command, and data.
     */
    public void handleTask(Message msg) {
        try {
            System.out.println(msg);
            taskToHandle.put(msg);
        } catch (InterruptedException e) {
            System.out.println("Error in model callback" + e.getMessage());
        }
    }

    /**
     * This method handles updates to existing groups on the client side.
     *
     * @param message message containing the group to update.
     */
    private void updateGroup(Message message) {
        Group currentGroup = (Group) message.getData().get(0);
        if (currentGroup.getUsers().contains(storage.getUser())) {

            if (storage.saveOrUpdateGroup(currentGroup)) {
                message.setCommand(NetCommands.updateGroup);
                network.sendMessage(message);
                Presenter.getInstance().updateCurrent();
            }
            //If not, group is already up to date.
        } else {
            storage.deleteGroup(currentGroup);
            Presenter.getInstance().updateCurrent();
        }
    }

    /**
     * Handles updates to groups coming from the server,
     * @param message
     */
    private void updateGroupExternal(Message message) {
        Group currentGroup = (Group) message.getData().get(0);
        if (currentGroup.getUsers().contains(storage.getUser())) {

            if (storage.saveOrUpdateGroup(currentGroup)) {
                Presenter.getInstance().updateCurrent();
            }
            //If not, group is already up to date.
        } else {
            storage.deleteGroup(currentGroup);
            Presenter.getInstance().updateCurrent();
        }
    }

    /**
     * Method to log the user in if there is a saved user from a previous session.
     */
    private void automaticLogIn() {
        if (hasStoredUser()) {
            network.sendMessage(new Message(NetCommands.login, getUser()));
        }
    }

    /**
     * Manual login method.
     * @param msg Message containing user object to login to.
     */
    private void manualLogIn(Message msg) {
        network.sendMessage(msg);

    }

    /**
     * Method to log out the user.
     * @param msg message containing the user to log out.
     */
    private void logOut(Message msg) {
        network.sendMessage(msg);
        isLoggedIn = false;
        storage.deleteAllGroups();
        storage.deleteSelectedGroup();
        storage.deleteUser();

    }

    /**
     * This method handles creation of new groups on the client side.
     *
     * @param message message containing the new group.
     */
    private void createGroup(Message message) {
        if (storage.saveOrUpdateGroup((Group) message.getData().get(0))) {
            System.out.println("SENDING NEW GROUP TO SERVER");
            network.sendMessage(message);
            Presenter.getInstance().updateCurrent();
        }
    }

    /**
     * This method updates current group with new chores.
     *
     * @param message message containing the new chore.
     */
    public void addNewChore(Message message) {
        Group group = storage.getSelectedGroup();
        Chore chore = (Chore) message.getData().get(0);
        boolean foundSame = false;
        for (Chore tempChore : group.getChores()) {
            if (tempChore.nameEquals(chore)) {
                foundSame = true;
                if (!tempChore.equals(chore)) {
                    tempChore.updateChore(chore);
                }
            }
        }
        if (!foundSame) {
            group.addChore(chore);
        }
        System.out.println("CHORE LIST SIZE: " + group.getChores().size());
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(group);
        Message newMessage = new Message(NetCommands.updateGroup, message.getUser(), data);
        updateGroup(newMessage);

    }

    /**
     * This method updates current group with new reward.
     *
     * @param message message containing the new reward.
     */
    public void addNewReward(Message message) {
        Group group = storage.getSelectedGroup();
        Reward reward = (Reward) message.getData().get(0);
        boolean foundSame = false;
        for (Reward tempReward : group.getRewards()) {
            if (tempReward.nameEquals(reward)) {
                foundSame = true;
                if (!tempReward.equals(reward)) {
                    tempReward.updateReward(reward);
                }
            }
        }
        if (!foundSame) {
            group.addReward(reward);
        }
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(group);
        Message newMessage = new Message(NetCommands.updateGroup, message.getUser(), data);
        updateGroup(newMessage);

    }

    /**
     * @Author Johan, Måns
     * Sends a notification to the Android Notification Manager and displays a notification to the user.
     *  @param currentTask The current task that triggers the notification.
     *  @return A boolean indicating whether the notification was successfully sent.
     *  @throws Exception If there was an error sending the notification.
     */
    public boolean receiveNotification(Message currentTask) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notifications")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Member Added")
                .setContentText("A new member was added to " + getGroupNameForNotification(currentTask))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId("Notifications");
        int notificationId = 2;
        try {
            notificationManager.notify(notificationId, builder.build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @Author Johan
     * Returns the name of the group in the given message.
     *
     * @param data The message that contains the data.
     * @return The name of the group, or `null` if no group was found in the data.
     */
    public String getGroupNameForNotification(Message data){
        for (int i  = 0; i < data.getData().size(); i++){
            if (data.getData().get(i) instanceof Group) {
                return ((Group) data.getData().get(i)).getName();
            }
        }

        return null;
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
                        case login:
                            manualLogIn(currentTask);
                            break;

                        case loginDenied:
                            isLoggedIn = false;
                            Presenter.getInstance().updateCurrent();
                            Presenter.getInstance().toastCurrent(currentTask.getErrorMessage().getMessage());
                            break;

                        case deleteGroup:
                            storage.deleteSelectedGroup();
                            /*Falls through*/
                        case searchForUser:
                            /*Falls through*/
                        case registerUser:
                            network.sendMessage(currentTask);
                            break;

                        case loginOk:
                            storage.updateUser(currentTask.getUser());
                            isLoggedIn = true;
                            Presenter.getInstance().updateCurrent();
                            break;

                        case registerNewGroup:
                            createGroup(currentTask);
                            break;

                        case clientInternalGroupUpdate:
                            updateGroup(currentTask);
                            break;

                        case registrationOk:
                            isLoggedIn = true;
                            storage.updateUser(currentTask.getUser());
                            Presenter.getInstance().updateCurrent();
                            break;


                        case connectionFailed:
                            isConnected = false;
                            isLoggedIn = false;
                            Presenter.getInstance().updateCurrent();
                            Presenter.getInstance().toastCurrent("Återansluter till servern.");
                            break;

                        case connected:
                            isConnected = true;
                            automaticLogIn();
                            Presenter.getInstance().updateCurrent();
                            break;

                        case userExists:
                            lastSearchedUser = (User) currentTask.getData().get(0);
                            Presenter.getInstance().updateCurrent();
                            Presenter.getInstance().toastCurrent("Användare hittad!");
                            break;

                        case registrationDenied:
                            /*Falls through*/
                        case userDoesNotExist:
                            Presenter.getInstance().updateCurrent();
                            Presenter.getInstance().toastCurrent(currentTask.getErrorMessage().getMessage());
                            break;

                        case addNewChore:
                            addNewChore(currentTask);
                            break;

                        case addNewReward:
                            addNewReward(currentTask);
                            break;

                        case updateGroup:
                            updateGroupExternal(currentTask);
                            Presenter.getInstance().updateCurrent();
                            break;

                        case newGroupOk:
                            Presenter.getInstance().updateCurrent();
                            break;

                        case logout:
                            logOut(currentTask);
                            break;

                        case notificationSent:
                            network.sendMessage(currentTask);
                            break;
                        case notificationReceived:          //@author Johan, Måns
                            receiveNotification(currentTask);
                            break;
                        default:
                            System.out.println("Unrecognized command: " + command);
                            Presenter.getInstance().toastCurrent("Unknown command: " + command);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted in main model queue");
                }
            }
        }
    }
}
