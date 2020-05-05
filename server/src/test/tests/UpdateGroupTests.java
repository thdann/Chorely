import controller.ServerController;
import org.junit.jupiter.api.Test;
import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.User;
import test.util.TestClient;
import test.util.TestUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static shared.transferable.NetCommands.*;


public class UpdateGroupTests {

    /**
     * When a user logs in they should receive groups they belong to.
     */
    @Test
    public void testLoginAndReceiveGroup() {
        try {
            int port = 6586;
            ExecutorService executorService = Executors.newCachedThreadPool();
            ServerController serverController = new ServerController(port);

            // Register a user and create a group.
            User user = new User("testLoginAndReceiveGroup", "secret");
            List<Message> outgoingMessages = List.of(
                    new Message(registerUser, user),
                    new Message(registerNewGroup, user, List.of(new Group("testLoginGroup"))));
            Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
            Future<List<Message>> received = executorService.submit(testClient);
            List<Message> receivedMessages = received.get();
            List<Message> expected = List.of(new Message(registrationOk, user), new Message(newGroupOk, user));
            assertEquals(receivedMessages, expected);

            // Login and expect to find 


//            TestUtils.deleteUser(user);
        } catch (InterruptedException | ExecutionException e) {
            // What do I do here?
        }

    }
}
