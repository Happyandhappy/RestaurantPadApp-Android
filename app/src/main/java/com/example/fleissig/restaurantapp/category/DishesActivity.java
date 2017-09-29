package com.example.fleissig.restaurantapp.category;

import android.content.Intent;
import android.os.Bundle;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.restaurantapp.AbstractDishesActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DishesActivity extends AbstractDishesActivity {


    @Override
    protected String getOriginKeySavedInstance(Bundle savedInstanceState) {
        return savedInstanceState.getString(CategoriesActivity.CATEGORY_KEY_EXTRA);
    }

    @Override
    protected String getOriginKeyIntent(Intent intent) {
        return intent.getStringExtra(CategoriesActivity.CATEGORY_KEY_EXTRA);
    }

    @Override
    protected void putOutState(Bundle outState, String mOriginKey) {
        outState.putString(CategoriesActivity.CATEGORY_KEY_EXTRA, mOriginKey);
    }


    @Override
    protected DatabaseReference getDatabaseReference(String mRestaurantId, String mCategory, String mOriginKey) {
        return  FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.CATEGORIES_KEY)
                .child(mRestaurantId)
                .child(mOriginKey);
    }

    @Override
    protected Intent generateIntent() {
        return  new Intent(DishesActivity.this, DishActivity.class);
    }
}
