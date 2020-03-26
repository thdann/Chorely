package com.mau.chorely.model;


public class ModelInstances {
    private static Model model;
    private ModelInstances(){}

    public static void Stop(){

    }

    public static void start(){

    }

    public static Model getInstance(){
        if(model == null){
            model = new Model();
            return model;
        }
        else{
            return model;
        }
    }
}
