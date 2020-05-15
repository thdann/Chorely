package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * ServerNetwork sets up the network between the server and connecting clients.
 * version 1.0 2020-03-23
 *
 * @author Angelica Asplund, Emma Svensson and Theresa Dannberg
 */
public class ServerNetwork implements Runnable {
    private final static Logger messagesLogger = Logger.getLogger("messages");
    private final int port;
    private final ServerController controller;
    private ServerSocket serverSocket;

    public ServerNetwork(ServerController controller, int port) {
        this.controller = controller;
        this.port = port;
        startServer();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            Thread serverThread = new Thread(this);
            serverThread.start();
        } catch (IOException e1) {
            // todo: not being able to start the server thread is a critical error. log this to a future error log.
            System.exit(0);
        }
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
