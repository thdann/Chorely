package controller;

/**
 * ServerController handles the over all logic on the server side.
 * The class contains the main method that starts the program and makes it possible for a client
 * to obtain a connection by creating an instance of ServerNetwork.
 * version 3.0 2020-04-22
 *
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */


import model.RegisteredGroups;
import model.RegisteredUsers;

import shared.transferable.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerController implements ClientListener {

    private RegisteredUsers registeredUsers;
    private RegisteredGroups registeredGroups;
    private ServerNetwork network;
    private LinkedBlockingQueue<Message> clientTaskBuffer;
    private MessageHandler messageHandler;
    private ConcurrentHashMap<User, ClientHandler> onlineClients = new ConcurrentHashMap<>();

    public ServerController() {
        registeredUsers = new RegisteredUsers();
        registeredGroups = new RegisteredGroups();
        clientTaskBuffer = new LinkedBlockingQueue<>();
        network = new ServerNetwork(this, 6583);
        messageHandler = new MessageHandler();
        Thread t1 = new Thread(messageHandler);
        t1.start();
    }

    @Override
    public void sendMessage(Message msg) {
        clientTaskBuffer.add(msg);

    }

    public void addOnlineClient(User user, ClientHandler client) {

        User userFromDisk = registeredUsers.getUserFromFile(user);
        ArrayList<GenericID> groupMemberships = userFromDisk.getGroups();

        for (GenericID id : groupMemberships) {
            Group group = registeredGroups.getGroupByID(id);
            ArrayList<Transferable> data = new ArrayList<>();
            data.add(group);
            Message message = new Message(NetCommands.updateGroup, user, data);
            sendReply(message);

        onlineClients.put(user, client);
        User rebellUser = registeredUsers.getUserFromFile(user);
        if (rebellUser != null) {
            if (rebellUser.getGroups() != null) {
                ArrayList<GenericID> groupMemberships = rebellUser.getGroups();

                for (GenericID id : groupMemberships) {
                    Group group = registeredGroups.getGroupByID(id);
                    ArrayList<Transferable> data = new ArrayList<>();
                    data.add(group);
                    Message message = new Message(NetCommands.updateGroup, user, data);
                    sendReply(message);
                }
            }

        }
    }

    public void removeOnlineClient(User user) {
        onlineClients.remove(user);
    }

    public void sendReply(Message reply) {
        ClientHandler client = onlineClients.get(reply.getUser());
        if (client != null) {
            client.addToOutgoingMessages(reply);
        }
    }

    /**
     * Notifies all the members of a group when a change is made in the group.
     * @param group
     */

    public void notifyGroupChanges(Group group) {
        ArrayList<User> members = group.getUsers();
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(group);

        for (User u : members) {
            Message message = new Message(NetCommands.updateGroup, u, data);
            sendReply(message);
        }
    }

    /**
     * Handles the incoming messages from the client
     * @param msg is the incoming message object
     */

    public void handleClientTask(Message msg) {
        NetCommands command = msg.getCommand();
        User user = msg.getUser();

        switch (command) {
            case registerUser:
                registerUser(msg);
                break;
            case registerNewGroup:
                registerNewGroup(msg);
                break;
            case updateGroup:
                updateGroupMembers(msg);
                break;
            case searchForUser:
                searchForUser(msg);
            default:
                //TODO:  kod för default case. Vad kan man skriva här?
                break;
        }
    }

    /**
     * Adds the incoming user to RegisteredUsers
     *
     * @param request
     */

    public void registerUser(Message request) {
        Message reply = null;

        if (registeredUsers.userNameAvailable(request.getUser().getUsername())) {
            if (request.getUser().getPassword() != "") {
                registeredUsers.writeUserToFile(request.getUser());
                reply = new Message(NetCommands.registrationOk, request.getUser());
                sendReply(reply);
            }
        } else {
            ErrorMessage errorMessage = new ErrorMessage("Användarnamnet är upptaget, välj ett annat.");
            reply = new Message(NetCommands.registrationDenied, request.getUser(), errorMessage);

            sendReply(reply);
            onlineClients.get(request.getUser()).throwOut();
        }
    }

    /**
     * Registers a new group to the server and updates all the members of that group with the new group membership
     *
     * @param request
     */

    public void registerNewGroup(Message request) {
        Message reply = null;
        Group group = (Group) request.getData().get(0);

        if (registeredGroups.groupIdAvailable(group.getGroupID())) {
            registeredGroups.updateGroup(group);
            ArrayList<User> members = group.getUsers();
            GenericID groupID = group.getGroupID();
            for (User u : members) {
                u.addGroupMembership(groupID);
                registeredUsers.updateUser(u);
                System.out.println( u.getUsername() + u.getGroups().get(0).getId());
            }
            reply = new Message(NetCommands.newGroupOk, request.getUser());
            sendReply(reply);
            notifyGroupChanges(group);
        } else {
            ErrorMessage errorMessage = new ErrorMessage("Registrering av grupp misslyckades.");
            reply = new Message(NetCommands.newGroupDenied, request.getUser(), errorMessage);
            sendReply(reply);
        }

    }

    /**
     * Updates the registered group with new members and updates group membership of the added or removed users.
     */

    public void updateGroupMembers(Message request) {
        Group group = (Group) request.getData().get(0);
        registeredGroups.updateGroup(group);
        updateUsersGroups(group);
        notifyGroupChanges(group);
    }

    /**
     * Updates the group membership of the added or removed users
     * @param group is the group that contains changes in members
     */

    public void updateUsersGroups(Group group) {
        ArrayList<User> members = group.getUsers();
        GenericID id = group.getGroupID();
        for (User u : members) {
            u.addGroupMembership(id);
            registeredUsers.updateUser(u);
        }
    }

    /**
     * Looks for a requested user among registered users.
     * @param request is the message object that contains the user searched for
     */

    public void searchForUser(Message request) {
        Message reply = null;
        User dummyUser = (User) request.getData().get(0);

        if (registeredUsers.findUser(dummyUser) != null) {
            User foundUser = registeredUsers.findUser(dummyUser);
            List<Transferable> data = Arrays.asList(new Transferable[]{foundUser});
            reply = new Message(NetCommands.userExists, request.getUser(), data);
        } else {
            ErrorMessage errorMessage = new ErrorMessage("Användaren finns inte");
            reply = new Message(NetCommands.userDoesNotExist, request.getUser(), errorMessage);
        }
        sendReply(reply);

    }

    /**
     * Updates registered groups with added or removed chores and rewards
     * @param request message-object containing the updated group.
     */
    public void updateGroup(Message request) {
        Group group = (Group) request.getData().get(0);
        registeredGroups.updateGroup(group);
        notifyGroupChanges(group);
    }

    /**
     * Inner class MessageHandler handles the incoming messages from the client one at a time.
     */

    private class MessageHandler implements Runnable {

        public MessageHandler() {

        }

        public void run() {
            ArrayList<Transferable> list;
            while (true) {
                try {
                    Message message = clientTaskBuffer.take();
                    handleClientTask(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

    }

}
