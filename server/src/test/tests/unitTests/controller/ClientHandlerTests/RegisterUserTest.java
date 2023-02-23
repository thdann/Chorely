package unitTests.controller.ClientHandlerTests;

import controller.ClientHandler;
import controller.ServerController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.User;
import java.io.IOException;
import java.net.Socket;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    /**
     * Method: registerUser(Message message) - Test registerUser when the Message object is null
     * @throws IOException
     */
    @Test
    void registerUserMessageIsNull() throws IOException {
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);
        ClientHandler clientHandler = new ClientHandler(socket, serverController);

        assertEquals(false, clientHandler.registerUser(null));
    }
    /**
     * Method: registerUser(Message message) - Test registerUser when the User object is null
     * @throws IOException
     */
    @ParameterizedTest
    @MethodSource("allCommands")
    void registerUserIsNull(NetCommands netCommand) throws IOException {
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);
        ClientHandler clientHandler = new ClientHandler(socket, serverController);

        Message message  = new Message(netCommand, null);
        assertEquals(false, clientHandler.registerUser(message));
    }
    /**
     * Method: registerUser(Message message) - Test registerUser when the User object is empty
     * @throws IOException
     */
    @ParameterizedTest
    @MethodSource("allCommands")
    void registerUserEmpty(NetCommands netCommand) throws IOException {
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);
        ClientHandler clientHandler = new ClientHandler(socket, serverController);

        User user = new User("","");
        Message message = new Message(netCommand, user);
        assertEquals(false, clientHandler.registerUser(message));
    }
    /**
     * Method: registerUser(Message message) - Test registerUser when the Message object is valid
     * @throws IOException
     */
    @ParameterizedTest
    @MethodSource("allCommands")
    void registerUserOk(NetCommands netCommand) throws IOException {
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);
        ClientHandler clientHandler = new ClientHandler(socket, serverController);

        User user = new User("User1","Pass1"); //valid username and password
        Message message  = new Message(netCommand, user);
        assertEquals(true, clientHandler.registerUser(message));
    }

    /**
     * Method used to return NetCommands
     * @return a NetCommand
     */
    static Stream<Arguments> allCommands(){
        return Stream.of(NetCommands.values()).map(Arguments::of);
    }

}