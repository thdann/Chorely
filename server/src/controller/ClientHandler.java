package controller;


import shared.transferable.Transferable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * ClientHandler
 * version 1.0 2020-03-23
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */

public class ClientHandler {

    private Socket socket;
    private InputThread inputThread;
    private OutputThread outputThread;
    private ServerController controller;


    public ClientHandler(Socket socket, ServerController controller) {
        this.controller=controller;
        this.socket = socket;
        inputThread = new InputThread();
        outputThread = new OutputThread();
        outputThread.start();

        Thread inputThread = new Thread(this.inputThread);
        inputThread.start();

    }

    /**
     * The inner class InputThread sets up an InputStream
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

           //while (true) {
                try {
                    System.out.println("Du har kommit hit");
                    ArrayList<Transferable> list = (ArrayList<Transferable>) ois.readObject();
                    controller.sendList(list);
                    for(int i =0; i<list.size(); i++){

                        System.out.println(list.get(i));
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
           // }


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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
