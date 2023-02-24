package model;

import service.*;
import shared.transferable.Group;
import shared.transferable.User;

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
    UserQueries userQueries;
    GroupQueries groupQueries;
    ChoreRewardQueries choreRewardQueries;
    private LeaderboardQueries leaderboardQueries;

    private RegisteredGroups() {
    }

    /**
     * @return the singleton instance of RegisteredGroups.
     */
    public static RegisteredGroups getInstance() {
        return instance;
    }

//    /**
//     * Saves a Group object to its own file on the server
//     *
//     * @param group the Group object to be saved to file
//     * @return
//     */
//    public synchronized int writeGroupToFile(Group group) {
//        String filename = String.format("%s%s.dat", filePath, group.getGroupID());
//        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
//            oos.writeObject(group);
//            oos.flush();
//            messagesLogger.info("wrote group to file: " + filename);
//            return 1;
//        } catch (IOException e) {
//            messagesLogger.info("writeGroupToFile(group): " + e.getMessage());
//            return 0;
//        }
//    }
    /**
     * Saves a Group object to the database
     *
     * @param group the Group object to be saved to file
     * @return
     */
    public synchronized Group writeGroupToFile(Group group) {
        Group createdGroup = groupQueries.createGroup(group.getOwner(), group.getName(), group.getDescription());

        return createdGroup;
    }

//    /**
//     * Reads the file of a registered group requested by the group ID.
//     *
//     * @param id the id of the requested group.
//     * @return the requested group.
//     */
//    public synchronized Group getGroupFromFile(GenericID id) {
//        String filename = String.format("%s%s.dat", filePath, id);
//        Group foundGroup;
//
//        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
//            foundGroup = (Group) ois.readObject();
//            messagesLogger.info("retrieved group from file: " + foundGroup.toString());
//        } catch (IOException | ClassNotFoundException e) {
//            messagesLogger.info("getGroupToFile(id): " + e.getMessage());
//            return null;
//        }
//        return foundGroup;
//    }
    /**
     * Reads the file of a registered group requested by the group ID.
     *
     * @param groupID the id of the requested group.
     * @return the requested group.
     */
    public synchronized Group getGroupFromFile(int groupID) {
            return groupQueries.getGroup(groupID);
    }

//    /**
//     * Returns a requested group based on group ID.
//     *
//     * @param id the group ID.
//     * @return the requested group.
//     */
//    public synchronized Group getGroupByID(GenericID id) {
//        Group foundGroup;
//        if (groupIdAvailable(id)) {
//            return null;
//        } else {
//            foundGroup = getGroupFromFile(id);
//        }
//        return foundGroup;
//    }
    /**
     * Returns a requested group based on group ID.
     * todo remove duplicate method
     * @param groupID the group ID.
     * @return the requested group.
     */
    public synchronized Group getGroupByID(int groupID) {
        return getGroupFromFile(groupID);
    }

//    /**
//     * Updates the directory with the new updated group.
//     *
//     * @param group is the new updated version of the Group object to be saved to file.
//     */
//    public synchronized void updateGroup(Group group) {
//        File file = new File(filePath + group.getGroupID() + ".dat");
//        if (file.exists()) {
//            file.delete();
//        }
//        writeGroupToFile(group);
//    }
    /**
     * Updates the directory with the new updated group.
     * todo return something (eg updated group)
     * @param group is the new updated version of the Group object to be saved to file.
     */
    public synchronized void updateGroup(Group group) {
        groupQueries.updateGroupName(group.getIntGroupID(), group.getName());
        groupQueries.updateGroupDescription(group.getIntGroupID(), group.getDescription());
        groupQueries.updateMembers(group);
    }

//    /**
//     * Compares the groupID of a new group to already registered groups.
//     *
//     * @param newGroupId the requested groupID of a new group.
//     * @return true if groupID is available and false if it already exists.
//     */
//    public synchronized boolean groupIdAvailable(int newGroupId) {
//        File file = new File(filePath + newGroupId + ".dat");
//        if (file.exists()) {
//            return false;
//        }
//        return true;
//    }

//    /**
//     * Removes a group from the saved groups.
//     * @param group the group that is removed.
//     */
//    public synchronized void deleteGroup(Group group) {
//        File file = new File(filePath + group.getGroupID() + ".dat");
//        file.delete();
//    }
    /**
     * Removes a group from the saved groups.
     * @param group the group that is removed.
     */
    public synchronized void deleteGroup(Group group) {
        groupQueries.removeGroup(group);
    }

    public void setQueryPerformers(QueryExecutor queryExecutor) {
        System.out.println("QP set");
        this.userQueries = queryExecutor.getUserQueries();
        this.groupQueries = queryExecutor.getGroupQueries();
        this.choreRewardQueries = queryExecutor.getChoreRewardQueries();
        this.leaderboardQueries = queryExecutor.getLeaderboardQueries();
    }

    public boolean removeMember(User user, Group group) {
        return groupQueries.removeMember(user, group);
    }

    public Group addMember(User userToAdd, Group groupToAlter) {
        return groupQueries.addMember(userToAdd, groupToAlter);
    }
}
