package com.example.fleissig.restaurantapp.ui.fragments;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.activities.CarrinhoActivity;
import com.example.fleissig.restaurantapp.ui.adapters.CarrinhoPedidoAdapter;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseCarrinhoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by phuctran on 5/26/17.
 */

public class CarrinhoPedidoFragment extends BaseCarrinhoFragment {

    private static final String TAG = CarrinhoPedidoFragment.class.getSimpleName();

    @BindView(R.id.rvPedido)
    RecyclerView mRecyclerView;
    @BindView(R.id.tvTotalPedido)
    TextView tvTotalPedido;
    @BindView(R.id.btnFazerPedido)
    Button btnFazerPedido;


    private CarrinhoPedidoAdapter mDishesAdapter;
    private double totalPrice;

    public static CarrinhoPedidoFragment newInstance() {
        CarrinhoPedidoFragment fragment = new CarrinhoPedidoFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_carrinho_pedido;
    }

    @Override
    protected void updateFollowingViewBinding() {
        setupRecycleView();
        renderTotalPedido();
    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }

    @OnClick(R.id.btnAdicionarItem)
    void onClickBtnAdicionarItem() {
        ((CarrinhoActivity) getActivity()).goToAdicionarItemScreen();
    }

    @OnClick(R.id.btnFazerPedido)
    void onClickBtnFazerPedido() {
        ((CarrinhoActivity) getActivity()).goToNotaFragment(totalPrice, new Runnable() {
            @Override
            public void run() {
                for (CartItem item : MyApplication.getInstance().CART.getItems()) {
                    if (item.getDishOrder().getQuantity() > 0) {
                        item.setOrderedQuantity(item.getOrderedQuantity() + item.getDishOrder().getQuantity());
                        item.getDishOrder().setQuantity(0);
                    }

                }
                setupRecycleView();
            }
        });
    }

    public void renderTotalPedido() {
        totalPrice = 0.0;

        for (CartItem item : MyApplication.getInstance().CART.getItems()) {

            int iQuantity = item.getDishOrder().getQuantity();
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
        tvTotalPedido.setText("Total Pedido: R$ " + totalPrice);
    }

    public void setupRecycleView() {
        List<CartItem> originalItems = MyApplication.getInstance().CART.getItems();
        List<CartItem> itemToShow = new ArrayList<>();
        for (CartItem ci : originalItems) {
            if (ci.getDishOrder().getQuantity() > 0) {
                itemToShow.add(ci);
            }
        }

        mDishesAdapter = new CarrinhoPedidoAdapter(getContext(), this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.mRecyclerView.setLayoutManager(mLayoutManager);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        this.mRecyclerView.setAdapter(mDishesAdapter);
        this.mDishesAdapter.setRecycleViewCollection(itemToShow);

        if (itemToShow.size() == 0) {
            btnFazerPedido.setEnabled(false);
        } else {
            btnFazerPedido.setEnabled(true);
        }

    }
}
