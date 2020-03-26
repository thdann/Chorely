package Controller;

/**
 * ServerController handles the over all logic on the server side.
 * The class contains the main method that starts the program and makes it possible for a client
 * to obtain a connection by creating an instance of ServerNetwork.
 *
 * version 1.0 2020-03-23
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */

import Model.NetCommands;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerController implements ClientListener {

    private ServerNetwork network;
    private LinkedBlockingDeque<ArrayList<Transferrable>> clientTask; //här läggs alla inkommande arraylists från klienterna.

    public ServerController() {
        clientTask = new LinkedBlockingDeque<>();
        network = new ServerNetwork(this, 6583);
    }


    @Override
    public void sendList(ArrayList<Transferrable> message) {
        //lägg in listan i en buffer så att controllern kan hantera listan sen i egen tråd.
        clientTask.add(message);
    }

    public void handleClientTask() {

        // 1. Plocka ut första "uppgiften" från clientTask
        // 2. Plocka ut tex position[0]  (om där är enumet)
        Transferrable command = clientTask.getFirst().get(0);

        // 3. Skicka in enumet i en switch sats som kontrollerar vilket enum där är
        // 4. En metod per enum, namnen ska vara talande för vad som händer i de olika scenarion
        // 5. Skriv metoderna för de olika situationerna.

        switch ((NetCommands)command) {
            case register:
                //metodnamnfördetta(); ska skicka tillbaka registrationOk eller registrationDenied, detta räcker för första sprinten...
                break;
            default:
                // kod för default case. Vad kan man skriva här?
                break;
        }
    }


    public static void main(String[] args) {
        ServerController prog = new ServerController();
        //Sätt upp servertråd (extenda thread) Eller se till att main fortsätter köra...
    }

}
