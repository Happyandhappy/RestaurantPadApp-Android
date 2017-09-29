package com.example.fleissig.restaurantapp.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.fleissig.restaurantapp.subcategory.SubCategory;

import java.util.Date;
import java.util.HashMap;

public class Category implements Parcelable {
    private HashMap<String, Boolean> dishes;
    private String text;
    private HashMap<String, SubCategory> subcategories;
    private Date datahora;

    public Category() {
    }

    public HashMap<String, Boolean> getDishes() {
        return dishes;
    }

    public HashMap<String, SubCategory> getSubcategories() {
        return subcategories;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.dishes);
        dest.writeString(this.text);
        dest.writeSerializable(this.subcategories);
        dest.writeLong(this.datahora != null ? this.datahora.getTime() : -1);
    }

    protected Category(Parcel in) {
        this.dishes = (HashMap<String, Boolean>) in.readSerializable();
        this.text = in.readString();
        this.subcategories = (HashMap<String, SubCategory>) in.readSerializable();
        long tmpDatahora = in.readLong();
        this.datahora = tmpDatahora == -1 ? null : new Date(tmpDatahora);
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
