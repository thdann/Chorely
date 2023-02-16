package service;

import shared.transferable.Group;

public class GroupQueries {

    //add a group to the database
    public Group createGroup(Group groupToAdd) {
        Group createdGroup = new Group();
        return createdGroup;
    }
    //search for and retrieve from database
    public Group getGroup(int groupID) {
        Group foundGroup = new Group();
        return foundGroup;
    }
}
