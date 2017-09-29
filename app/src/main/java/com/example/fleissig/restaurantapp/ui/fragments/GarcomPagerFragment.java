package com.example.fleissig.restaurantapp.ui.fragments;

import android.os.Bundle;

import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ui.activities.BaseActivity;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseFragment;

/**
 * Created by phuctran on 6/7/17.
 */

public class GarcomPagerFragment extends BaseFragment {

    private static final String TAG = GarcomPagerFragment.class.getSimpleName();

    public static GarcomPagerFragment newInstance() {
        return new GarcomPagerFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_pagamento_garcom;
    }

    @Override
    protected void updateFollowingViewBinding() {

    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }
}
