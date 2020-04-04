package com.mau.chorely.activities;

public class ListItem {
    private int mImageResource;
    private String mText1;
    private String mText2;

    public ListItem(int imageResource, String text1, String text2){
        this.mImageResource= imageResource;
        this.mText1 = text1;
        this.mText2 = text2;
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
}
