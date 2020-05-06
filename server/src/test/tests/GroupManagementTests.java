import controller.ServerController;
import model.RegisteredUsers;
import org.junit.jupiter.api.Test;
import shared.transferable.*;
import test.util.TestClient;
import test.util.TestUtils;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static shared.transferable.NetCommands.*;

public class GroupManagementTests {
    private static final int basePort = 6600;
    private static final RegisteredUsers registeredUsers = new RegisteredUsers();

    /**
     * Tests the following case:
     *
     *      1. User A is registered.
     *      3. User A creates a group and adds itself to this group.
     *      4. User A's user file is updated with the new group membership.
     */
    @Test
    public void testAddingUserToGroup() {
        User userA = new User("testA", "secret");
        User userB = new User("testB", "secret");
        Group group = new Group("testAddingUserToGroup");
        group.addUser(userA);
        try {
            int port = basePort + 1;
            ServerController serverController = new ServerController(port);
            ExecutorService executorService = Executors.newCachedThreadPool();
            registerUser(userA, port);
            createNewGroup(group, userA, port);

            User user = registeredUsers.getUserFromFile(userA);
            assertEquals(user.getGroups(), List.of(group.getGroupID()));
        } catch (InterruptedException | ExecutionException e) {
            // What do I do here?
        } finally {
            TestUtils.deleteUser(userA);
            TestUtils.deleteUser(userB);
            TestUtils.deleteGroup(group);
        }
    }

    @Test
    public void testRemovingUserFromGroup() {

    }

    private void createNewGroup(Group group, User userA, int port) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Message> outgoingMessages = List.of(
                new Message(login, userA),
                new Message(registerNewGroup, userA, List.of(group)));
        Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
        Future<List<Message>> received = executorService.submit(testClient);
        List<Message> receivedMessages = received.get();
        List<Message> expectedMessages = List.of(
                new Message(loginOk, userA),
                new Message(newGroupOk, userA),
                new Message(updateGroup, userA, List.of(group)));
        assertEquals(expectedMessages, receivedMessages);
    }

    private void registerUser(User user, int port) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Message> outgoingMessages = List.of(new Message(registerUser, user));
        Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
        Future<List<Message>> received = executorService.submit(testClient);
        List<Message> receivedMessages = received.get();
        List<Message> expectedMessages = List.of(new Message(registrationOk, user));
        assertEquals(expectedMessages, receivedMessages);
    }
}
