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

/**
 * @author Fredrik Jeppsson
 */
public class TestClient {
    private final static String IP = "127.0.0.1";
    private final static int INPUT_TIME = 1000;

    private TestClient() {
    }

    /**
     *
     * @param outgoingMessages the messages that are sent to the server during this test run.
     * @return a list of messages received from the server as a response to the outgoing messages.
     */
    public static Callable<List<Message>> newTestRun(List<Message> outgoingMessages, int port) {
        return () -> {
            List<Message> received = Collections.synchronizedList(new ArrayList<>());
            ExecutorService executor = Executors.newFixedThreadPool(2);

            try (Socket socket = new Socket(IP, port)) {
                var out = new ObjectOutputStream(socket.getOutputStream());
                var in = new ObjectInputStream(socket.getInputStream());

                Runnable output = () -> {
                    try {
                        for (Message msg : outgoingMessages) {
                            out.writeObject(msg);
                            out.flush();
                        }
                    } catch (IOException ignore) {
                    }
                };

                Runnable input = () -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            var msg = (Message) in.readObject();
                            received.add(msg);
                        }
                    } catch (ClassNotFoundException | IOException ignore) {
                    }
                };

                executor.submit(output);
                executor.submit(input);
                Thread.sleep(INPUT_TIME);
                in.close();
                executor.shutdown();
            } catch (IOException ignore) {
            }

            return received;
        };
    }
}
