package controller;

import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import shared.transferable.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ClientHandler
 * version 2.0 2020-03-23
 *
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */

public class ClientHandler {

    private Socket socket;
    private InputThread inputThread;
    private OutputThread outputThread;
    private ServerController controller;
    private LinkedBlockingQueue<Message> outgoingMessages;
    private volatile User clientUser = null;
    private boolean onlineClient = false;


    public ClientHandler(Socket socket, ServerController controller) {
        this.controller = controller;
        this.socket = socket;

        outgoingMessages = new LinkedBlockingQueue<>();
        inputThread = new InputThread();
        outputThread = new OutputThread();
        outputThread.start();

        Thread inputThread = new Thread(this.inputThread);
        inputThread.start();

    }

    public void addToOutgoingMessages(Message reply) {
        outgoingMessages.add(reply);
    }

    public void throwOut(){
        controller.removeOnlineClient(clientUser);
        this.clientUser = null;
    }

    /**
     * The inner class InputThread sets up an InputStream to receive messages from connected client
     */

    private class InputThread implements Runnable {
        ObjectInputStream ois;

        public void run() {
            //Sätt upp ObjectInputStream från socket
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {

                while (true) {
                    try {
                        System.out.println("Du har kommit hit");
                        Message msg = (Message) ois.readObject();
                        System.out.println(msg.getCommand());

                        if(clientUser == null) {
                            controller.addOnlineClient(msg.getUser(), ClientHandler.this);
                            clientUser = msg.getUser();
                        }

                        onlineClient = true;
                        controller.sendMessage(msg);

                    } catch (ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (IOException e) {
                if(clientUser != null) {
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
            //Sätt upp ObjectOutputStream från socket
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                while (true) {
                    oos.writeObject(outgoingMessages.take());
                    oos.flush();
                    System.out.println("skickat svar till klienten");

                }
            } catch (IOException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}