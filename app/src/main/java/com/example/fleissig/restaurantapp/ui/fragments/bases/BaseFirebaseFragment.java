package com.example.fleissig.restaurantapp.ui.fragments.bases;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by phuctran on 5/17/17.
 */

public abstract class BaseFirebaseFragment extends BaseFragment {
    protected DatabaseReference mRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRef = FirebaseDatabase.getInstance().getReference();
        View v =  super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    protected DatabaseReference getDatabaseReference(String mRestaurantId, String mOriginKey) {
        return  FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.CATEGORIES_KEY)
                .child(mRestaurantId)
                .child(mOriginKey);
    }

}
