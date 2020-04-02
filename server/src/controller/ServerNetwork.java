package controller;

/**
 * ServerNetwork sets up the network between the server and connecting clients.
 * version 1.0 2020-03-23
 *
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */

import shared.transferable.NetCommands;
import shared.transferable.RequestID;
import shared.transferable.Transferable;
import shared.transferable.User;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerNetwork implements Runnable {

    private ServerController controller;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ServerNetwork(ServerController controller, int port) {
        this.controller = controller;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Serversocket skapad");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Thread serverThread = new Thread(this);
        serverThread.start();
    }

    @Override
    public void run() {
        System.out.println("Tråd skapas i run()");

        while (true) {
            try {
                socket = serverSocket.accept(); //Tar emot anslutande klient
                System.out.println("Server tar emot anslutande klient");
                //Sätter upp strömmar mellan klient och server
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                //oos.writeObject("Hej klient, du är nu uppkopplad.");
                ArrayList<Transferable> testArray = new ArrayList<>();
                System.out.println("Reading object");
                testArray = (ArrayList<Transferable>) ois.readObject();
                System.out.println(((NetCommands)testArray.get(0)).toString());
                RequestID id = (RequestID) testArray.get(1);

                testArray = new ArrayList<Transferable>();
                testArray.add(NetCommands.registrationOk);
                testArray.add(id);
                System.out.println(testArray.size());
                oos.writeObject(testArray);
                //controller.addRegisteredUser((User) ois.readObject()); //Skickar vidare ett nyregistrerat Userobjekt till controllen som skickar till registeredUsers
                System.out.println("read object");
                //oos.writeObject(NetCommands.registrationOk); //Ska skickas registrationOK tillbaka till klienten.

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
