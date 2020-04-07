/**
 * This is the client class for networking.
 * @version 1.0
 * @author Timothy Denison
 */


package com.mau.chorely.model;

import shared.transferable.NetCommands;
import shared.transferable.GenericID;
import shared.transferable.TransferList;
import shared.transferable.Transferable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class ClientNetworkManager {
    private static final int SERVER_PORT = 6583;
    private static final String SERVER_IP ="10.0.2.2";

    private Socket socket;
    private static Thread inputThread;
    private static Thread outputThread;
    private static volatile boolean connected = false;
    private LinkedBlockingDeque<ArrayList<Transferable>> outBoundQueue = new LinkedBlockingDeque<>();
    private NetworkListener model;

    public ClientNetworkManager(NetworkListener model){
        this.model = model;
        setupSocket();
    }

    public void sendData(ArrayList<Transferable> data){
        if((inputThread == null || outputThread == null)){

        }
        try {
            outBoundQueue.put(data);
        } catch (InterruptedException e){
            // TODO: 2020-03-24 Varför måste tråden blocka när den lägger data i kön? evt byta typ av kö.
            System.out.println("Error putting data in outboundqueue" + e.getMessage());
        }
    }

    public boolean isConnected(){
        return connected;
    }

    public TransferList connectAndCheckStatus(TransferList list){

        GenericID id = (GenericID)list.get(Model.ID_ELEMENT);
        TransferList ret;
        int iteration = 0;


        if(socket.isClosed()){
            setupSocket();
        }

        while (!connected && iteration < 3) {
            connectSocket();
            iteration++;
        }
        if(connected){
            ret = new TransferList(NetCommands.connected, id);
        }
        else{
            ret = new TransferList(NetCommands.notConnected, id);
        }
        return ret;
    }

    private synchronized void connectSocket(){
        if(!connected){
            socket = new Socket();
            try {
                connected = false;
                SocketAddress socketAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
                socket.connect(socketAddress, 2000);
                connected = (socket.isConnected() && !socket.isClosed());
                System.out.println(connected);
                setupThreads();
            } catch (IOException e){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException intExept){
                    System.out.println("SHOULD NEVER HAPPEN! thread interrupted trying to connect");
                }
                socket = new Socket();
                System.out.println("ERRROOROOROROROOROR");
                System.out.println(e.getMessage());
                System.out.println(e);
            }
        }
    }



    private boolean setupSocket() {

            socket = new Socket();
            try {
                socket.bind(new InetSocketAddress(SERVER_IP, SERVER_PORT));
            } catch (IOException e){
                System.out.println("Error setting up socket!");
            }

            return (socket.isConnected() && !socket.isClosed());

    }


    public void disconnect() {

            inputThread.interrupt();
            outputThread.interrupt();

            try {
                socket.close();
                connected = false;
            } catch (IOException e){
                System.out.println("ERROR CLOSING SOCKET");
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
