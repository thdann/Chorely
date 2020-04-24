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
     * Getter for the model.
     * @return the reference to the model.
     */
    public static Model getModel(File appFilesDir){
        if(model == null){
            model = new Model(appFilesDir);
            Exception e = new Exception("MODEL UNREFERENCED");
            e.printStackTrace();
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
