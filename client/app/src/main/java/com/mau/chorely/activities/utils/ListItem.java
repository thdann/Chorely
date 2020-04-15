package com.mau.chorely.activities.utils;

import androidx.annotation.Nullable;

import shared.transferable.GenericID;

public class ListItem {
    private GenericID groupId;
    private int mImageResource;
    private String mText1;
    private String mText2;
    private String mText3;


    // TODO: 2020-04-10 Delete class

    private ListItem(int imageResource, GenericID groupId,  String text1, String text2, int text3){
        this.mImageResource= imageResource;
        this.mText1 = text1;
        this.mText2 = text2;
        this.mText3 = "" + text3;
        this.groupId = groupId;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public String getText1() {
        return mText1;
    }

    public String getText2() {
        return mText2;
    }

    public String getText3(){
        return mText3;
    }

    @Override
    public int hashCode() {
        return groupId.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
