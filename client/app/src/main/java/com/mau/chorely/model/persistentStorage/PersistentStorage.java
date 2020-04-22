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

import java.util.ArrayList;
import java.util.Date;



import shared.transferable.Group;
import shared.transferable.User;

/**
 * This class handles all persistent storage of the client.
 * It takes a reference to the application files directory upon creation.
 *
 * @author Timothy Denison
 */
public class PersistentStorage {
    private File filesDir;
    private static final String USER_FILE_NAME = "/user.cho";
    private static File userFile;
    private static File groupDir;

    private PersistentStorage() {
    }

    public PersistentStorage(File filesDir) {
        this.filesDir = filesDir;
        userFile = new File(filesDir.getAbsolutePath() + USER_FILE_NAME);
        groupDir = new File(filesDir.getAbsolutePath() + "/groups");
    }

    /**
     * Method to update a saved user. (This is the user of the client.)
     *
     * @param user The new user.
     */
    public void updateUser(User user) {
        if (userFile.exists()) {
            userFile.delete();
        }
        try (ObjectOutputStream outputStream = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(userFile)))) {
            outputStream.writeObject(user);
            outputStream.flush();
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


    // TODO: 2020-04-13 Ska returnera sant om ändringar gjorts. falskt om identisk grupp finns sparad
    // TODO: 2020-04-15 om ändring görs ska även det sparade user objektet ändras för att inehålla den nya gruppen
    public boolean saveOrUpdateGroup(Group newGroup) {
        boolean groupUpdated = false;
        if(!groupDir.exists()){
            groupDir.mkdirs();
        }

        File groupFile = new File(groupDir.getPath()+ "/" + newGroup.getGroupID()+".cho");
        if (groupFile.exists()) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(
                    new FileInputStream(groupFile)))) {
                Group oldGroup = (Group) inputStream.readObject();

                if (!oldGroup.allIsEqual(newGroup)) {
                    groupFile.delete();
                    ObjectOutputStream outputStream = new ObjectOutputStream(new
                            BufferedOutputStream(new FileOutputStream(groupFile)));

                    outputStream.writeObject(newGroup);
                    outputStream.flush();
                    outputStream.close();
                    System.out.println("New group saved----------------------------");
                    groupUpdated = true;
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("ERROR HANDLING GROUP FILE: " + e.getMessage());
            }
        } else {

            try (ObjectOutputStream outputStream = new ObjectOutputStream(new
                    BufferedOutputStream(new FileOutputStream(groupFile)))) {

                outputStream.writeObject(newGroup);
                outputStream.flush();
            } catch (IOException e) {
                System.out.println("ERROR CREATING NEW GROUP FILE: " + e.getMessage());
            }
        }
        System.out.println("Saved new group = " + groupUpdated);

        if(groupUpdated){
            User user = getUser();
            user.addGroupMembership(newGroup.getGroupID());
        }

        return groupUpdated;
    }


    public ArrayList<Group> getGroups() {
        ArrayList<Group> ret = new ArrayList<>();
        File[] groupFiles = groupDir.listFiles();
        if (groupFiles != null) {
            for (File groupFile : groupFiles) {
                try (ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(
                        new FileInputStream(groupFile)))){
                    ret.add((Group) inputStream.readObject());
                } catch (IOException  | ClassNotFoundException e){
                    System.out.println("ERROR READING GROUP FILE: " + e.getMessage());
                }
            }
        }
        System.out.println("GROUP LIST SIZE: " + ret.size());
        return ret;
    }

    public void deleteGroup(Group group){
        //remove group from user file
        User user = getUser();
        user.removeGroupMembership(group.getGroupID());
        updateUser(user);

        //delete file
        File fileToDelete = new File(groupDir.getAbsolutePath() + "/" + group.getGroupID() + ".cho");

        if(fileToDelete.exists()){
            fileToDelete.delete();
            System.out.println("GROUP DELETED");
        }
    }

    public void setSelectedGroup(Group group){

    }

    // TODO: 2020-04-22 Implementerad
    public Group getSelectedGroup(){
        return null;
    }

}
