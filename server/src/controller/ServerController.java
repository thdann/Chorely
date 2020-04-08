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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerController implements ClientListener {

    private RegisteredUsers registeredUsers;
    private RegisteredGroups registeredGroups;
    private ServerNetwork network;
    private LinkedBlockingQueue<Message> clientTaskBuffer; //TODO: här läggs alla inkommande arraylists från klienterna.
    private BetterNameComingSoon betterNameComingSoon;
    private ConcurrentHashMap<User, ClientHandler> onlineClients = new ConcurrentHashMap<>();

    public ServerController() {
        registeredUsers = new RegisteredUsers();
        registeredGroups = new RegisteredGroups();
        clientTaskBuffer = new LinkedBlockingQueue<>();
        network = new ServerNetwork(this, 6583);
        betterNameComingSoon = new BetterNameComingSoon();
        Thread t1 = new Thread(betterNameComingSoon);
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

    public void handleClientTask(Message msg) {

        NetCommands command = msg.getCommand();
        User user = msg.getUser();

        switch (command) {
            case register:
                registerUser(user);
                break;
            case registerNewGroup:
                registerNewGroup((Group) msg.getData());
                break;
            default:
                //TODO:  kod för default case. Vad kan man skriva här?
                break;
        }
    }

    public void registerUser(User user) {
        Message reply = null;

        if (registeredUsers.userNameAvailable(user.getUsername())) {
            if (user.getPassword() != "") {
                registeredUsers.addRegisteredUser(user);

                reply = new Message(NetCommands.registrationOk, user, new ArrayList<>());
                sendReply(reply);

            }
        } else {
            ErrorMessage errorMessage = new ErrorMessage("Användarnamnet är upptaget, välj ett annat.");
            reply = new Message(NetCommands.registrationDenied, user, errorMessage);

        }

    }

    /**
     * Registers a new group to the server and updates all the members of that group with the new group membership
     *
     * @param group the new group that was created
     */

    public void registerNewGroup(Group group) {
        registeredGroups.addGroup(group);
        ArrayList<User> members = group.getUsers();
        GenericID groupID = group.getGroupID();
        for (User u : members) {
            u.addGroupMembership(groupID);
        }
    }


    public void sendReply(Message reply) {
        ClientHandler client = onlineClients.get(reply.getUser());
        client.addToOutgoingMessages(reply);
    }

    public void addRegisteredUser(User newUser) {
        registeredUsers.addRegisteredUser(newUser);
    }

    public static void main(String[] args) {
        ServerController prog = new ServerController();
        //TODO: Sätt upp servertråd (extenda thread) Eller se till att main fortsätter köra...
    }


    private class BetterNameComingSoon implements Runnable { //TODO: Kom på bättre namn för klassen.

        public BetterNameComingSoon() {


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
