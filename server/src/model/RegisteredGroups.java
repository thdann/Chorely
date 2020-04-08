package model;

import shared.transferable.Group;

import java.io.*;
import java.util.ArrayList;

/**
 * RegisteredGroups contains all the registered groups by reading and writing to a file stored on the server.
 * version 1.0 2020-04-08
 *
 * @author Theresa Dannberg
 */

public class RegisteredGroups {

    private String filename;
    private ArrayList<Group> registeredGroups;

    public RegisteredGroups() {
        this.filename = "files/registeredGroups.dat";

        File savedUsers = new File(filename);
        if (savedUsers.exists()) {
            readGroupsFromFile();

        } else {
            registeredGroups = new ArrayList<>();
            writeGroupsToFile();

        }
    }

    public void addGroup(Group newGroup) {
        registeredGroups.add(newGroup);
    }

    public void readGroupsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            registeredGroups = (ArrayList<Group>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.getMessage();

        }
    }

    public void writeGroupsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            oos.writeObject(registeredGroups);
            oos.flush();

        } catch (IOException e) {
            e.getMessage();

        }
    }




}
