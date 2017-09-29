package com.example.fleissig.restaurantapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.category.Category;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.fragments.DishDetailFragment;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseDetailDishFragment;
import com.example.fleissig.restaurantapp.ui.models.RvDish;
import com.example.fleissig.restaurantapp.utils.DataUtils;

import butterknife.BindView;

/**
 * Created by phuctran on 5/22/17.
 */

public class DishDetailActivity extends BaseActivity implements BaseDetailDishFragment.BaseFragmentResponder {
    private static final String DISH_DETAIL_FRAGMENT = "DISH_DETAIL_FRAGMENT";

    public static final String ARGS_DISH_KEY = "ARGS_DISH_KEY";
    public static final String ARGS_DISH_TOTAL = "ARGS_DISH_TOTAL";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragmentcontainer)
    View fragmentcontainer;

    private RvDish mDishKey;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_dish_detail;
    }

    @Override
    protected void updateFollowingViewBinding(Bundle savedInstanceState) {
        setupToolbar();
        setupData(savedInstanceState);
        setupDishDetailView();
    }

    private void setupData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            mDishKey = intent.getParcelableExtra(ARGS_DISH_KEY);
        }
    }

    private void setupDishDetailView() {
        goToDishDetailFragment(mDishKey);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle(DataUtils.currentRestaurant.name);
    }


    @Override
    public void goToDishDetailFragment(RvDish diskKey) {
        showFragment(DishDetailFragment.newInstance(diskKey), DISH_DETAIL_FRAGMENT, R.id.fragmentcontainer);
    }

    @Override
    public void goToPreviousScreen() {
        finish();
    }
}
