package com.example.fleissig.restaurantapp.utils;

import com.example.fleissig.mylibrary.PayingOrder;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.Restaurant;
import com.example.fleissig.restaurantapp.order.CartItem;

/**
 * Created by phuctran on 5/22/17.
 */

public class DataUtils {
    public static Restaurant currentRestaurant;

    public static double countMinhaConta() {
        double totalPrice = 0.00;

        for (CartItem item : MyApplication.getInstance().CART.getOrderedItems()) {

            int iQuantity = item.getOrderedQuantity();
            if (iQuantity < 0) iQuantity = 0;
            final double dPrice;
            if (item.getDish().price == null || item.getDish().price == 0) {
                if (item.getDish().price_bottle != null) {
                    dPrice = item.getDish().price_bottle;
                } else {
                    dPrice = 0.00;
                }
            } else {
                dPrice = item.getDish().price;
            }
            totalPrice += dPrice * iQuantity;
        }
        return totalPrice;
    }

    public static double countLeft(){
        double subTotal = countMinhaConta();
        subTotal = subTotal * 1.1;
        double payed = countPaids();
        return subTotal - payed;
    }

    public static double countPaids() {
        double totalPrice = 0.00;

        for (PayingOrder item : MyApplication.getInstance().CART.getPays()) {


            totalPrice += item.getMoney();
        }
        return totalPrice;
    }
}
