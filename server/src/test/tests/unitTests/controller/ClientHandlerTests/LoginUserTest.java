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


public class LoginUserTest {

    /**
     * Method: loginUser(Message message) - test loginUser if message is null
     * @throws IOException
     */
    @Test
    void testLoginNullMessage() throws IOException {
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);

        ClientHandler clientHandler = new ClientHandler(socket, serverController);
        assertEquals(false, clientHandler.loginUser(null));
    }

    /**
     * Method: loginUser(Message message) - test loginUser if user is null
     * @throws IOException
     */
    @ParameterizedTest
    @MethodSource("allCommands")
    void testLoginUserNullUse(NetCommands netCommand) throws IOException {
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);

        ClientHandler clientHandler = new ClientHandler(socket, serverController);
        Message message = new Message(netCommand, null);
        assertEquals(false, clientHandler.loginUser(message));
    }

    /**
     * Method: loginUser(Message message) - Test loginUser when the User object is empty
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
     * Method: loginUser(Message message) - test loginUser if message is valid
     * @throws IOException
     */
    @ParameterizedTest
    @MethodSource("allCommands")
    void testLoginUserInvalidCommand(NetCommands netCommand) throws IOException {
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);

        ClientHandler clientHandler = new ClientHandler(socket, serverController);
        User user = new User("User1","Pass1"); //valid username and password
        Message message  = new Message(netCommand, user);
        assertEquals(true, clientHandler.loginUser(message));
    }

    /**
     * Method used to return NetCommands
     * @return a NetCommand
     */
    static Stream<Arguments> allCommands(){
        return Stream.of(NetCommands.values()).map(Arguments::of);
    }
}
