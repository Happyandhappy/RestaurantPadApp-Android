package com.example.fleissig.restaurantapp.ui.fragments;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.activities.PagamentoActivity;
import com.example.fleissig.restaurantapp.ui.adapters.CarrinhoNotaAdapter;
import com.example.fleissig.restaurantapp.ui.adapters.PagamentoNotaAdapter;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BasePagamentoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by phuctran on 6/1/17.
 */

public class PagamentoNotaFragment extends BasePagamentoFragment {

    private static final String TAG = PagamentoNotaFragment.class.getSimpleName();

    @BindView(R.id.rvNota)
    RecyclerView mRecyclerView;

    private PagamentoNotaAdapter mAdapter;

    public static PagamentoNotaFragment newInstance() {
        PagamentoNotaFragment fragment = new PagamentoNotaFragment();
        return fragment;
    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_pagamento_nota;
    }

    @Override
    protected void updateFollowingViewBinding() {
        setupRecycleView();
    }

    void setupRecycleView() {
        List<CartItem> originalItems = MyApplication.getInstance().CART.getItems();
        List<CartItem> itemToShow = new ArrayList<>();
        for (CartItem ci : originalItems) {
            if (ci.getOrderedQuantity() > 0) {
                itemToShow.add(ci);
            }
        }

        mAdapter = new PagamentoNotaAdapter(getContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.mRecyclerView.setLayoutManager(mLayoutManager);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        this.mRecyclerView.setAdapter(mAdapter);
        this.mAdapter.setRecycleViewCollection(itemToShow);
        mAdapter.setOnItemClickListener(new PagamentoNotaAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(CartItem cartModel) {
                ((PagamentoActivity)getActivity()).goToPagamentoDetailFragment(cartModel);
            }
        });

    }
}
