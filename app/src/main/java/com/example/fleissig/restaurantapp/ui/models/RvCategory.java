package com.example.fleissig.restaurantapp.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.example.fleissig.restaurantapp.subcategory.SubCategory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RvCategory implements Parent<RvSubCategory>,Parcelable {
    public String text;
    public List<RvSubCategory> subcategories;
    public List<RvDish> dishes;

    public RvCategory() {
    }

    @Override
    public List<RvSubCategory> getChildList() {
        return subcategories;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeList(this.subcategories);
        dest.writeTypedList(this.dishes);
    }

    protected RvCategory(Parcel in) {
        this.text = in.readString();
        this.subcategories = new ArrayList<RvSubCategory>();
        in.readList(this.subcategories, RvSubCategory.class.getClassLoader());
        this.dishes = in.createTypedArrayList(RvDish.CREATOR);
    }

    public static final Parcelable.Creator<RvCategory> CREATOR = new Parcelable.Creator<RvCategory>() {
        @Override
        public RvCategory createFromParcel(Parcel source) {
            return new RvCategory(source);
        }

        @Override
        public RvCategory[] newArray(int size) {
            return new RvCategory[size];
        }
    };
}