package controller;

import model.RegisteredGroups;
import model.RegisteredUsers;

import shared.transferable.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * ServerController handles the overall logic on the server side.
 *
 * @author Angelica Asplund, Emma Svensson, Theresa Dannberg, Fredrik Jeppsson
 */
public class ServerController {
    private final static Logger messagesLogger = Logger.getLogger("messages");
    private final RegisteredUsers registeredUsers = RegisteredUsers.getInstance();
    private final RegisteredGroups registeredGroups = RegisteredGroups.getInstance();
    private final LinkedBlockingQueue<Message> clientTaskBuffer = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<User, ClientHandler> onlineClients = new ConcurrentHashMap<>();

    public ServerController(int port) {
        MessageHandler messageHandler = new MessageHandler();
        new ServerNetwork(this, port);
        Thread t1 = new Thread(messageHandler);
        t1.start();
    }

    /**
     * Adds an incoming message to the controller's queue of messages to handle.
     *
     * @param msg the incoming Message object.
     */
    public void handleMessage(Message msg) {
        clientTaskBuffer.add(msg);
    }

    /**
     * The method keeps track of the users that are currently online and
     * which client they are currently connected on.
     *
     * @param user   the logged in user
     * @param client the connected client
     */
    public void addOnlineClient(User user, ClientHandler client) {
        onlineClients.put(user, client);
    }

    /**
     * Removes the user from the list with online clients when the log off.
     *
     * @param user the user to be removed from the list.
     */
    public void removeOnlineClient(User user) {
        onlineClients.remove(user);
    }

    /**
     * Sends the groups that a user belongs to.
     *
     * @param user the user whose groups are sent.
     */
    public void sendSavedGroups(User user) {
        User userFromFile = registeredUsers.getUserFromFile(user);
        if (userFromFile != null) {
            if (userFromFile.getGroups() != null) {
                List<GenericID> groupMemberships = userFromFile.getGroups();
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


    /**
     * Sends a reply in the form of a message to a client if the client is online,
     * an updated group for example. If the user is offline the message is added
     * to a queue that sends the message when the user is online again.
     *
     * @param reply the message object containing the reply
     */
    public void sendReply(Message reply) {
        ClientHandler client = onlineClients.get(reply.getUser());
        if (client != null) {
            client.addToOutgoingMessages(reply);
        }
    }

    /**
     * Notifies all the members of a group when a change is made in the group.
     *
     * @param group the updated group containing the members to be notified.
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
     *
     * @param msg is the incoming message object
     */
    public void handleClientTask(Message msg) {
        NetCommands command = msg.getCommand();

        switch (command) {
            case registerNewGroup:
                registerNewGroup(msg);
                break;
            case updateGroup:
                updateGroup(msg);
                break;
            case searchForUser:
                searchForUser(msg);
                break;
            case logout:
                logoutUser(msg);
                break;
            case deleteGroup:
                deleteGroup(msg);
                break;
            case notificationSent:         // @Author Johan, Måns
                sendNotifications(msg);
            default:
                break;
        }
    }

    /**
     * @Author Johan, Måns
     * Sends notifications to all users in the specified message's data.
     * The method loops through the data of the msg parameter and sends a notification to each user.
     * The NetCommands value of each notification is set to NetCommands.notificationReceived
     * and the data is set to the original data from msg.
     * @param msg The message containing the data of the users to receive notifications.
     */
    private void sendNotifications(Message msg) {
        Message reply;

        if (msg.getData() != null) {
            ArrayList<Transferable> data = new ArrayList<>();
            data.addAll(msg.getData());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) instanceof User) {
                    System.out.println(data.get(i));
                    reply = new Message(NetCommands.notificationReceived, (User) data.get(i), data);
                    sendReply(reply);
                }
            }
        }

    }

    /**
     * Looks for a requested user among registered users.
     *
     * @param request is the message object that contains the user searched for
     */
    public void searchForUser(Message request) {
        Message reply;
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
     * Removes group membership from the users that belong to the group at the time
     * of deletion and then deletes the group. Sends a groupDeleted message to
     * all users that belonged to the group.
     *
     * @param msg the message that contains the group that is deleted.
     */
    private void deleteGroup(Message msg) {
        List<Transferable> data = msg.getData();
        Group groupFromMessage = (Group) data.get(0);
        Group group = new Group(groupFromMessage);   // defensive copy.
        List<User> users = group.getUsers();
        GenericID id = group.getGroupID();

        for (User user : users) {
            User userFromFile = registeredUsers.getUserFromFile(user);
            userFromFile.removeGroupMembership(id);
            registeredUsers.updateUser(userFromFile);
        }

        registeredGroups.deleteGroup(group);

        group.deleteAllUsers();
        List<Transferable> list = new ArrayList<>();
        list.add(group);

        for (User user : users) {
            Message message = new Message(NetCommands.updateGroup, user, list);
            sendReply(message);
        }
    }

    /**
     * Handles a logout message by getting the ClientHandler that belongs to the
     * user in question, and then calling its logout method.
     *
     * @param msg the logout message from the client.
     */
    public void logoutUser(Message msg) {
        User user = msg.getUser();
        ClientHandler clientHandler = onlineClients.get(user);
        if (clientHandler != null) {
            clientHandler.logout(user);
        }
    }

    /**
     * Registers a new group to the server and updates all the members
     * of that group with the new group membership
     *
     * @param request the Message object containing the group with all
     *                the members to be added.
     */
    public void registerNewGroup(Message request) {
        Message reply = null;
        Group group = (Group) request.getData().get(0);

        if (registeredGroups.groupIdAvailable(group.getGroupID())) {
            registeredGroups.updateGroup(group);
            ArrayList<User> members = group.getUsers();
            GenericID groupID = group.getGroupID();
            for (User u : members) {
                User userFromFile = registeredUsers.getUserFromFile(u);
                userFromFile.addGroupMembership(groupID);
                registeredUsers.updateUser(userFromFile);
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
     * Updates a registered group with new change and notifies all the
     * members of that group.
     *
     * @param request The Message object containing the updated group.
     */
    public void updateGroup(Message request) {
        Group updatedGroup = (Group) request.getData().get(0);
        updateUsersInGroup(updatedGroup);
        registeredGroups.updateGroup(updatedGroup);
        notifyGroupChanges(updatedGroup);
    }


    /**
     * Updates the group membership of the removed and/or added users
     *
     * @param group is the group that contains changes in members
     */
    private void updateUsersInGroup(Group group) {
        removeUsers(group);

        ArrayList<User> members = group.getUsers();
        GenericID id = group.getGroupID();
        for (User u : members) {
            User userFromFile = registeredUsers.getUserFromFile(u);
            userFromFile.addGroupMembership(id);
            registeredUsers.updateUser(userFromFile);
        }
    }

    /**
     * Updates the group membership of the users removed from a group
     * and notifies the changes to the user.
     *
     * @param newGroup the group that potentially has a change in group membership.
     */
    private void removeUsers(Group newGroup) {
        GenericID id = newGroup.getGroupID();
        Group oldGroup = registeredGroups.getGroupFromFile(id);
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(newGroup);

        for (User u : oldGroup.getUsers()) {
            if (!newGroup.getUsers().contains(u)) {
                u.removeGroupMembership(id);
                registeredUsers.updateUser(u);
                Message message = new Message(NetCommands.updateGroup, u, data);
                sendReply(message);
            }
        }
    }

    /**
     * Inner class MessageHandler handles the incoming messages from the client one at a time.
     */
    private class MessageHandler implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Message message = clientTaskBuffer.take();
                    System.out.println(message);
                    handleClientTask(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
