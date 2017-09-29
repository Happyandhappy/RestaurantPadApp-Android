package com.example.fleissig.restaurantapp.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ui.fragments.GarcomFragment;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseGarcomFragment;
import com.example.fleissig.restaurantapp.utils.DataUtils;

import butterknife.BindView;

/**
 * Created by phuctran on 6/7/17.
 */

public class GarcomActivity extends BaseActivity implements BaseGarcomFragment.BaseFragmentResponder {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragmentContainer)
    View fragmentContainer;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_garcom;
    }

    @Override
    protected void updateFollowingViewBinding(Bundle savedInstanceState) {
        setupToolbar();
        setupView();
    }

    private void setupView() {
        showFragment(GarcomFragment.newInstance(), "FRAGMENT_GARCOM", R.id.fragmentContainer);
    }

    private void setupToolbar() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle(DataUtils.currentRestaurant.name);
    }

    @Override
    public void goToVoltarPagamento() {

    }

    @Override
    public void goToPagamentoGarcon() {

    }
}
