package com.example.fleissig.restaurantapp.ui.fragments.bases;

/**
 * Created by phuctran on 5/26/17.
 */

public abstract class BaseCarrinhoFragment extends BaseFirebaseFragment {
    protected void goToNotaFragment(double totalPrice, Runnable runnable) {
        ((BaseCarrinhoFragment.BaseFragmentResponder) getActivity()).goToNotaFragment(totalPrice, runnable);

    }

    protected void goToAdicionarItemScreen() {
        ((BaseCarrinhoFragment.BaseFragmentResponder) getActivity()).goToAdicionarItemScreen();
    }

    protected void goToPagamentoScreen() {
        ((BaseCarrinhoFragment.BaseFragmentResponder) getActivity()).goToPagamentoScreen();
    }

    public interface BaseFragmentResponder extends BaseFragment.BaseFragmentResponder {

        void goToNotaFragment(double totalPrice, Runnable runnable);

        void goToAdicionarItemScreen();

        void goToPagamentoScreen();
    }
}
