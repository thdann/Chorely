package com.mau.chorely.model;



import com.mau.chorely.model.transferrable.Transferable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class NetInterface {
    private static final int SERVER_PORT = 6583;
    private static final String SERVER_IP ="";
    private Socket socket;
    private static Thread inputThread;
    private static Thread outputThread;
    boolean connected = false;
    LinkedBlockingDeque<ArrayList<Transferable>> outBoundQueue = new LinkedBlockingDeque<>();
    NetworkListener model;

    public NetInterface(NetworkListener model){
        this.model = model;
        connected = setupSocket();
        setupThreads();
    }

    public void sendData(ArrayList<Transferable> data){
        try {
            outBoundQueue.put(data);
        } catch (InterruptedException e){
            // TODO: 2020-03-24 Varför måste tråden blocka när den lägger data i kön? evt byta typ av kö.
            System.out.println("Error putting data in outboundqueue" + e.getMessage());
        }
    }

    private boolean setupSocket() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            return true;

        } catch (IOException e) {
            System.out.println("Error setting up socket!");
        }
        return false;
    }

    // TODO: 2020-03-24 Make client resetable. interrupt threads, restart, and reset socket.

    private void connect(){
        connected = setupSocket();

    }

    public void disconnect() {
        try{
            socket.close();
            connected = false;
        }
        catch (IOException e){
            System.out.println("Error closing socket" + e.getMessage());
        }
    }

    private void setupThreads(){
        outputThread = new Thread(new OutputThread());
        inputThread = new Thread(new InputThread());
        outputThread.start();
        inputThread.start();
    }

    private class InputThread implements Runnable{

        @Override
        public void run() {

                try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())){
                    while (!Thread.interrupted()) {
                        try {
                            model.notify((ArrayList<Transferable>) inputStream.readObject());
                        } catch (ClassNotFoundException e){
                            System.out.println("Error reading object from stream" + e.getMessage());
                        } catch (IOException e){
                            System.out.println("Error reading object from stream" + e.getMessage());
                            break;
                        }
                    }
                }
                catch (IOException e){
                    System.out.println("Error setting up inputStream" + e.getMessage());
                }
                disconnect();
            }
        }


    private class OutputThread implements Runnable {
        @Override
        public void run() {

            try(ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())){
                while(!Thread.interrupted()) {
                    try {
                        outputStream.writeObject(outBoundQueue.getFirst());
                        outputStream.flush();
                    } catch (IOException e){
                        System.out.println("Error writing to outputStream" + e.getMessage());
                        break;
                    }
                }

            }
            catch(IOException e){
                System.out.println("Error setting up outputStream" + e.getMessage());

            }
            disconnect();
        }

    }

}
