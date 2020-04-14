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
import java.util.Date;


import shared.transferable.User;

public class PersistentStorage {
    private File filesDir;
    private static final String USER_FILE_NAME = "/user.cho";
    private static File userFile;
    private static File groupDir;

    public PersistentStorage(File filesDir) {
        this.filesDir = filesDir;
        userFile = new File(filesDir.getAbsolutePath() + USER_FILE_NAME);
    }

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


    public User getUser(){
        User user = null;
        if(userFile.exists()){
            try(ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(
                    new FileInputStream(userFile)))){
                    user = (User) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e){
                System.out.println(new Date() + "File input stream: thrown exception " +
                        "trying to read user." + e.getMessage());
            }
        }
        // null is returned, if an exception is thrown or if there is no file saved.
        return user;
    }
}
