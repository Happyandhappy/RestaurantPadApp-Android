package com.example.fleissig.mylibrary;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.net.URI;
import java.net.URISyntaxException;

public class Dish implements Parcelable {
    public String text;
    @Nullable
    public Double price = null;
    @Nullable
    public Double price_bottle = null;
    @Nullable
    public String image_url = null;
    @Nullable
    public String desc;

    public Dish(String text, Double price, Double price_bottle, String image_url, String desc) {
        this.text = text;
        this.price = price;
        this.price_bottle = price_bottle;
        this.image_url = image_url;
        this.desc = desc;
    }

    public Dish() {
        price = null;
        price_bottle = null;
        image_url = null;
        desc = null;
    }

    public Dish(String text) {
        this.text = text;
    }

    public Dish(String text, String price, String price_bottle, String image_url) {
        this.text = text;
        try {
            this.price = Double.parseDouble(price);
        }
        catch (NumberFormatException e){}
        try {
            this.price_bottle = Double.parseDouble(price_bottle);
        }
        catch (NumberFormatException e){}
        try {
            this.image_url = new URI(image_url).toString();
        }
        catch (URISyntaxException e) {}
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeValue(this.price);
        dest.writeValue(this.price_bottle);
        dest.writeString(this.image_url);
        dest.writeString(this.desc);
    }

    protected Dish(Parcel in) {
        this.text = in.readString();
        this.price = (Double) in.readValue(Double.class.getClassLoader());
        this.price_bottle = (Double) in.readValue(Double.class.getClassLoader());
        this.image_url = in.readString();
        this.desc = in.readString();
    }

    public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {
        @Override
        public Dish createFromParcel(Parcel source) {
            return new Dish(source);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };
}
