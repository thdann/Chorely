package model;

import shared.transferable.GenericID;
import shared.transferable.Group;

import java.io.*;

/**
 * RegisteredGroups handles all registered groups by reading and writing each Group object to a
 * separate file stored on the server.
 * version 1.0 2020-04-08
 *
 * @author Theresa Dannberg
 */

//TODO vilka olika scenarion kan hända som vi måste ha metoder för här???

public class RegisteredGroups {

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

    public boolean groupIdAvailable(GenericID newGroupId) {
        File file = new File(filePath + newGroupId + ".dat");
        System.out.println(file.getPath());
        if (file.exists()) {
            return false;
        }

        return true;

    }

    public void loopRegisteredGroups() {
        for (File file : directory.listFiles()) {
            System.out.println(file.getName());
        }

    }

}
