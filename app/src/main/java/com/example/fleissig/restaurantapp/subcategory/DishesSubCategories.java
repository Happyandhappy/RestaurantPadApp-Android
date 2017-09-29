package com.example.fleissig.restaurantapp.subcategory;

import android.content.Intent;
import android.os.Bundle;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.restaurantapp.AbstractDishesActivity;
import com.example.fleissig.restaurantapp.category.CategoriesActivity;
import com.example.fleissig.restaurantapp.category.DishActivity;
import com.example.fleissig.restaurantapp.category.DishesActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by carlosnetto on 15/04/17.
 */

public class DishesSubCategories extends AbstractDishesActivity{

    @Override
    protected String getOriginKeySavedInstance(Bundle savedInstanceState) {
        return savedInstanceState.getString(SubCategoriesActivity.SUBCATEGORY_KEY_EXTRA);
    }

    @Override
    protected String getOriginKeyIntent(Intent intent) {
        return intent.getStringExtra(SubCategoriesActivity.SUBCATEGORY_KEY_EXTRA);
    }


    @Override
    protected DatabaseReference getDatabaseReference(String mRestaurantId, String categoryKey, String mOriginKey) {
        return  FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.CATEGORIES_KEY)
                .child(mRestaurantId)
                .child(categoryKey)
                .child(FirebaseConstants.SUBCATEGORIES_KEY)
                .child(mOriginKey);
    }

    @Override
    protected Intent generateIntent() {
        return  new Intent(DishesSubCategories.this, DishActivity.class);
    }

    @Override
    protected void putOutState(Bundle outState, String mOriginKey) {
        outState.putString(SubCategoriesActivity.SUBCATEGORY_KEY_EXTRA, mOriginKey);
    }
}
