package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * ServerNetwork sets up the network between the server and connecting clients.
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

    /**
     * Starts a thread that accepts incoming sockets using a ServerSocket object.
     */
    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            Thread serverThread = new Thread(this);
            serverThread.start();
        } catch (IOException e1) {
            messagesLogger.severe("ServerNetwork could not be started.");
            System.exit(0);
        }
    }

    /**
     * Waits for new connections from clients and starts a ClientHandler for each
     * established socket.
     */
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
