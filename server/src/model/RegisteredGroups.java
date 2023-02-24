package model;

import service.*;
import shared.transferable.Group;
import shared.transferable.User;

/**
 * RegisteredGroups handles all registered groups by reading and writing each Group object to a
 * separate file stored on the server.
 *
 * @author Theresa Dannberg, Fredrik Jeppsson
 */
public class RegisteredGroups {

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

    /**
     * Reads the file of a registered group requested by the group ID.
     *
     * @param groupID the id of the requested group.
     * @return the requested group.
     */
    public synchronized Group getGroupFromFile(int groupID) {
            return groupQueries.getGroup(groupID);
    }

    /**
     * Updates the directory with the new updated group.
     * @param group is the new updated version of the Group object to be saved to file.
     */
    public synchronized void updateGroup(Group group) {
        //update name
        groupQueries.updateGroupName(group.getIntGroupID(), group.getName());
        //update description
        groupQueries.updateGroupDescription(group.getIntGroupID(), group.getDescription());
        //update members
        groupQueries.updateMembers(group);
        //update chores
        groupQueries.updateChores(group);
        //update rewards
        groupQueries.updateRewards(group);
        //update leaderboard
        leaderboardQueries.updateLeaderboard(group);
    }

    /**
     * Removes a group from the saved groups.
     * @param group the group that is removed.
     */
    public synchronized void deleteGroup(Group group) {
        groupQueries.removeGroup(group);
    }

    public void setQueryPerformers(QueryExecutor queryExecutor) {
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
