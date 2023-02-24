/**
 * Class to manage all persistant storage on the client.
 */

package com.mau.chorely.model.persistentStorage;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;


import shared.transferable.GenericID;
import shared.transferable.Group;
import shared.transferable.User;

/**
 * This class handles all persistent storage of the client.
 * It takes a reference to the application files directory upon creation.
 *
 * @author Timothy Denison, Theresa Dannberg, Emma Svensson
 */
public class PersistentStorage {
    private File filesDir;
    private static final String USER_FILE_NAME = "/user.cho";
    private static final String SEL_GROUP_FILE_NAME = "/selGroup.cho";
    private static File userFile;
    private static File groupDir;
    private static File selectedGroup;

    private PersistentStorage() {
    }

    public PersistentStorage(File filesDir) {
        this.filesDir = filesDir;
        userFile = new File(filesDir.getAbsolutePath() + USER_FILE_NAME);
        groupDir = new File(filesDir.getAbsolutePath() + "/groups");
        selectedGroup = new File(filesDir.getAbsolutePath() + SEL_GROUP_FILE_NAME);
        deleteAllGroups();

    }

    /**
     * Method to delete selected group.
     */
    public void deleteSelectedGroup() {
//     if(selectedGroup.exists()) {
//         selectedGroup.delete();
//     }
    }

    /**
     * Method to delete current user.
     */
    public void deleteUser() {
        if(userFile.exists()) {
            userFile.delete();
        }
    }

    /**
     * Method to delete all groups.
     */
    public void deleteAllGroups(){
        if(groupDir.exists()) {
            File[] groupFiles = groupDir.listFiles();
            for(File file : groupFiles){
                System.out.println("Deleted File: " + file.toString());
                file.delete();
            }
        }
    }

    /**
     * Method to update a saved user. (This is the user of the client.)
     *
     * @param user The new user.
     */
    public void updateUser(User user) {
        if (userFile.exists()) {
            userFile.delete();
            System.out.println("User file deleted");
        }
        try (ObjectOutputStream outputStream = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(userFile)))) {
            outputStream.writeObject(user);
            outputStream.flush();
            System.out.println("User file written");
        } catch (IOException e) {
            System.out.println(new Date() + "File output stream: thrown exception " +
                    "trying to write user." + e.getMessage());
        }
    }


    /**
     * Method to get the saved user of the client.
     *
     * @return Returns the saved user if there is one. Otherwise null.
     */
    public User getUser() {
        User user = null;
        if (userFile.exists()) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(
                    new FileInputStream(userFile)))) {
                user = (User) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(new Date() + "File input stream: thrown exception " +
                        "trying to read user." + e.getMessage());
            }
        }
        // null is returned if an exception is thrown, or if there is no file saved.
        return user;
    }

    /**
     * Method to save or overwrite group. If the group doesent already exist it is simply stored,
     * otherwise overwritten.
     * @param newGroup Group to save.
     * @return returns true if there was an update to existing group.
     */
    public synchronized boolean saveOrUpdateGroup(Group newGroup) {
        boolean groupUpdated = false;
        if (!groupDir.exists()) {
            groupDir.mkdirs();
        }
        System.out.println("Group to save" + newGroup);
        File groupFile = new File(groupDir.getPath() + "/" + newGroup.getIntGroupID() + ".cho");
        if (groupFile.exists()) {
            System.out.println("FILE EXISTS");
            try (ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(
                    new FileInputStream(groupFile)))) {
                Group oldGroup = (Group) inputStream.readObject();


                    groupFile.delete();
                    ObjectOutputStream outputStream = new ObjectOutputStream(new
                            BufferedOutputStream(new FileOutputStream(groupFile)));

                    outputStream.writeObject(newGroup);
                    outputStream.flush();
                    outputStream.close();
                    System.out.println("New group saved----------------------------");
                    groupUpdated = true;


            } catch (IOException | ClassNotFoundException e) {
                System.out.println("ERROR HANDLING GROUP FILE: " + e.getMessage());
            }
        } else {

            try (ObjectOutputStream outputStream = new ObjectOutputStream(new
                    BufferedOutputStream(new FileOutputStream(groupFile)))) {

                outputStream.writeObject(newGroup);
                outputStream.flush();
                groupUpdated = true;
            } catch (IOException e) {
                System.out.println("ERROR CREATING NEW GROUP FILE: " + e.getMessage());
            }
        }
        System.out.println("Saved new group = " + groupUpdated);

        if (groupUpdated) {
            User user = getUser();
            user.addGroupMembership(newGroup);
        }

        return groupUpdated;
    }


    /**
     * getter for all saved groups
     * @return all saved groups.
     */
    public synchronized ArrayList<Group> getGroups() {
        ArrayList<Group> ret = new ArrayList<>();

        File[] groupFiles = groupDir.listFiles();
        if (groupFiles != null) {
            for (File groupFile : groupFiles) {
                try (ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(
                        new FileInputStream(groupFile)))) {
                    ret.add((Group) inputStream.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("ERROR READING GROUP FILE: " + e.getMessage());
                }
            }
        }
        System.out.println("GROUP LIST SIZE: " + ret.size());
        return ret;
    }

    /**
     * Method to delete a selected group.
     * @param group group to delete.
     */
    public void deleteGroup(Group group) {
        //remove group from user file
        User user = getUser();
        user.removeGroupMembership(group);
        updateUser(user);

        //delete file
        File fileToDelete = new File(groupDir.getAbsolutePath() + "/" + group.getGroupID() + ".cho");

        if (fileToDelete.exists()) {
            fileToDelete.delete();
            System.out.println("GROUP DELETED");
        }
    }

    public synchronized void setSelectedGroup(Group inGroup) {
        Group group = inGroup;
        if (selectedGroup.exists()) {
            selectedGroup.delete();
        }
        try (ObjectOutputStream ois = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(selectedGroup)))) {
            ois.writeObject(group.getIntGroupID());
            ois.flush();
        } catch (IOException e) {
            System.out.println(new Date() + "File output stream: thrown exception " +
                    "trying to write group." + e.getMessage());
        }
    }

    /**
     * Method to get the current selected group.
     * @return
     */
    public synchronized Group getSelectedGroup() {
        Group group = null;
        if (selectedGroup.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(selectedGroup)))) {
                int id = (int) ois.readObject();
                try(ObjectInputStream groupInput = new ObjectInputStream(new BufferedInputStream(new FileInputStream(groupDir +"/" + id +".cho")))){
                    group = (Group) groupInput.readObject();
                } catch (IOException f){
                    System.out.println("ERROR READING SELECTED GROUP: " + f.getMessage());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(new Date() + "File input stream: thrown exception " +
                        "trying to read group." + e.getMessage());
            }
        }
        return group;
    }
}
