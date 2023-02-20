package service;

import shared.transferable.Group;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class GroupQueries {

    private QueryExecutor queryExecutor;

    public GroupQueries(QueryExecutor database){
        this.queryExecutor = database;
    }
    //add a group to the database
    public Group createGroup(String owner, String groupName, String description) {
        Group createdGroup = null;
        owner = makeSqlSafe(owner);
        groupName = makeSqlSafe(groupName);
        description = makeSqlSafe(description);
        String query = "INSERT INTO [Group] (group_name, group_owner, group_description) VALUES ('" + groupName + "', '" + owner + "', '" + description + "');" +
                "INSERT INTO [Member] VALUES ((select group_id from [Group] where group_name = '" + groupName + "' AND group_owner = '" + owner + "'), '" + owner + "', 0, 1);" +
                "SELECT * FROM [Group] WHERE group_id = (select group_id from [Group] where group_name = '" + groupName + "' AND group_owner = '" + owner + "');";
        try {
            ResultSet resultSet = queryExecutor.executeReadQuery(query);
            while (resultSet.next()) {
                int newGroupId = resultSet.getInt("group_id");
                String newGroupName = resultSet.getString("group_name");
                String newGroupDescription = resultSet.getString("group_description");
                createdGroup = new Group(newGroupId, newGroupName, newGroupDescription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return createdGroup;
    }


    public Group getGroup(int groupID) {
        //todo return group with all attributes, chores, members and rewards
        Group group = null;
        String query = "SELECT * FROM [Group] WHERE group_id = " + groupID + ";";

        try {
            ResultSet resultSet = queryExecutor.executeReadQuery(query);
            resultSet.next();
            group = new Group(
                    Integer.parseInt(resultSet.getString("group_id")),
                    resultSet.getString("group_name"),
                    resultSet.getString("group_owner"),
                    resultSet.getString("group_description"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return group;
    }

    public Group updateGroupName(int id, String name) {
        Group updatedGroup = null;

        String query = "UPDATE [Group]\nSET group_name = '" + makeSqlSafe(name)
                + "'\nWHERE group_id = " + id + ";";

        try {
            queryExecutor.executeUpdateQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return updatedGroup = getGroup(id);
    }

    public Group updateGroupDescription(int id, String description) {
        Group updatedGroup = null;

        String query = "UPDATE [Group]\nSET group_description = '" + makeSqlSafe(description)
                + "'\nWHERE group_id = " + id + ";";

        try {
            queryExecutor.executeUpdateQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return updatedGroup = getGroup(id);
    }

    /**
     * Method adds a member to a given group,
     * with, by default, 0 points and no admin rights
     *
     * @param userName user to add to group
     * @param group group to be updated
     * @return the updated group if successful, null if not
     */
    public Group addMember(String userName, Group group) {
        //todo return the group with updated attributes, chores, members and rewards
        Group biggerGroup = null;
        int groupID = group.getIntGroupID();
        boolean success = false;
        userName = makeSqlSafe(userName);
        String query = "INSERT INTO [Member] VALUES ("+ groupID + ", '" + userName + "', 0 , 0);";
        try {
            queryExecutor.executeUpdateQuery(query);
            success = true;
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        if (success) {
            biggerGroup = group;
            biggerGroup.addUser(userName);
        }
        return biggerGroup;
    }

    /**
     * Remove a member from a group, if the member is the owner,
     * remove the group and all it's members/chores/rewards
     * @param userName name of member to remove
     * @param group group to be updated
     * @return true when success
     */
    public boolean removeMember(String userName, Group group) {
        boolean memberRemoved = false;
        String owner = group.getOwner();
        String query;
        if (owner.equals(userName)) {
            query =          //remove chores
                    "DELETE FROM [Chore] WHERE group_id = '" + group.getGroupID() + "');" +
                            //remove rewards
                            "DELETE FROM [Reward] WHERE group_id = '" + group.getGroupID() + "');" +
                            //remove members
                            "DELETE FROM [Member] WHERE group_id = '" + group.getGroupID() + "');" +
                            //finally, remove group
                            "DELETE FROM [Group] WHERE group_id = '" + group.getGroupID() + "';";

        }
        else {
            query = "DELETE FROM [Member] WHERE user_name = '" + userName + "' AND group_id = '"+ group.getGroupID() + "'";
        }
        try {
            Statement statement = queryExecutor.beginTransaction();
            statement.executeUpdate(query);
            queryExecutor.endTransaction();
            memberRemoved = true;
        }
        catch (SQLException sqlException) {
            try {
                System.out.println(sqlException);
                queryExecutor.rollbackTransaction();
            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return memberRemoved;
    }
    private static String makeSqlSafe(String string) {
        return string.replace("'", "''");
    }


}
