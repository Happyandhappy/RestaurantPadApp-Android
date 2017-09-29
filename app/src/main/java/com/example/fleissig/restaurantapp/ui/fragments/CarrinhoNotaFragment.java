package com.example.fleissig.restaurantapp.ui.fragments;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.adapters.CarrinhoNotaAdapter;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseCarrinhoFragment;

import butterknife.BindView;
import butterknife.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phuctran on 5/26/17.
 */

public class CarrinhoNotaFragment extends BaseCarrinhoFragment {

    private static final String TAG = CarrinhoNotaFragment.class.getSimpleName();

    @BindView(R.id.rvNota)
    RecyclerView mRecyclerView;
    @BindView(R.id.tvTotalPagar)
    TextView tvTotalPagar;
    @BindView(R.id.btnPagar)
    Button btnPagar;
    private CarrinhoNotaAdapter mAdapter;
    private double totalPrice;

    public static CarrinhoNotaFragment newInstance() {
        CarrinhoNotaFragment fragment = new CarrinhoNotaFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_carrinho_nota;
    }

    @Override
    protected void updateFollowingViewBinding() {
        setupRecycleView();
        renderTotalPagar();
    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }

    @OnClick(R.id.btnPagar)
    void btnPagar() {
        goToPagamentoScreen();
    }

    void setupRecycleView() {
        List<CartItem> originalItems = MyApplication.getInstance().CART.getItems();
        List<CartItem> itemToShow = new ArrayList<>();
        for (CartItem ci : originalItems) {
            if (ci.getOrderedQuantity() > 0) {
                itemToShow.add(ci);
            }
        }

        mAdapter = new CarrinhoNotaAdapter(getContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.mRecyclerView.setLayoutManager(mLayoutManager);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        this.mRecyclerView.setAdapter(mAdapter);
        this.mAdapter.setRecycleViewCollection(itemToShow);
        if (itemToShow.size() == 0) {
            btnPagar.setEnabled(false);
        } else {
            btnPagar.setEnabled(true);
        }
    }

    public void renderTotalPagar() {
        totalPrice = 0.0;

        for (CartItem item : MyApplication.getInstance().CART.getItems()) {

            int iQuantity = item.getOrderedQuantity();
            if (iQuantity < 0) iQuantity = 0;
            final double dPrice;
            if (item.getDish().price == null || item.getDish().price == 0) {
                if (item.getDish().price_bottle != null) {
                    dPrice = item.getDish().price_bottle;
                } else {
                    dPrice = 0.0;
                }
            } else {
                dPrice = item.getDish().price;
            }
            totalPrice += dPrice * iQuantity;
        }
        tvTotalPagar.setText("Total a pagar: R$ " + totalPrice);
    }
}
