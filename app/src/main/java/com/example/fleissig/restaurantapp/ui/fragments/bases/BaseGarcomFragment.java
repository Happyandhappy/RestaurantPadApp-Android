package com.example.fleissig.restaurantapp.ui.fragments.bases;

import com.example.fleissig.restaurantapp.category.Category;
import com.example.fleissig.restaurantapp.ui.models.RvDish;

/**
 * Created by phuctran on 5/18/17.
 */

public abstract class BaseGarcomFragment extends BaseFirebaseFragment {
    protected void goToVoltarPagamento() {
        ((BaseFragmentResponder) getActivity()).goToVoltarPagamento();
    }

    protected void goToPagamentoGarcon() {
        ((BaseFragmentResponder) getActivity()).goToPagamentoGarcon();
    }

    public interface BaseFragmentResponder extends BaseFragment.BaseFragmentResponder {
        void goToVoltarPagamento();

        void goToPagamentoGarcon();
    }
}
