package com.mau.chorely.model;

import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This is the client class for networking.
 *
 * @author Fredrik Jeppsson
 * @version 2.0
 */
public class ClientNetworkManager {
    private static final int SERVER_PORT = 6583;
    private static final String SERVER_IP = "10.0.2.2";
    private volatile boolean connected = false;
    private LinkedBlockingDeque<Message> outBoundQueue = new LinkedBlockingDeque<>();
    private NetworkListener model;
    private ConnectionHandler connectionHandler = new ConnectionHandler();

    public ClientNetworkManager(NetworkListener model) {
        this.model = model;
        Thread thread = new Thread(connectionHandler);
        thread.start();
    }

    public void sendMessage(Message msg) {
        outBoundQueue.add(msg);
    }

    private class ConnectionHandler implements Runnable {
        private synchronized void sleepConnectionHandler() {
            try {
                wait();
            } catch (InterruptedException ignore) {
            }
        }

        synchronized void wakeConnectionHandler() {
            notifyAll();
        }

        @Override
        public void run() {
            while (true) {
                if (!connected) {
                    try {
                        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                        System.out.println(new Date() + " Network: established socket...");
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        Thread outputThread = new Thread(new OutputHandler(socket, output));
                        outputThread.start();
                        Thread inputThread = new Thread(new InputHandler(socket, input, outputThread));
                        inputThread.start();
                        connected = true;
                        model.handleTask(new Message(NetCommands.connected, null, new ArrayList<Transferable>()));
                    } catch (IOException e1) {
                        // This exception happens if the socket has failed to connect.
                        System.out.println(new Date() + " Network: failed to connect...");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e2) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } else {
                    sleepConnectionHandler();
                }
            }
        }
    }

    private class InputHandler implements Runnable {
        private final ObjectInputStream input;
        private final Thread outputThread;
        private final Socket socket;

        InputHandler(Socket socket, ObjectInputStream input, Thread outputThread) {
            this.socket = socket;
            this.input = input;
            this.outputThread = outputThread;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    model.handleTask((Message) input.readObject());
                }
            } catch (IOException e) {
                System.out.println(new Date() + "Network: closed input handler with IOException.");
            } catch (ClassNotFoundException e) {
                // We should only get here if we have a bug in the program that makes
                // serialization of Message fail.
                throw new RuntimeException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException ignore) {
                    // Socket might already have been closed by the OutputHandler.
                }
                connected = false;
                // Interrupt the output thread in case it's waiting on an empty outBoundQueue.
                outputThread.interrupt();
                // Wake up the ConnectionHandler to notify it of connection failure and to initiate
                // reconnection attempts.
                connectionHandler.wakeConnectionHandler();
                // Notify the model class of connection failure.
                model.handleTask(new Message(NetCommands.connectionFailed, null, new ArrayList<Transferable>()));
            }
        }
    }

    private class OutputHandler implements Runnable {
        private final Socket socket;
        private final ObjectOutputStream output;

        OutputHandler(Socket socket, ObjectOutputStream output) {
            this.socket = socket;
            this.output = output;
        }

        @Override
        public void run() {
            Message msg = null;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    msg = outBoundQueue.take();
                    output.writeObject(msg);
                    output.flush();
                }
            } catch (IOException e) {
                System.out.println(new Date() + " Network: closed output handler with IOException.");
                // If IOException happens, we've taken a Message from the output queue and
                // tried to send it with writeObject. In that case we need to put it back
                // at the front of the output queue before terminating the thread.
                outBoundQueue.addFirst(msg);
            } catch (InterruptedException e) {
                System.out.println(new Date() + " Network: closed output handler with InterruptedException.");
                // This interrupted exception happens if another thread interrupts this thread
                // while the output queue is waiting.
            } finally {
                try {
                    socket.close();
                } catch (IOException ignore) {
                    // Socket might already have been closed by the InputHandler.
                }
                connected = false;
            }
        }
    }
}

