package Controller;

/**
 * ServerNetwork sets up the serverSocket and starts a thread that awaits clients asking to connect,
 * then creates a
 * version 1.0 2020-03-23
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */

import java.io.IOException;
import java.net.ServerSocket;

public class ServerNetwork implements Runnable {

    private ServerController controller;
    private ServerSocket connection;
    private ClientHandler clientHandler;

    public ServerNetwork(ServerController controller, int port) {
        this.controller = controller;

        try {
            connection = new ServerSocket(port);
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
                clientHandler = new ClientHandler(connection.accept(), controller);
                System.out.println("Connection accepted");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}
