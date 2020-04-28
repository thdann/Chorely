package model;

import shared.transferable.GenericID;
import shared.transferable.Group;
import shared.transferable.User;

import java.io.*;
import java.util.logging.Logger;

/**
 * RegisteredGroups handles all registered groups by reading and writing each Group object to a
 * separate file stored on the server.
 * version 1.0 2020-04-08
 *
 * @author Theresa Dannberg
 */

//TODO vilka olika scenarion kan hända som vi måste ha metoder för här???

public class RegisteredGroups {
    private final static Logger messagesLogger = Logger.getLogger("messages");
    private String filePath;
    private File directory;

    /**
     * Constructor
     */

    public RegisteredGroups() {
        this.filePath = "files/groups/";
        directory = new File(filePath);

    }

    /**
     * Saves a Group object to its own file on the server
     *
     * @param group the Group object to be saved to file
     */

    public void writeGroupToFile(Group group) {
        String filename = String.format("%s%s.dat", filePath, group.getGroupID());
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            oos.writeObject(group);
            oos.flush();
            System.out.println("write group to file " + filename);

        } catch (IOException e) {
            e.getMessage();

        }

    }

    public Group getGroupFromFile(GenericID id) {
        String filename = String.format("%s%s.dat", filePath, id);
        Group foundGroup = null;

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            foundGroup = (Group) ois.readObject();
            System.out.println(foundGroup.toString());

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return foundGroup;

    }

    /**
     * Updates the directory with the new updated group.
     *
     * @param group is the new updated version of the Group object to be saved to file.
     */

    public void updateGroup(Group group) {
        File file = new File(filePath + group.getGroupID() + ".dat");
        System.out.println("updatemetoden " + file.getPath());
        if (file.exists()) {
            file.delete();
        }

        writeGroupToFile(group);

    }

    /**
     * Compares the groupID of a new group to already registered groups.
     *
     * @param newGroupId the requested groupID of a new group.
     * @return true if groupID is available and false if it already exists.
     */

    public boolean groupIdAvailable(GenericID newGroupId) {
        File file = new File(filePath + newGroupId + ".dat");
        System.out.println(file.getPath());
        if (file.exists()) {
            return false;
        }

        return true;

    }

    public Group getGroupByID(GenericID id) {
        Group foundGroup = null;
        if (groupIdAvailable(id)) {
            //gruppen finns inte
            return null;
        } else {
            foundGroup = getGroupFromFile(id);
        }
        return foundGroup;

    }

    //TODO: obs ej klar med denna, vet inte riktigt vad vi ska med den till?
    public void loopRegisteredGroups() {
        for (File file : directory.listFiles()) {
            System.out.println(file.getName());
        }

    }

}
