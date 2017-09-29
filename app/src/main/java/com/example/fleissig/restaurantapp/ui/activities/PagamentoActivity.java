package com.example.fleissig.restaurantapp.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.fragments.PagamentoDetailFragment;
import com.example.fleissig.restaurantapp.ui.fragments.PagamentoNotaFragment;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BasePagamentoFragment;
import com.example.fleissig.restaurantapp.utils.DataUtils;

import butterknife.BindView;

/**
 * Created by phuctran on 6/1/17.
 */

public class PagamentoActivity extends BaseActivity implements BasePagamentoFragment.BaseFragmentResponder {
    private static final String TAG = PagamentoActivity.class.getSimpleName();

    private static final String PAGAMENTO_NOTA_FRAGMENT = "PAGAMENTO_NOTA_FRAGMENT";
    private static final String PAGAMENTO_DETAIL_FRAGMENT = "PAGAMENTO_DETAIL_FRAGMENT";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_pagamento;
    }

    @Override
    protected void updateFollowingViewBinding(Bundle savedInstanceState) {
        setupToolbar();
        goToNotaFragment();
    }

    @Override
    public void goToNotaFragment() {
        showFragment(PagamentoNotaFragment.newInstance(), PAGAMENTO_NOTA_FRAGMENT, R.id.leftFragmentContainer);
    }

    @Override
    public void goToPagamentoDetailFragment(CartItem cartModel) {
        showFragment(PagamentoDetailFragment.newInstance(cartModel), PAGAMENTO_DETAIL_FRAGMENT, R.id.rightFragmentContainer);
    }
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle(DataUtils.currentRestaurant.name);
    }

}
