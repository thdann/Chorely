package sytemtests;

import controller.ServerController;
import org.junit.jupiter.api.Test;
import shared.transferable.ErrorMessage;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.User;
import test.util.TestClient;
import test.util.TestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrationTests {
    private static final int basePort = 6800;


    /**
     * Registration of a single user.
     */
    @Test
    public void testRegistration() {
        try {
            int port = basePort + 1;
            ExecutorService executorService = Executors.newCachedThreadPool();
            ServerController serverController = new ServerController(port);
            User user = new User("testRegistration", "secret");
            List<Message> outgoingMessages = new ArrayList<>();
            outgoingMessages.add(new Message(NetCommands.registerUser, user));
            Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
            Future<List<Message>> received = executorService.submit(testClient);
            List<Message> receivedMessages = received.get();
            List<Message> expected = Collections.singletonList(new Message(NetCommands.registrationOk, user));
            assertEquals(receivedMessages, expected);
            TestUtils.deleteUser(user);
        } catch (InterruptedException | ExecutionException ignore) {
        }
    }

    /**
     * Register multiple users simultaneously.
     */
    @Test
    public void testMultiRegistration() {
        try {
            int port = basePort + 2;
            ExecutorService executor = Executors.newCachedThreadPool();
            ServerController serverController = new ServerController(port);
            User user1 = new User("testMultiUsers1", "password");
            User user2 = new User("testMultiUsers2", "password");
            User user3 = new User("testMultiUsers3", "password");
            User user4 = new User("testMultiUsers4", "password");
            List<Message> outgoingMessages1 = Collections.singletonList(new Message(NetCommands.registerUser, user1));
            List<Message> outgoingMessages2 = Collections.singletonList(new Message(NetCommands.registerUser, user2));
            List<Message> outgoingMessages3 = Collections.singletonList(new Message(NetCommands.registerUser, user3));
            List<Message> outgoingMessages4 = Collections.singletonList(new Message(NetCommands.registerUser, user4));
            List<Callable<List<Message>>> tests = new ArrayList<>();
            tests.add(TestClient.newTestRun(outgoingMessages1, port));
            tests.add(TestClient.newTestRun(outgoingMessages2, port));
            tests.add(TestClient.newTestRun(outgoingMessages3, port));
            tests.add(TestClient.newTestRun(outgoingMessages4, port));

            List<Future<List<Message>>> futureResults = executor.invokeAll(tests);
            List<List<Message>> results = new ArrayList<>();
            for (var future : futureResults) {
                results.add(future.get());
            }

            List<List<Message>> expected =
                    List.of(List.of(new Message(NetCommands.registrationOk, user1)),
                            List.of(new Message(NetCommands.registrationOk, user2)),
                            List.of(new Message(NetCommands.registrationOk, user3)),
                            List.of(new Message(NetCommands.registrationOk, user4)));

            for (int i = 0; i < results.size(); i++) {
                assertEquals(results.get(i), expected.get(i));
            }

            TestUtils.deleteUser(user1);
            TestUtils.deleteUser(user2);
            TestUtils.deleteUser(user3);
            TestUtils.deleteUser(user4);

        } catch (InterruptedException | ExecutionException ignore) {
        }
    }

    @Test
    public void testRegistrationDenied() {
        try {
            int port = basePort + 3;
            ExecutorService executorService = Executors.newCachedThreadPool();
            ServerController serverController = new ServerController(port);
            User user = new User("testRegistration", "secret");
            List<Message> outgoingMessages = new ArrayList<>();
            outgoingMessages.add(new Message(NetCommands.registerUser, user));

            // Normal registration.
            Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
            Future<List<Message>> received = executorService.submit(testClient);
            List<Message> receivedMessages = received.get();
            List<Message> expected = Collections.singletonList(new Message(NetCommands.registrationOk, user));
            assertEquals(expected, receivedMessages);

            // Trying to register with the same username - should fail because it already exists.
            Callable<List<Message>> testClient2 = TestClient.newTestRun(outgoingMessages, port);
            Future<List<Message>> received2 = executorService.submit(testClient);
            List<Message> receivedMessages2 = received2.get();
            List<Message> expected2 = Collections.singletonList(new Message(NetCommands.registrationDenied, user,
                    new ErrorMessage("Användarnamnet är upptaget, välj ett annat.")));
            assertEquals(expected2, receivedMessages2);

            TestUtils.deleteUser(user);

        } catch (InterruptedException | ExecutionException ignore) {
        }
    }


}


