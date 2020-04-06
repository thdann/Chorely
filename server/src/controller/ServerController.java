package controller;

/**
 * ServerController handles the over all logic on the server side.
 * The class contains the main method that starts the program and makes it possible for a client
 * to obtain a connection by creating an instance of ServerNetwork.
 * version 1.0 2020-03-23
 *
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */


import model.RegisteredUsers;

import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import shared.transferable.User;


import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerController implements ClientListener {

    private RegisteredUsers registeredUsers;
    private ServerNetwork network;
    private LinkedBlockingDeque<ArrayList<Transferable>> clientTaskBuffer; //TODO: här läggs alla inkommande arraylists från klienterna.
    private BetterNameComingSoon betterNameComingSoon;

    public ServerController() {
        registeredUsers = new RegisteredUsers();
        clientTaskBuffer = new LinkedBlockingDeque<>();
        network = new ServerNetwork(this, 6583);
        betterNameComingSoon = new BetterNameComingSoon();
        Thread t1 = new Thread(betterNameComingSoon);
        t1.start();

    }

    @Override
    public void sendList(ArrayList<Transferable> list) {
        //TODO: lägg in listan i en buffer så att controllern kan hantera listan sen i egen tråd.
        clientTaskBuffer.add(list);
    }

    public void handleClientTask(ArrayList<Transferable> list) {

        NetCommands command = (NetCommands) list.get(0);
        User user = (User) list.get(1);

        // TODO:  1. Plocka ut första "uppgiften" från clientTask
        // TODO: 2. Plocka ut tex position[0]  (om där är enumet)


        // TODO: 3. Skicka in enumet i en switch sats som kontrollerar vilket enum där är
        // TODO: 4. En metod per enum, namnen ska vara talande för vad som händer i de olika scenarion
        // TODO: 5. Skriv metoderna för de olika situationerna.

        switch (command) {
            case register:
                registerUser(user);
                //TODO: metodnamnfördetta(); ska skicka tillbaka registrationOk eller registrationDenied, detta räcker för första sprinten...
                break;
            default:
                //TODO:  kod för default case. Vad kan man skriva här?
                break;
        }
    }

    public void registerUser(User user) {
        //1 kontroll användarnamn: får inte vara tomt, får inte vara ett namn som finns redan
        if (registeredUsers.userNameAvailable(user.getUsername())) {
            if (user.getPassword() != "") {         //2 kontroll password: får inte vara tomt/Null?
                registeredUsers.addRegisteredUser(user);  //3 förutsatt att ovan är ok - lägg till new user i registeredUsers
                //Skicka meddelande till klienten att användaren är registerad ok.

            }
        } else {
            //skicka meddelande till klienten att användarnamnet är upptaget.
        }

    }



    public void addRegisteredUser(User newUser) {
        registeredUsers.addRegisteredUser(newUser);
    }

    public static void main(String[] args) {
        ServerController prog = new ServerController();
        //TODO: Sätt upp servertråd (extenda thread) Eller se till att main fortsätter köra...
    }


    private class BetterNameComingSoon implements Runnable { //TODO: Kom på bättre namn för klassen.

        public BetterNameComingSoon() {


        }


        public void run() {
            ArrayList<Transferable> list;
            while (true) {

                try {
                    list = clientTaskBuffer.take();
                    handleClientTask(list);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }

    }
}


