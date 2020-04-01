import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import shared.transferable.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class TestClient {

    public TestClient(String ip, int port) {

        try (Socket socket = new Socket(ip, port)) {

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            ArrayList<Transferable> list = new ArrayList<>();
            list.add(NetCommands.register);
            list.add(new User("fredrik","fred"));
            list.add(new User("Angelica", "ang"));

            oos.writeObject(list);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestClient test = new TestClient("127.0.0.1", 6583);
    }

}
