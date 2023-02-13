package sytemtests;

import controller.ServerController;
import model.RegisteredUsers;
import org.junit.jupiter.api.Test;
import shared.transferable.*;
import test.util.TestClient;
import test.util.TestUtils;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static shared.transferable.NetCommands.*;
import static test.util.TestUtils.sendAndReceive;

public class GroupManagementTests {
    private static final int basePort = 6600;
    private static final RegisteredUsers registeredUsers = RegisteredUsers.getInstance();

    /**
     * Tests the following case:
     * <p>
     * 1. User A registers.
     * 2. User B registers.
     * 3. User A creates a group and adds itself and user B to this group.
     * 4. User A and B's user files are updated with the new group membership.
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
     * <p>
     * 1. User A registers.
     * 2. User B registers.
     * 3. User A creates a group and adds itself and user B to the group.
     * 4. User A removes user B from the group.
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

    /**
     * Tests the following case:
     * <p>
     * 1. User A registers.
     * 2. User A creates two groups.
     * 3. User A disconnects.
     * 4. User A logs in and expects to get 2 updateGroup message: one for each group.
     */
    @Test
    public void testRegisterTwoGroups() {
        User userA = new User("testA", "secret");
        Group group1 = new Group("testRegisterTwoGroups1");
        Group group2 = new Group("testRegisterTwoGroups2");
        group1.addUser(userA);
        group2.addUser(userA);

        try {
            int port = basePort + 3;
            ServerController serverController = new ServerController(port);
            List<Message> outgoing = List.of(
                    new Message(registerUser, userA),
                    new Message(registerNewGroup, userA, List.of(group1)),
                    new Message(registerNewGroup, userA, List.of(group2)));
            List<Message> expected = List.of(
                    new Message(registrationOk, userA),
                    new Message(newGroupOk, userA),
                    new Message(updateGroup, userA, List.of(group1)),
                    new Message(newGroupOk, userA),
                    new Message(updateGroup, userA, List.of(group2))
            );

            List<Message> received = sendAndReceive(outgoing, port);
            assertEquals(expected, received);

            outgoing = List.of(new Message(login, userA));
            expected = List.of(
                    new Message(loginOk, userA),
                    new Message(updateGroup, userA, List.of(group1)),
                    new Message(updateGroup, userA, List.of(group2))
            );
            received = sendAndReceive(outgoing, port);
            assertEquals(expected, received);

        } catch (InterruptedException | ExecutionException e) {
            // What do I do here?
        } finally {
            TestUtils.deleteUser(userA);
            TestUtils.deleteGroup(group1);
            TestUtils.deleteGroup(group2);
        }
    }

    /**
     * Simple test of delete group. Registers a group with a single user and then deletes it.
     */
    @Test
    public void testDeleteGroup() {
        User user = new User("testDeleteGroup", "secret");
        Group group = new Group("testDeletionGroup");
        group.addUser(user);
        try {
            int port = basePort + 4;
            ServerController serverController = new ServerController(port);
            List<Message> outgoing = List.of(
                    new Message(registerUser, user),
                    new Message(registerNewGroup, user, List.of(group)),
                    new Message(deleteGroup, user, List.of(group)));
            List<Message> expected = List.of(
                    new Message(registrationOk, user),
                    new Message(newGroupOk, user),
                    new Message(updateGroup, user, List.of(group)),
                    new Message(updateGroup, user, List.of(group))
            );
            List<Message> received = sendAndReceive(outgoing, port);
            Group deletedGroup = (Group) received.get(3).getData().get(0);
            List<User> users = deletedGroup.getUsers();
            assertEquals(expected, received);
            assertEquals(0, users.size());
            RegisteredUsers registeredUsers = RegisteredUsers.getInstance();
            User userFromFile = registeredUsers.getUserFromFile(user);
            List<GenericID> groups = userFromFile.getGroups();
            assertFalse(groups.contains(group.getGroupID()));
        } catch (InterruptedException | ExecutionException e) {
            // What do I do here?
        } finally {
            TestUtils.deleteUser(user);
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
