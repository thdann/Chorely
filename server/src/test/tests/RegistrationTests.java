import controller.ServerController;
import org.junit.jupiter.api.Test;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.User;
import test.util.TestClient;
import test.util.TestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationTests {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 6583;

    @Test
    public void testRegistration() {
        ServerController serverController = new ServerController();
        User user = new User("Fredrik", "secret");
        List<Message> outgoingMessages = new ArrayList<>();
        outgoingMessages.add(new Message(NetCommands.registerUser, user));
        TestClient testClient = new TestClient(IP, PORT, outgoingMessages);
        List<Message> received = testClient.getReceivedMessages();
        List<Message> expected = Collections.singletonList(new Message(NetCommands.registrationOk, user));
        assertEquals(received, expected);
        boolean deleted = TestUtils.deleteUser(user);
        assertTrue(deleted);
    }
}

