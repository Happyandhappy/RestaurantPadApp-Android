package com.example.fleissig.mylibrary;

/**
 * Created by phuctran on 7/19/17.
 */

public class PayingOrder {
    private double money;
    private long time;

    public PayingOrder() {
    }

    public PayingOrder(double money, long time) {
        this.money = money;
        this.time = time;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
