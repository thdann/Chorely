package com.mau.chorely.activities.centralFragments.utils;

import androidx.annotation.Nullable;

import shared.transferable.Chore;
import shared.transferable.Reward;

public class ListItemCentral {
    String title;
    String description;
    int points;
    Chore chore;
    Reward reward;
    String username = "";
    String doneBy;
    public ListItemCentral(String title, String description, int points){
        this.title = title;
        this.description = description;
        this.points=points;
    }

    public Chore getChore() {
        return chore;
    }

    public Reward getReward() {
        return reward;
    }

    public ListItemCentral(Chore chore){
        title = chore.getName();
        description = chore.getDescription();
        points = chore.getScore();
        this.username = chore.getLastDoneByUser();
        this.doneBy = "Senast utförd av: ";
        this.chore = chore;
    }

    public ListItemCentral(Reward reward){
        title = reward.getName();
        description = reward.getDescription();
        points = reward.getRewardPrice();
        this.username = reward.getLastDoneByUser();
        this.doneBy = "Senast köpt av: ";
        this.reward = reward;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }
    public String getPoints(){
        return String.format("%d", points);
    }

    public String getDoneBy() {
        return doneBy;
    }

    public String getUsername() {
        return username;
    }

    public void updateItem(Chore chore){
        title = chore.getName();
        description = chore.getDescription();
        points = chore.getScore();
        this.chore = chore;
        this.username = chore.getLastDoneByUser();
    }

    public void updateItem(Reward reward){
        title = reward.getName();
        description = reward.getDescription();
        points = reward.getRewardPrice();
        this.reward = reward;
        this.username = reward.getLastDoneByUser();
    }

    public boolean allIsEqual(Object input){
        boolean ret;
        if(input instanceof Reward){
            if(((Reward) input).getName().equals(title) && ((Reward) input).getDescription().equals(description)
            && ((Reward) input).getRewardPrice() == points && ((Reward) input).getLastDoneByUser().equals(username)){
                ret = true;
            } else {
                ret = false;
            }
        } else if(input instanceof Chore){
            if(((Chore) input).getName().equals(title) && ((Chore) input).getDescription().equals(description)
            && ((Chore) input).getScore() == points && ((Chore) input).getLastDoneByUser().equals(username)){
                ret = true;
            } else {
                ret = false;
            }
        } else {
            ret = false;
        }
        return ret;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        boolean ret;
        if(obj instanceof Reward){
            if(((Reward) obj).getName().equals(title)){
                ret = true;
            } else {
                ret = false;
            }
        } else if(obj instanceof Chore){
            if(((Chore) obj).getName().equals(title)){
                ret = true;
            } else{
                ret = false;
            }
        } else {
            ret = false;
        }
        return ret;
    }
}
