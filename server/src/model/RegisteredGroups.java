package model;

import shared.transferable.Group;
import shared.transferable.User;

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


    /**
     * Searches through all registered groups and returns all the groups that
     * the specified user is a member of.
     * @param user the user whos groups are asked for.
     * @return an ArrayList with the name of the requested groups.
     */

    public ArrayList<String> getGroupsByUser(User user) {
        ArrayList<String> groupsOfUser = new ArrayList<>();
        for (Group group : registeredGroups) {
            ArrayList<User> users = group.getUsers();
            for (User u : users) {
                if (u.getUsername().equals(user.getUsername())) {
                    groupsOfUser.add(group.getName());
                }
            }
        }

        return groupsOfUser;

    }


}
