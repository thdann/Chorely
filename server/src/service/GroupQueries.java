package service;

import shared.transferable.Group;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupQueries {

    private QueryExecutor database;

    public GroupQueries(QueryExecutor database){
        this.database = database;
    }
    //add a group to the database
    public Group createGroup(String owner, String groupName, String description) {
        Group createdGroup = null;
        owner =makeSqlSafe(owner);
        groupName =makeSqlSafe(groupName);
        description =makeSqlSafe(description);
        String query = "INSERT INTO [Group] (group_name, group_owner, group_description) VALUES ('" + groupName + "', '" + owner + "', '" + description + "');" +
                "INSERT INTO [Member] VALUES ((select group_id from [Group] where group_name = '" + groupName + "' AND group_owner = '" + owner + "'), '" + owner + "', 0, 1);" +
                "SELECT * FROM [Group] WHERE group_id = (select group_id from [Group] where group_name = '" + groupName + "' AND group_owner = '" + owner + "');";
        try {
            ResultSet resultSet = database.executeReadQuery(query);
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
        Group gotGroup = null;
        return gotGroup;
    }

    public Group updateGroup(Group groupToUpdate) {
        //todo return the group with updated attributes, chores, members and rewards
        Group updatedGroup = null;
        return updatedGroup;
    }

    public Group addMember(String userName) {
        //todo return the group with updated attributes, chores, members and rewards
        Group newGroup = null;
        return newGroup;
    }
    private static String makeSqlSafe(String string) {
        return string.replace("'", "''");
    }


}
