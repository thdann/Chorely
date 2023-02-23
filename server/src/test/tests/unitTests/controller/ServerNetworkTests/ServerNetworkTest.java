package unitTests.controller.ServerNetworkTests;

import controller.ServerNetwork;
import org.junit.jupiter.api.Test;
import controller.ServerController;

/** 
* ServerNetwork Tester. 
* 
* @author <Authors name> 
* @since <pre>feb. 8, 2023</pre> 
* @version 1.0 
*/ 
public class ServerNetworkTest {

    /**
     * Method: run()
     */
    @Test
    void run() {
        ServerController serverController = new ServerController(1234); //ServerController with valid port
        ServerNetwork serverNetwork = new ServerNetwork(serverController, 1234); //ServerNetwork with valid ServerController and port
        Thread thread = new Thread(serverNetwork);
        thread.start();
    }
}
