package controller;

import shared.transferable.Message;
import shared.transferable.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * ClientHandler
 * version 2.0 2020-03-23
 *
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */
public class ClientHandler {
    private final static Logger messagesLogger = Logger.getLogger("messages");
    private Socket socket;
    private OutputThread outputThread;
    private ServerController controller;
    private LinkedBlockingQueue<Message> outgoingMessages;
    private volatile User clientUser = null;

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
     * Adds an Message object to a list of outgoing messages if the
     * receiving user is currently offline.
     *
     * @param reply the Message object to be placed in queue.
     */
    public void addToOutgoingMessages(Message reply) {
        outgoingMessages.add(reply);
    }

    /**
     * Removes a user from the list of clients currently online.
     */
    public void throwOut() {
        controller.removeOnlineClient(clientUser);
        this.clientUser = null;
    }

    /**
     * The inner class InputThread sets up an InputStream to receive messages from connected client
     */
    private class InputThread implements Runnable {
        ObjectInputStream ois;

        @Override
        public void run() {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                while (true) {
                    try {
                        Message msg = (Message) ois.readObject();
                        System.out.println(msg.getCommand());
                        messagesLogger.info(msg.getCommand() + " received from client " + msg.getUser());

                        if (clientUser == null) {
                            controller.addOnlineClient(msg.getUser(), ClientHandler.this);
                            clientUser = msg.getUser();
                        }

                        controller.sendMessage(msg);

                    } catch (ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (IOException e) {
                if (clientUser != null) {
                    // only removes client from online user -hashmap if it has been registered.
                    controller.removeOnlineClient(clientUser);
                }
                // Interrupts output thread waiting in queue.
                outputThread.interrupt();
            }
        }
    }

    /**
     * The inner class OutputThread sets up an OutputStream
     */
    private class OutputThread extends Thread {
        ObjectOutputStream oos;

        public void run() {
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                while (true) {
                    Message reply = outgoingMessages.take();
                    oos.writeObject(reply);
                    oos.flush();
                    messagesLogger.info(reply.getCommand() + " Sent to user " + reply.getUser());

                }
            } catch (IOException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}