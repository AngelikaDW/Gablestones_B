package com.aleaf.gablestones;

/**
 * Created by angelika on 06/07/2017.
 */

public class Details {

    private int mCategoryId;
    private int mStreetId;

    //Create new Detail object
    public Details(int catgoryId, int streetId) {
        mCategoryId = catgoryId;
        mStreetId = streetId;
    }
    public int getCategoryId() {return mCategoryId;}
    public int getStreetId() {return mStreetId;}

}
