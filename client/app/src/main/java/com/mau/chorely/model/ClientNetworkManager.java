/**
 * This is the client class for networking.
 *
 * @version 1.0
 * @author Timothy Denison
 */


package com.mau.chorely.model;

import android.renderscript.ScriptGroup;

import shared.transferable.Message;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientNetworkManager {
    private static final int SERVER_PORT = 6583;
    private static final String SERVER_IP = "10.0.2.2";
    private volatile boolean connected = false;
    private LinkedBlockingDeque<Message> outBoundQueue = new LinkedBlockingDeque<>();
    private NetworkListener model;

    public ClientNetworkManager(NetworkListener model) {
        this.model = model;
        Thread thread = new Thread(new ConnectionHandler());
        thread.start();
    }

    private class ConnectionHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (!connected) {
                    try {
                        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        Thread outputThread = new Thread(new OutputHandler(socket, output));
                        outputThread.start();
                        Thread inputThread = new Thread(new InputHandler(socket, input, outputThread));
                        inputThread.start();
                        connected = true;
                        // todo: notify model that we're connected.
                    } catch (IOException e1) {
                        // if we get here connection has failed to be established and we need to retry.
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e2) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } else if (connected) {
                    try {
                        // We are connected, sleep for 500 ms and then check connected status.
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw new RuntimeException("It's a bug to be here..");
                }
            }
        }
    }

    private class InputHandler implements Runnable {
        private final ObjectInputStream input;
        private final Thread outputThread;
        private final Socket socket;

        public InputHandler(Socket socket, ObjectInputStream input, Thread outputThread) {
            this.socket = socket;
            this.input = input;
            this.outputThread = outputThread;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    model.notify((Message) input.readObject());
                } catch (IOException e) {
                    connected = false;
                    // Interrupt the output thread in case it's waiting on an empty outBoundQueue.
                    outputThread.interrupt();
                    break;
                } catch (ClassNotFoundException e) {
                    // We should only be here if we have a bug in the program that makes
                    // serialization of Message fail.
                    throw new RuntimeException(e);
                } finally {
                    try {
                        socket.close();
                    } catch (IOException ignore) {
                    }

                    // todo: Notify model of connection failure.
                }
            }
        }
    }


    private class OutputHandler implements Runnable {
        private final Socket socket;
        private final ObjectOutputStream output;

        public OutputHandler(Socket socket, ObjectOutputStream output) {
            this.socket = socket;
            this.output = output;
        }

        @Override
        public void run() {
            Message msg = null;
            while (true) {
                try {
                    msg = outBoundQueue.take();
                    output.writeObject(msg);
                    output.flush();
                } catch (IOException e1) {
                    // If IOException happens, we've taken a Message from the output queue and
                    // tried to send it with writeObject. In that case we need to put it back
                    // on the output queue before terminating the thread.
                    outBoundQueue.addFirst(msg);
                    connected = false;
                    break;
                } catch (InterruptedException e2) {
                    // This interrupted exception happens if another thread interrupts this thread
                    // while the output queue is waiting.
                    connected = false;
                    break;
                } finally {
                    try {
                        socket.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }

//    public void disconnect() {
//
//        inputThread.interrupt();
//        outputThread.interrupt();
//
//        try {
//            socket.close();
//            connected = false;
//        } catch (IOException e){
//            System.out.println("ERROR CLOSING SOCKET");
//        }
//    }
}



/*
    public void sendData(Message data){
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


    private boolean setupSocket() {

        socket = new Socket();
        try {
            socket.bind(new InetSocketAddress(SERVER_IP, SERVER_PORT));
        } catch (IOException e){
            System.out.println("Error setting up socket!");
        }

        return (socket.isConnected() && !socket.isClosed());

    }

    public void connect(){
        if (socket == null){
            while (!connected){
                System.out.println("HERREEERERER");
                connectSocket();
            }
        }
    }

    private synchronized void connectSocket(){
        if(!connected){
            socket = new Socket();
            try {
                connected = false;
                SocketAddress socketAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
                socket.connect(socketAddress, 2000);
                System.out.println("CONNECTED_______________________________________");
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
                System.out.println("Socket could not connect.");
                System.out.println(e.getMessage());
                System.out.println(e);
            }
        }
    }

    /*

    public void connectAndCheckStatus(){

        Message connectionStatus;
        int iteration = 0;
        if(socket.isClosed()){
            setupSocket();
        }

        while (!connected && iteration < 3) {
            connectSocket();
            iteration++;
        }
        if(connected){
            connectionStatus = new Message(NetCommands.connected, null, null );
        }
        else{
            connectionStatus = new Message(NetCommands.notConnected, null, null, new ErrorMessage("Not connected"));
        }
        model.notify(connectionStatus);
    }












     */

