
import controller.ServerController;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.User;

public class RegistrationTests {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 6583;

    @Test
    public void testRegistration() {
        ServerController serverController = new ServerController();

        List<Message> outgoingMessages = new ArrayList<>();
        outgoingMessages.add(new Message(NetCommands.registerUser, new User("Fredrik", "secret")));
        TestClient testClient = new TestClient(IP, PORT, outgoingMessages);

        List<Message> received = testClient.getReceivedMessages();

        System.out.println(received);
    }


}

