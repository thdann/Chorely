/**
 *
 */


package com.mau.chorely.activities.utils;


import com.mau.chorely.model.Model;

import java.io.File;

/**
 * This class enables callbacks between the activities and the model.
 * It holds a static reference to both model and the presenter. These are instantiated only once
 * and shared throughout the application.
 * @author Timothy Denison
 */
public class BridgeInstances {
    private static Model model;
    private static Presenter presenter;
    private BridgeInstances(){}

    /**
     * method that is called upon application start. Takes a reference to the application
     * files-directory
     * @param appFilesDir Files directory
     */
    public static void instantiateModel(File appFilesDir){
        if(model == null) {
            model = new Model(appFilesDir);
        }
    }

    /**
     * Getter for the model.
     * @return the reference to the model.
     */
    public static Model getModel(){
        if(model == null){
            throw new RuntimeException("Model unreferenced");
            //model = new Model();
        }
        return model;
    }

    /**
     * Getter for the presenter.
     * @return reference to the presenter.
     */
    public static Presenter getPresenter(){
        if (presenter == null){
            presenter = new Presenter();
        }
        return presenter;
    }
}
