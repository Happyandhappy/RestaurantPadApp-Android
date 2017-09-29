package com.example.fleissig.mylibrary;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Map;
import java.util.List;

@IgnoreExtraProperties
public class Order {
    public Map<String, DishOrder> preorderDishes;
    public Map<String, DishOrder> orderedDishes;
    public List<PayingOrder> pays;
    public int table_number;
    public Object timestamp;
    public boolean closed;

    public Order(Map<String, DishOrder> preorderDishes, int table_number) {
        this.preorderDishes = preorderDishes;
        this.table_number = table_number;
        this.timestamp = ServerValue.TIMESTAMP;
        this.closed = false;
    }

    public Order(int table_number, Object timestamp, boolean closed) {
        this.table_number = table_number;
        this.timestamp = timestamp;
        this.closed = closed;
    }

    public Order() {
    }

    @Exclude
    public long getTimestamp() {
        return (long) timestamp;
    }

    public int getTable_number() {
        return table_number;
    }

    public boolean isClosed() {
        return closed;
    }

    public List<PayingOrder> getPays() {
        return pays;
    }

    public Map<String, DishOrder> getPreorderDishes() {
        return preorderDishes;
    }

    public Map<String, DishOrder> getOrderedDishes() {
        return orderedDishes;
    }
}
