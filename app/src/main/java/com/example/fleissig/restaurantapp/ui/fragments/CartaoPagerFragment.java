package com.example.fleissig.restaurantapp.ui.fragments;

import android.os.Bundle;

import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ui.activities.BaseActivity;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseFragment;

/**
 * Created by phuctran on 6/7/17.
 */

public class CartaoPagerFragment extends BaseFragment {

    private static final String TAG = CartaoPagerFragment.class.getSimpleName();

    public static CartaoPagerFragment newInstance() {
        return new CartaoPagerFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_pagamento_cartao;
    }

    @Override
    protected void updateFollowingViewBinding() {

    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }
}
