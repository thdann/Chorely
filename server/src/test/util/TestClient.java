package test.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import shared.transferable.Message;
import shared.transferable.User;
import shared.transferable.NetCommands;


public class TestClient {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    public TestClient(String ip, int port, List<Message> outgoingMessage) {
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Thread output = new Thread(new Output(outgoingMessage));
            output.start();



        } catch (IOException e) {
            System.err.println("Couldn't establish connection to server.");
//            System.exit(0);
        }
    }

    public List<Message> getReceivedMessages() {
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<List<Message>> receivedMessages = executorService.submit(new Input());
            List<Message> messages = receivedMessages.get();
            executorService.shutdown();
            return messages;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private class Input implements Callable<List<Message>> {

        @Override
        public List<Message> call() throws Exception {
            List<Message> received = Collections.synchronizedList(new ArrayList<>());

            try {
                Thread collectMessages = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            Message msg = (Message) in.readObject();
                            received.add(msg);
                        }
                    } catch (ClassNotFoundException | IOException ignore) {
                    }
                });

                collectMessages.start();
                Thread.sleep(1000);
                socket.close();
            }

            catch (InterruptedException | IOException e) {
            }

            return received;
        }
    }

    private class Output implements Runnable {
        private final List<Message> outgoingMessages;

        public Output(List<Message> outgoingMessages) {
            this.outgoingMessages = outgoingMessages;
        }

        @Override
        public void run() {
            try {
                for (Message msg : outgoingMessages) {
                    out.writeObject(msg);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public static void main(String[] args) {
        List<Message> send = new ArrayList<>();
//        send.add(new Message(NetCommands.registerUser, new User("Fredrik", "secret")));
        send.add(new Message(NetCommands.login, new User("Fredrik", "secret")));
        TestClient test = new TestClient("127.0.0.1", 6583, send);
        List<Message> received = test.getReceivedMessages();
        System.out.println(received);
    }

}
