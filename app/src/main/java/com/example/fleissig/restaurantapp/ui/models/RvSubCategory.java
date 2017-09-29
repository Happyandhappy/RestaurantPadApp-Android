package com.example.fleissig.restaurantapp.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by phuctran on 5/19/17.
 */

public class RvSubCategory implements Parcelable {
    public String text;
    public List<RvDish> dishes;

    public RvSubCategory(String text) {
        this.text = text;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeTypedList(this.dishes);
    }

    protected RvSubCategory(Parcel in) {
        this.text = in.readString();
        this.dishes = in.createTypedArrayList(RvDish.CREATOR);
    }

    public static final Parcelable.Creator<RvSubCategory> CREATOR = new Parcelable.Creator<RvSubCategory>() {
        @Override
        public RvSubCategory createFromParcel(Parcel source) {
            return new RvSubCategory(source);
        }

        @Override
        public RvSubCategory[] newArray(int size) {
            return new RvSubCategory[size];
        }
    };
}
