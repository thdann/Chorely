package unitTests.controller.ClientHandlerTests;

import controller.ClientHandler;
import controller.ServerController;
import org.junit.jupiter.api.Test;
import shared.transferable.User;
import java.io.IOException;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogoutTest {
    /**
     * Method: logout(User user) - Test the logout method if the user is null
     * @throws IOException
     */
    @Test
    void testLogoutNull() throws IOException{
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);

        ClientHandler clientHandler = new ClientHandler(socket, serverController);
        assertEquals(false, clientHandler.logout(null));
    }

    /**
     * Method: logout(User user) - Test the logout method with a valid user
     * @throws IOException
     */
    @Test
    void testLogoutValidUser() throws IOException{
        Socket socket = new Socket("ip", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);

        ClientHandler clientHandler = new ClientHandler(socket, serverController);
        User user = new User("User1", "Pass1"); //valid username and password
        assertEquals(true, clientHandler.logout(user));
    }

    /**
     * Method: logout(User user) - Test the logout method if the user does not exist
     * @throws IOException
     */
    @Test
    void testLogoutInvalidUser() throws IOException{
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);
        ClientHandler clientHandler = new ClientHandler(socket, serverController);

        User user = new User("fake username", "fake password"); //invalid user
        assertEquals(false, clientHandler.logout(user));
    }

    /**
     * Method: logout(User user) - Test the logout method when user is empty
     * @throws IOException
     */
    @Test
    void testLogoutEmptyUser() throws IOException{
        Socket socket = new Socket("id", 1234); //some valid IP and port
        ServerController serverController = new ServerController(1234);

        ClientHandler clientHandler = new ClientHandler(socket, serverController);
        User user = new User("", ""); //empty user
        assertEquals(false, clientHandler.logout(user));
    }


}
