/**
 * This is the client class for networking.
 * @version 1.0
 * @author Timothy Denison
 */


package com.mau.chorely.model;

import shared.transferable.ErrorMessage;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class ClientNetworkManager {
    private static final int SERVER_PORT = 6583;
    private static final String SERVER_IP ="10.0.2.2";

    private Socket socket;
    private static Thread inputThread;
    private static Thread outputThread;
    private boolean connected = false;
    private LinkedBlockingDeque<ArrayList<Transferable>> outBoundQueue = new LinkedBlockingDeque<>();
    private NetworkListener model;

    public ClientNetworkManager(NetworkListener model){
        this.model = model;

        if(connected = setupSocket()) {
            setupThreads();
        }
        else{
            ArrayList<Transferable> errorList = new ArrayList<>();
            errorList.add(NetCommands.internalClientError);
            errorList.add(new ErrorMessage("Error connecting to server."));
            model.notify(errorList);
        }
    }

    public void sendData(ArrayList<Transferable> data){
        try {
            outBoundQueue.put(data);
        } catch (InterruptedException e){
            // TODO: 2020-03-24 Varför måste tråden blocka när den lägger data i kön? evt byta typ av kö.
            System.out.println("Error putting data in outboundqueue" + e.getMessage());
        }
    }

    public void reconnect(){
        if(connected)
            disconnect();
        if(connected = setupSocket())
            setupThreads();
        else
            netWorkError("Could not connect to server.");
    }

    private void netWorkError(String message){
        ErrorMessage errorMessage = new ErrorMessage(message);
        ArrayList<Transferable> transferables = new ArrayList<>();
        transferables.add(NetCommands.internalClientError);
        transferables.add(errorMessage);
        model.notify(transferables);
    }

    private boolean setupSocket() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            return true;

        } catch (IOException e) {
            System.out.println("Error setting up socket!" + e.getMessage());
        }
        return false;
    }


    public void disconnect() {
        try{
            inputThread.interrupt();
            outputThread.interrupt();
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
                            disconnect();
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
                        outputStream.writeObject(outBoundQueue.take());
                        outputStream.flush();
                    } catch (IOException | InterruptedException e){
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