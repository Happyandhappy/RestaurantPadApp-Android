package com.example.fleissig.restaurantapp.ui.models;

import com.example.fleissig.restaurantapp.order.CartItem;

/**
 * Created by phuctran on 5/30/17.
 */

public class RvNota {
    private int rate;
    private CartItem cartItem;

    public RvNota(int rate, CartItem cartItem) {
        this.rate = rate;
        this.cartItem = cartItem;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
}
