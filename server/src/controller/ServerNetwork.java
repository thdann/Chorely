package controller;

/**
 * ServerNetwork sets up the network between the server and connecting clients.
 * version 1.0 2020-03-23
 *
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


public class ServerNetwork implements Runnable {
    private final static Logger messagesLogger = Logger.getLogger("messages");
    private ServerController controller;
    private ServerSocket serverSocket;

    public ServerNetwork(ServerController controller, int port) {
        this.controller = controller;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Thread serverThread = new Thread(this);
        serverThread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler newClient = new ClientHandler(socket, controller);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
