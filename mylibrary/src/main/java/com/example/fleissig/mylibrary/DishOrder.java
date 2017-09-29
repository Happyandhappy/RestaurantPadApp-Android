package com.example.fleissig.mylibrary;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by carlosnetto on 18/04/17.
 */

public class DishOrder implements Parcelable {
    public Double singlePrice;
    public int quantity;

    public DishOrder(){
    }


    public Double getSinglePrice() {
        return singlePrice;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSinglePrice(Double singlePrice) {
        this.singlePrice = singlePrice;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.singlePrice);
        dest.writeInt(this.quantity);
    }

    protected DishOrder(Parcel in) {
        this.singlePrice = (Double) in.readValue(Double.class.getClassLoader());
        this.quantity = in.readInt();
    }

    public static final Parcelable.Creator<DishOrder> CREATOR = new Parcelable.Creator<DishOrder>() {
        @Override
        public DishOrder createFromParcel(Parcel source) {
            return new DishOrder(source);
        }

        @Override
        public DishOrder[] newArray(int size) {
            return new DishOrder[size];
        }
    };
}
