package controller;

import model.RegisteredUsers;
import shared.transferable.ErrorMessage;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * ClientHandler
 *
 * @author Angelica Asplund, Emma Svensson, Theresa Dannberg, Fredrik Jeppsson
 */
public class ClientHandler {
    private final static Logger messagesLogger = Logger.getLogger("messages");
    private final RegisteredUsers registeredUsers = RegisteredUsers.getInstance();
    private final Socket socket;
    private final OutputThread outputThread;
    private final ServerController controller;
    private final LinkedBlockingQueue<Message> outgoingMessages;

    public ClientHandler(Socket socket, ServerController controller) {
        this.controller = controller;
        this.socket = socket;
        outgoingMessages = new LinkedBlockingQueue<>();
        InputThread inputThread1 = new InputThread();
        outputThread = new OutputThread();
        outputThread.start();
        Thread inputThread = new Thread(inputThread1);
        inputThread.start();
    }

    /**
     * Adds an Message object to the queue of outgoing messages for this client.
     *
     * @param reply the Message object to be placed in queue.
     * @return
     */
    public Message addToOutgoingMessages(Message reply) {
        outgoingMessages.add(reply);
        return reply;
    }

    /**
     * Called by controller to log out a user by closing the socket.
     *
     * @param user the user that is logged out.
     */
    public boolean logout(User user) {
        try {
            socket.close();
        } catch (IOException ignore) {
            return false;
        }
        messagesLogger.info(user + " logged out.");
        return true;
    }

    /**
     * Handles an attempt to register a new user.
     *
     * @param message the registration message received from the client.
     * @return true if registration is successful.
     */
    public boolean registerUser(Message message) {
        User user = message.getUser();
        Message reply;
        boolean success = false;
        if (registeredUsers.userNameAvailable(user.getUsername())) {
            registeredUsers.writeUserToFile(user);
            reply = new Message(NetCommands.registrationOk, user);
            success = true;
            controller.addOnlineClient(user, ClientHandler.this);
        } else {
            ErrorMessage errorMessage = new ErrorMessage("Användarnamnet är upptaget, välj ett annat.");
            reply = new Message(NetCommands.registrationDenied, user, errorMessage);
        }
        outgoingMessages.add(reply);
        return success;
    }

    /**
     * Handles an attempt to login.
     *
     * @param message the login message received from the client.
     * @return true if login is successful.
     */
    public boolean loginUser(Message message) {
        boolean success = false;
        User user = message.getUser();
        User userFromFile = registeredUsers.getUserFromFile(user);
        Message reply;

        if (userFromFile == null) {  // userFromFile == null when the username isn't found as a registered user.
            reply = new Message(NetCommands.loginDenied, user, new ErrorMessage("Fel användarnamn eller lösenord, försök igen!"));
        }
//        else if (user.compareUsernamePassword(userFromFile)) {
          else if (registeredUsers.checkPassword(user.getUsername(), user.getPassword())) {
            reply = new Message(NetCommands.loginOk, userFromFile);
            controller.addOnlineClient(userFromFile, ClientHandler.this);
            success = true;
        } else {
            reply = new Message(NetCommands.loginDenied, user, new ErrorMessage("Fel användarnamn eller lösenord, försök igen!"));
        }

        outgoingMessages.add(reply);
        return success;
    }


    /**
     * The inner class InputThread sets up an InputStream to receive messages from a connected client.
     *
     * InputThread makes sure that a client is either registered or logged in before allowing it to
     * proceed and send other types of messages to the server.
     *
     * Once a client is logged in, an infinite loop receives messages until the client connection is
     * closed.
     */
    private class InputThread implements Runnable {

        @Override
        public void run() {
            User user = null;
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                boolean isLoggedIn = false;
                while (!isLoggedIn) {
                    Message msg = (Message) ois.readObject();
                    user = msg.getUser();
                    NetCommands command = msg.getCommand();
                    switch (command) {
                        case registerUser:
                            isLoggedIn = registerUser(msg);
                            break;
                        case login:
                            isLoggedIn = loginUser(msg);
                            break;
                        default:
                            // If default case happens we have received a message other than login or registerUser
                            // before the user is actually logged in. We do nothing and wait for the next message.
                    }
                }

                controller.sendSavedGroups(user);

                while (true) {
                    Message msg = (Message) ois.readObject();
                    messagesLogger.info("incoming message: " + msg + " received from client " + msg.getUser());
                    controller.handleMessage(msg);
                }

            } catch (ClassNotFoundException | IOException e) {
                try {
                    socket.close();
                } catch (IOException ignore) {
                }

                if (user != null) {
                    controller.removeOnlineClient(user);
                }

                outputThread.interrupt();
                messagesLogger.info(user + " disconnected from server.");
            }
        }
    }

    /**
     * The inner class OutputThread sets up an OutputStream and sends message to the client.
     * The outgoing messages are put in an output queue by the controller.
     */
    private class OutputThread extends Thread {

        @Override
        public void run() {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                while (!Thread.currentThread().isInterrupted()) {
                    Message reply = outgoingMessages.take();

                    System.out.println("outgoing: " + reply);

                    oos.writeObject(reply);
                    oos.flush();
                    messagesLogger.info("outgoing message: " + reply + " sent to user " + reply.getUser());
                }
            } catch (IOException | InterruptedException ignore) {
                // We get here when the connection to the client is lost and the input thread interrupts
                // the output thread. It's okay to ignore the exception here because it's logged in the
                // input thread.
            }
        }
    }
}