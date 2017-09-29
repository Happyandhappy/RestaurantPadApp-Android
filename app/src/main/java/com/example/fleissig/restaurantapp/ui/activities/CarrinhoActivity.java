package com.example.fleissig.restaurantapp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ui.fragments.CarrinhoNotaFragment;
import com.example.fleissig.restaurantapp.ui.fragments.CarrinhoPedidoFragment;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseCarrinhoFragment;
import com.example.fleissig.restaurantapp.utils.DataUtils;

import butterknife.BindView;

/**
 * Created by phuctran on 5/26/17.
 */

public class CarrinhoActivity extends BaseActivity implements BaseCarrinhoFragment.BaseFragmentResponder {
    private static final String TAG = CarrinhoActivity.class.getSimpleName();

    private static final String PEDIDO_FRAGMENT = "PEDIDO_FRAGMENT";
    private static final String NOTA_FRAGMENT = "NOTA_FRAGMENT";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.leftFragmentContainer)
    View leftFragmentContainer;
    @BindView(R.id.rightFragmentContainer)
    View rightFragmentContainer;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_carrinho;
    }

    @Override
    protected void updateFollowingViewBinding(Bundle savedInstanceState) {
        setupToolbar();
        setupView();
    }

    private void setupView() {
        showFragment(CarrinhoPedidoFragment.newInstance(), PEDIDO_FRAGMENT, R.id.leftFragmentContainer);
        showFragment(CarrinhoNotaFragment.newInstance(), NOTA_FRAGMENT, R.id.rightFragmentContainer);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        setActionBarTitle(DataUtils.currentRestaurant.name);
    }


    @Override
    public void goToNotaFragment(double totalPrice, final Runnable runnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirma?");
        builder.setMessage("Confirma o Pedido no valor de R$" + totalPrice);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (runnable != null) {
                    runnable.run();
                }
                showFragment(CarrinhoNotaFragment.newInstance(), NOTA_FRAGMENT, R.id.rightFragmentContainer);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();


    }

    @Override
    public void goToAdicionarItemScreen() {
        finish();
    }

    @Override
    public void goToPagamentoScreen() {
        Intent intent = new Intent(CarrinhoActivity.this, PagamentoActivity.class);
        startActivity(intent);
    }
}
