package test.util;

import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.User;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

/**
 * Utility methods that are used in the automated tests.
 *
 * @author Fredrik Jeppsson
 */
public class TestUtils {

    /**
     * Deletes a User by deleting its corresponding file.
     * Used after having created a User during a test.
     *
     * @param user the user whose file is deleted.
     */
    public static void deleteUser(User user) {
        String username = user.getUsername();
        String filename = "files/users/" + username + ".dat";
        File file = new File(filename);
        file.delete();
    }

    /**
     * Deletes a Group by deleting its corresponding file.
     * Used after having created a Group during a test.
     *
     * @param group the group that is deleted.
     */
    public static void deleteGroup(Group group) {
        String filename = "files/groups/" + group.getGroupID() + ".dat";
        File file = new File(filename);
        file.delete();
    }

    /**
     * Helper method that runs a TestClient and returns the list of messages that the
     * client received during its connection.
     */
    public static List<Message> sendAndReceive(List<Message> outgoingMessages, int port) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Callable<List<Message>> testClient = TestClient.newTestRun(outgoingMessages, port);
        Future<List<Message>> received = executorService.submit(testClient);
        return received.get();
    }
}
