package com.mau.chorely.activities.utils;

public class ListItem {
    private int mImageResource;
    private String mText1;
    private String mText2;
    private String mText3;

    public ListItem(int imageResource, String text1, String text2, int text3){
        this.mImageResource= imageResource;
        this.mText1 = text1;
        this.mText2 = text2;
        this.mText3 = "" + text3;
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
}
