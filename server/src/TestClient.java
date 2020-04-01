import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TestClient {

    public TestClient(String ip, int port) {
       // String message;
        try (Socket socket = new Socket(ip, port)) {
          //  ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
           // message = (String) ois.readObject();
           // System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestClient test = new TestClient("127.0.0.1", 6583);
    }

}
