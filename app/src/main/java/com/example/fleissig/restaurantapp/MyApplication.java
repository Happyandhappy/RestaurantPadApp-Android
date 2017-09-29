package com.example.fleissig.restaurantapp;

import android.app.Application;
import android.content.Context;

import com.example.fleissig.restaurantapp.order.Cart;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.HashMap;

public class MyApplication extends Application {
    private static MyApplication singleton;

    public static boolean hasVisitedSplash;
    private static Context mContext;
    public Cart CART = new Cart();
    public HashMap<DatabaseReference, String> dishCommentMap = new HashMap<>();
    public HashMap<DatabaseReference, Float> dishRatingMap = new HashMap<>();

    public static MyApplication getInstance() {

        return singleton;
    }

    public void clear() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        CART = new Cart();
        dishCommentMap.clear();
        dishRatingMap.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        singleton = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
    }

    public static Context getContext() {
        return mContext;
    }
}
