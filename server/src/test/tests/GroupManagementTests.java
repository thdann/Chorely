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
     *      1. User A registers.
     *      2. User B registers.
     *      3. User A creates a group and adds itself and user B to this group.
     *      4. User A and B's user files are updated with the new group membership.
     */
    @Test
    public void testAddingUserToGroup() {
        User userA = new User("testA", "secret");
        User userB = new User("testB", "secret");
        Group group = new Group("testAddingUserToGroup");
        group.addUser(userA);
        group.addUser(userB);
        try {
            int port = basePort + 1;
            ServerController serverController = new ServerController(port);
            registerUser(userA, port);
            registerUser(userB, port);
            createNewGroup(group, userA, port);
            User userAfromFile = registeredUsers.getUserFromFile(userA);
            User userBfromFile = registeredUsers.getUserFromFile(userB);
            assertEquals(userAfromFile.getGroups(), List.of(group.getGroupID()));
            assertEquals(userBfromFile.getGroups(), List.of(group.getGroupID()));
        } catch (InterruptedException | ExecutionException e) {
            // What do I do here?
        } finally {
            TestUtils.deleteUser(userA);
            TestUtils.deleteUser(userB);
            TestUtils.deleteGroup(group);
        }
    }

    /**
     * Tests the following case:
     *
     *      1. User A registers.
     *      2. User B registers.
     *      3. User A creates a group and adds itself and user B to the group.
     *      4. User A removes user B from the group.
     *
     */
    @Test
    public void testRemovingUserFromGroup() {
        User userA = new User("testA", "secret");
        User userB = new User("testB", "secret");
        Group group = new Group("testRemovingUserFromGroup");
        group.addUser(userA);
        group.addUser(userB);

        try {
            int port = basePort + 2;
            ServerController serverController = new ServerController(port);
            registerUser(userA, port);
            registerUser(userB, port);
            createNewGroup(group, userA, port);

            // Remove userB from the group.
            group.deleteUser(userB);

            // Send update with this modified group.
            updateGroup(userA, group, port);

            // Check user groups.
            User AfromFile = registeredUsers.getUserFromFile(userA);
            User BfromFile = registeredUsers.getUserFromFile(userB);

            assertEquals(List.of(group.getGroupID()), AfromFile.getGroups());
            assertEquals(List.of(), BfromFile.getGroups());
        } catch (InterruptedException | ExecutionException e) {
            // What do I do here?
        } finally {
            TestUtils.deleteUser(userA);
            TestUtils.deleteUser(userB);
            TestUtils.deleteGroup(group);
        }
    }

    private void updateGroup(User user, Group group, int port) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Message> outgoingMessages = List.of(
                new Message(login, user),
                new Message(updateGroup, user, List.of(group)));
        Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
        Future<List<Message>> received = executorService.submit(testClient);
        List<Message> receivedMessages = received.get();
        List<Message> expectedMessages = List.of(
                new Message(loginOk, user),
                new Message(updateGroup, user, List.of(group)),
                new Message(updateGroup, user, List.of(group)));
        assertEquals(expectedMessages, receivedMessages);
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
