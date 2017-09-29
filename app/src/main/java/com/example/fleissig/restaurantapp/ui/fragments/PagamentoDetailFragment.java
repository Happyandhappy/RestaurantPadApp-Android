package com.example.fleissig.restaurantapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.adapters.PagamentoPagerAdapter;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BasePagamentoFragment;

import butterknife.BindView;

/**
 * Created by phuctran on 6/1/17.
 */

public class PagamentoDetailFragment extends BasePagamentoFragment {
    private static final String TAG = PagamentoDetailFragment.class.getSimpleName();

    private static final String ARGS_CART_ITEM = "ARGS_CART_ITEM";

    @BindView(R.id.etRealizados)
    EditText etRealizados;
    @BindView(R.id.tvTotalOfPagar)
    TextView tvTotalPagar;
    @BindView(R.id.tvTotalPago)
    TextView tvTotalPago;
    @BindView(R.id.tvFalta)
    TextView tvFalta;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;

    private CartItem mCartItem;
    private PagamentoPagerAdapter pagerAdapter;

    public static PagamentoDetailFragment newInstance(CartItem cartItem) {
        PagamentoDetailFragment fragment = new PagamentoDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_CART_ITEM, cartItem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_pagamento_right_panel;
    }

    @Override
    protected void updateFollowingViewBinding() {
        setupView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getArguments() != null) {
            mCartItem = getArguments().getParcelable(ARGS_CART_ITEM);
        }
    }

    private void setupView() {
        tabLayout.addTab(tabLayout.newTab().setText("Cartão"));
        tabLayout.addTab(tabLayout.newTab().setText("Garcom"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new PagamentoPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
