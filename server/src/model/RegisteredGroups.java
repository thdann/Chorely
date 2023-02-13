package model;

import shared.transferable.GenericID;
import shared.transferable.Group;

import java.io.*;
import java.util.logging.Logger;

/**
 * RegisteredGroups handles all registered groups by reading and writing each Group object to a
 * separate file stored on the server.
 *
 * @author Theresa Dannberg, Fredrik Jeppsson
 */
public class RegisteredGroups {
    private final static Logger messagesLogger = Logger.getLogger("messages");
    private final static String filePath ="files/groups/";
    private final static RegisteredGroups instance = new RegisteredGroups();

    private RegisteredGroups() {
    }

    /**
     * @return the singleton instance of RegisteredGroups.
     */
    public static RegisteredGroups getInstance() {
        return instance;
    }

    /**
     * Saves a Group object to its own file on the server
     *
     * @param group the Group object to be saved to file
     * @return
     */
    public synchronized int writeGroupToFile(Group group) {
        String filename = String.format("%s%s.dat", filePath, group.getGroupID());
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            oos.writeObject(group);
            oos.flush();
            messagesLogger.info("wrote group to file: " + filename);
            return 1;
        } catch (IOException e) {
            messagesLogger.info("writeGroupToFile(group): " + e.getMessage());
            return 0;
        }
    }

    /**
     * Reads the file of a registered group requested by the group ID.
     *
     * @param id the id of the requested group.
     * @return the requested group.
     */
    public synchronized Group getGroupFromFile(GenericID id) {
        String filename = String.format("%s%s.dat", filePath, id);
        Group foundGroup;

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            foundGroup = (Group) ois.readObject();
            messagesLogger.info("retrieved group from file: " + foundGroup.toString());
        } catch (IOException | ClassNotFoundException e) {
            messagesLogger.info("getGroupToFile(id): " + e.getMessage());
            return null;
        }
        return foundGroup;
    }

    /**
     * Returns a requested group based on group ID.
     *
     * @param id the group ID.
     * @return the requested group.
     */
    public synchronized Group getGroupByID(GenericID id) {
        Group foundGroup;
        if (groupIdAvailable(id)) {
            return null;
        } else {
            foundGroup = getGroupFromFile(id);
        }
        return foundGroup;
    }

    /**
     * Updates the directory with the new updated group.
     *
     * @param group is the new updated version of the Group object to be saved to file.
     */
    public synchronized void updateGroup(Group group) {
        File file = new File(filePath + group.getGroupID() + ".dat");
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
    public synchronized boolean groupIdAvailable(GenericID newGroupId) {
        File file = new File(filePath + newGroupId + ".dat");
        if (file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * Removes a group from the saved groups.
     * @param group the group that is removed.
     */
    public synchronized void deleteGroup(Group group) {
        File file = new File(filePath + group.getGroupID() + ".dat");
        file.delete();
    }
}
