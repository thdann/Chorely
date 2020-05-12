import controller.ServerController;
import org.junit.jupiter.api.Test;
import shared.transferable.*;
import test.util.TestClient;
import test.util.TestUtils;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static shared.transferable.NetCommands.*;

public class LoginTests {

    @Test
    public void testSimpleLogin() {
        User user = new User("testLoginAndReceiveGroup", "secret");
        Group group = new Group("testLoginGroup");
        group.addUser(user);
        try {
            int port = 6587;
            ExecutorService executorService = Executors.newCachedThreadPool();
            ServerController serverController = new ServerController(port);

            // Register a user and create a group.
            List<Message> outgoingMessages = List.of(
                    new Message(registerUser, user),
                    new Message(registerNewGroup, user, List.of(group)));
            Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
            Future<List<Message>> received = executorService.submit(testClient);
            List<Message> receivedMessages = received.get();
            List<Message> expected = List.of(
                    new Message(registrationOk, user),
                    new Message(newGroupOk, user),
                    new Message(updateGroup, user, List.of(group)));
            assertEquals(expected, receivedMessages);

            // Login with the same user.
            outgoingMessages = List.of(new Message(login, user));
            testClient = TestClient.newTestRun(outgoingMessages, port);
            received = executorService.submit(testClient);
            receivedMessages = received.get();
            expected = List.of(
                    new Message(loginOk, user),
                    new Message(updateGroup, user, List.of(group)));
            assertEquals(expected, receivedMessages);
        } catch (InterruptedException | ExecutionException e) {
            // What do I do here?
        } finally {
            TestUtils.deleteUser(user);
            TestUtils.deleteGroup(group);
        }
    }

    @Test
    public void testLoginDenied() {
        User user = new User("testLoginAndReceiveGroup", "secret");
        Group group = new Group("testLoginGroup");
        group.addUser(user);
        try {
            int port = 6588;
            ExecutorService executorService = Executors.newCachedThreadPool();
            ServerController serverController = new ServerController(port);

            // Register a user and create a group.
            List<Message> outgoingMessages = List.of(
                    new Message(registerUser, user),
                    new Message(registerNewGroup, user, List.of(group)));
            Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
            Future<List<Message>> received = executorService.submit(testClient);
            List<Message> receivedMessages = received.get();
            List<Message> expected = List.of(
                    new Message(registrationOk, user),
                    new Message(newGroupOk, user),
                    new Message(updateGroup, user, List.of(group)));
            assertEquals(expected, receivedMessages);

            // Login with the same username but incorrect password. Should only receive login denied.
            User incorrectPassword = new User("testLoginAndReceiveGroup", "incorrect");
            outgoingMessages = List.of(new Message(login, incorrectPassword));
            testClient = TestClient.newTestRun(outgoingMessages, port);
            received = executorService.submit(testClient);
            receivedMessages = received.get();
            expected = List.of(new Message(loginDenied, incorrectPassword,
                    new ErrorMessage("Fel användarnamn eller lösenord, försök igen!")));
            assertEquals(expected, receivedMessages);
        } catch (InterruptedException | ExecutionException e) {
            // What do I do here?
        } finally {
            TestUtils.deleteUser(user);
            TestUtils.deleteGroup(group);
        }
    }

    /**
     *      A client tries to login with a username that doesn't exist.
     *      The expected outcome is that the client receives a loginDenied message.
     */
    @Test
    public void testLoginUnknownUser() {
        User user = new User("thisUserDoesntExist", "abcd");
        try {
            int port = 6589;
            ServerController serverController = new ServerController(port);
            List<Message> outgoing = List.of(new Message(login, user));
            List<Message> expected = List.of(new Message(loginDenied, user, new ErrorMessage("Fel användarnamn eller lösenord, försök igen!")));
            List<Message> received = sendAndReceive(outgoing, port);
            assertEquals(expected, received);
        } catch (InterruptedException | ExecutionException e) {
            // What do I do here?
        }
    }

    private List<Message> sendAndReceive(List<Message> outgoingMessages, int port) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
        Future<List<Message>> received = executorService.submit(testClient);
        return received.get();
    }
}
