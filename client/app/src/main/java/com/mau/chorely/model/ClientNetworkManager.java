package com.mau.chorely.model;

import android.util.Log;

import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Fredrik Jeppsson
 * @version 2.0
 * This class handles the network connection between the client and the server. The class is
 * designed to automatically reconnect whenever the connection to the server is lost. This is
 * achieved by using three different threads: ClientNetworkManager, InputHandler, OutputHandler.
 */
public class ClientNetworkManager {
    private static final String TAG = "ClientNetworkManager";
    private static final int SERVER_PORT = 6583;
    private static final String SERVER_IP = "192.168.50.210";
    //private static final String SERVER_IP = "10.0.2.2";
    private volatile boolean connected = false;
    private LinkedBlockingDeque<Message> outBoundQueue = new LinkedBlockingDeque<>();
    private Model model;
    private ConnectionHandler connectionHandler = new ConnectionHandler();

    public ClientNetworkManager(Model model) {
        this.model = model;
        Thread thread = new Thread(connectionHandler);
        thread.start();
    }

    /**
     * Called when a message needs to be sent to the server. The message is put in an output
     * queue for later processing by the OutputHandler.
     * @param msg the Message that is sent to the server.
     */
    public void sendMessage(Message msg) {
        outBoundQueue.add(msg);
    }

    /**
     * Connects to the server and spawns one thread that handles input and one
     * thread that handles output. As long as the connection is alive, this thread waits. When
     * the connection is lost, the thread wakes up and reestablishes the connection.
     * Once a connection to the server has been successfully established, the Model class is
     * notified.
     */
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
                        Log.i(TAG, "Established socket.");
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        Thread outputThread = new Thread(new OutputHandler(socket, output));
                        outputThread.start();
                        Thread inputThread = new Thread(new InputHandler(socket, input, outputThread));
                        inputThread.start();
                        connected = true;
                        model.handleTask(new Message(NetCommands.connected, null, new ArrayList<Transferable>()));
                    } catch (IOException e1) {
                        Log.i(TAG, "Failed to connect.");
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

    /**
     * Continuously reads Message objects from the input stream. If the underlying socket
     * connection is lost, an IOException will cause the reading loop to finish. Important
     * cleanup tasks are then performed, including interrupting the OutputHandler from waiting
     * on its output queue, and notifying the Model class that the connection has been lost.
     * It wakes up the ClientNetworkManager thread, causing it to attempt to reestablish the
     * connection.
     */
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
                Log.i(TAG, "Closed input handler with IOException.");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException ignore) {
                }
                connected = false;
                outputThread.interrupt();
                connectionHandler.wakeConnectionHandler();
                model.handleTask(new Message(NetCommands.connectionFailed, null, new ArrayList<Transferable>()));
            }
        }
    }

    /**
     * Takes messages from the output queue and sends them to the server. The conditions that
     * terminate this thread are different from the InputHandler. If this thread is waiting on
     * an empty output queue, a closed socket will not cause an IOException. Therefore, if this
     * thread is not interrupted, it will most likely not terminate when the connection is lost.
     * If an IOException does happen however, the message that the output handler attempted to send
     * must be put back at the front of the output queue since it was never successfully sent.
     */
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
                Log.i(TAG, "Closed output handler with IOException.");
                outBoundQueue.addFirst(msg);
            } catch (InterruptedException e) {
                Log.i(TAG, "Closed output handler with InterruptedException.");
            } finally {
                try {
                    socket.close();
                } catch (IOException ignore) {
                }
                connected = false;
            }
        }
    }
}

