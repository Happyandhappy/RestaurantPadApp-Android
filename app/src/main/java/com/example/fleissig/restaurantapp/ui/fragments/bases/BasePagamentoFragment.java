package com.example.fleissig.restaurantapp.ui.fragments.bases;

import com.example.fleissig.restaurantapp.order.CartItem;

/**
 * Created by phuctran on 6/1/17.
 */

public abstract class BasePagamentoFragment extends BaseFirebaseFragment {
    protected void goToNotaFragment() {
        ((BasePagamentoFragment.BaseFragmentResponder) getActivity()).goToNotaFragment();
    }

    protected void goToPagamentoDetailFragment(CartItem cartModel) {
        ((BasePagamentoFragment.BaseFragmentResponder) getActivity()).goToPagamentoDetailFragment(cartModel);
    }

    public interface BaseFragmentResponder extends BaseFragment.BaseFragmentResponder {

        void goToNotaFragment();

        void goToPagamentoDetailFragment(CartItem cartModel);
    }
}
