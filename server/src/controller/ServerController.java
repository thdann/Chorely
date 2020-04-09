package controller;

/**
 * ServerController handles the over all logic on the server side.
 * The class contains the main method that starts the program and makes it possible for a client
 * to obtain a connection by creating an instance of ServerNetwork.
 * version 1.0 2020-03-23
 *
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */


import model.RegisteredGroups;
import model.RegisteredUsers;

import shared.transferable.*;


import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerController implements ClientListener {

    private RegisteredUsers registeredUsers;
    private RegisteredGroups registeredGroups;
    private ServerNetwork network;
    private LinkedBlockingQueue<Message> clientTaskBuffer; //TODO: här läggs alla inkommande arraylists från klienterna.
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
        onlineClients.put(user, client);
    }

    public void removeOnlineClient(User user) {
        onlineClients.remove(user);
    }

    public void sendReply(Message reply) {
        ClientHandler client = onlineClients.get(reply.getUser());
        client.addToOutgoingMessages(reply);
    }

    public void handleClientTask(Message msg) {

        //TODO: tanke - ska vi skicka replymeddelanden härifrån istället för själva metoden? För här finns ju redan usern som ska ha svaret?
        NetCommands command = msg.getCommand();
        User user = msg.getUser();

        switch (command) {
            case register:
                registerUser(user);
                break;
            case registerNewGroup:
                registerNewGroup(user, msg.getGroup());
                break;
            case addNewChore:
                addNewChore(msg.getGroup(), (Chore) msg.getData());
                break;
            case addNewReward:
                addNewReward(msg.getGroup(), (Reward) msg.getData());
                break;
            default:
                //TODO:  kod för default case. Vad kan man skriva här?
                break;
        }
    }

    /**
     * Adds the incoming user to RegisteredUsers
     *
     * @param user the new user
     */

    public void registerUser(User user) {
        Message reply = null;

        if (registeredUsers.userNameAvailable(user.getUsername())) {
            if (user.getPassword() != "") {
                registeredUsers.writeUserToFile(user);

                reply = new Message(NetCommands.registrationOk, user, new ArrayList<>());

            }
        } else {
            ErrorMessage errorMessage = new ErrorMessage("Användarnamnet är upptaget, välj ett annat.");
            reply = new Message(NetCommands.registrationDenied, user, errorMessage);

        }

        sendReply(reply);

    }

    /**
     * Registers a new group to the server and updates all the members of that group with the new group membership
     *
     * @param group the new group that was created
     */

    public void registerNewGroup(User user, Group group) {
        Message reply = null;

        if (registeredGroups.groupIdAvailable(group.getGroupID())) {
            registeredGroups.writeGroupToFile(group);
            ArrayList<User> members = group.getUsers();
            GenericID groupID = group.getGroupID();
            for (User u : members) {
                u.addGroupMembership(groupID);
            }
            reply = new Message(NetCommands.newGroupOk, user, new ArrayList<>());

        } else {
            ErrorMessage errorMessage = new ErrorMessage("Vad kan gå fel vid skapande av grupp?"); //FixMe: felmeddelandetext?
            reply = new Message(NetCommands.newGroupDenied, user, errorMessage);

        }

        sendReply(reply);

    }

    //TODO: skicka svar till klienten
    public void addNewChore(Group group, Chore chore) {
        group.addChore(chore);
        registeredGroups.updateGroup(group);
    }

    //TODO: skicka svar till klienten
    public void addNewReward(Group group, Reward reward) {
        group.addReward(reward);
        registeredGroups.updateGroup(group);
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
