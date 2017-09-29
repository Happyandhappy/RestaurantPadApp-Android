package com.example.fleissig.restaurantapp.ui.fragments;

import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseFirebaseFragment;

/**
 * Created by phuctran on 6/7/17.
 */

public class GarcomFragment extends BaseFirebaseFragment {
    private static final String TAG = GarcomFragment.class.getSimpleName();

    public static GarcomFragment newInstance() {
        return new GarcomFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_garcom;
    }

    @Override
    protected void updateFollowingViewBinding() {

    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }
}
