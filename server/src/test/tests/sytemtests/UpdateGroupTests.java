package sytemtests;

import controller.ServerController;
import org.junit.jupiter.api.Test;
import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.User;
import test.util.TestClient;
import test.util.TestUtils;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static shared.transferable.NetCommands.*;


public class UpdateGroupTests {
    private static final int basePort = 6900;

    /**
     * When a user logs in they should receive groups that they belong to.
     */
    @Test
    public void testLoginAndReceiveGroup() {
        User user = new User("testLoginAndReceiveGroup", "secret");
        Group group = new Group("testLoginGroup");
        group.addMember(user);

        try {
            int port = basePort + 1;
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

            // Login and expect to receive the group in an update group message.
            outgoingMessages = List.of(new Message(login, user));
            testClient= TestClient.newTestRun(outgoingMessages, port);
            received = executorService.submit(testClient);
            receivedMessages = received.get();
            expected = List.of(new Message(loginOk, user), new Message(updateGroup, user, List.of(group)));
            assertEquals(expected, receivedMessages);
        } catch (InterruptedException | ExecutionException e) {
        } finally {
            TestUtils.deleteUser(user);
            TestUtils.deleteGroup(group);
        }

    }
}
