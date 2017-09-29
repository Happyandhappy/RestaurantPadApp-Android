package com.example.fleissig.restaurantapp.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by phuctran on 5/19/17.
 */

public class RvDish implements Parcelable {
    public String id;
    public String text;
    public String desc;
    public String image_url;
    public double price;
    public double price_bottle;
    public String datahora;
    public RvDish() {
    }

    public RvDish(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeString(this.desc);
        dest.writeString(this.image_url);
        dest.writeDouble(this.price);
        dest.writeDouble(this.price_bottle);
        dest.writeString(this.datahora);
    }

    protected RvDish(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.desc = in.readString();
        this.image_url = in.readString();
        this.price = in.readDouble();
        this.price_bottle = in.readDouble();
        this.datahora = in.readString();
    }

    public static final Parcelable.Creator<RvDish> CREATOR = new Parcelable.Creator<RvDish>() {
        @Override
        public RvDish createFromParcel(Parcel source) {
            return new RvDish(source);
        }

        @Override
        public RvDish[] newArray(int size) {
            return new RvDish[size];
        }
    };
}
