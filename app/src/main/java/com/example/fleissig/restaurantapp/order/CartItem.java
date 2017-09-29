package com.example.fleissig.restaurantapp.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.DishOrder;

/**
 * Created by carlosnetto on 22/04/17.
 */

public class CartItem implements Parcelable {

    String dishId;

    Dish dish;

    DishOrder dishOrder;

    int orderedQuantity = -1;
    private String infomationExtra;
    private int ratingStar;

    CartItem(String dishId, Dish dish, DishOrder dishOrder, int orderedQuantity) {
        this.dishId = dishId;
        this.dish = dish;
        this.dishOrder = dishOrder;
        this.orderedQuantity = orderedQuantity;
    }

    CartItem(String dishId, Dish dish, DishOrder dishOrder) {
        this(dishId, dish, dishOrder, 0);
    }

    public String getDishId() {
        return dishId;
    }

    public Dish getDish() {
        return dish;
    }

    public DishOrder getDishOrder() {
        return dishOrder;
    }

    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public int getOrderedQuantity() {
        return orderedQuantity;
    }

    public void incrementQuantity() {
        int quantity = dishOrder.getQuantity();
        quantity = quantity + 1;
        dishOrder.setQuantity(quantity);
    }

    public void decrementQuantity() {
        int quantity = dishOrder.getQuantity();
        int newQuantity = quantity - 1;
        if (newQuantity <= orderedQuantity) {
            return;
        }
        dishOrder.setQuantity(newQuantity);
    }

    public boolean isZero() {
        return dishOrder.getQuantity() == 0;
    }

    public String getInfomationExtra() {
        return infomationExtra;
    }

    public void setInfomationExtra(String infomationExtra) {
        this.infomationExtra = infomationExtra;
    }

    public int getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(int ratingStar) {
        this.ratingStar = ratingStar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dishId);
        dest.writeParcelable(this.dish, flags);
        dest.writeParcelable(this.dishOrder, flags);
        dest.writeInt(this.orderedQuantity);
        dest.writeString(this.infomationExtra);
        dest.writeInt(this.ratingStar);
    }

    protected CartItem(Parcel in) {
        this.dishId = in.readString();
        this.dish = in.readParcelable(Dish.class.getClassLoader());
        this.dishOrder = in.readParcelable(DishOrder.class.getClassLoader());
        this.orderedQuantity = in.readInt();
        this.infomationExtra = in.readString();
        this.ratingStar = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel source) {
            return new CartItem(source);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
}
