package com.mau.chorely.activities.centralFragments.utils;

import shared.transferable.Chore;
import shared.transferable.Reward;

public class ListItemCentral {
    String title;
    String description;
    int points;
    public ListItemCentral(String title, String description, int points){
        this.title = title;
        this.description = description;
        this.points=points;
    }

    public ListItemCentral(Chore chore){
        title = chore.getName();
        description = chore.getDescription();
        points = chore.getScore();
    }

    public ListItemCentral(Reward reward){
        title = reward.getName();
        description = reward.getDescription();
        points = reward.getRewardPrice();
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
}
