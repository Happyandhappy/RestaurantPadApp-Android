package com.example.fleissig.restaurantapp.ui.fragments.bases;

import com.example.fleissig.restaurantapp.ui.models.RvCategory;
import com.example.fleissig.restaurantapp.ui.models.RvDish;
import com.example.fleissig.restaurantapp.ui.models.RvSubCategory;

/**
 * Created by phuctran on 5/18/17.
 */

public abstract class BaseMenuListFragment extends BaseFirebaseFragment {
    protected void goToDishesFragment(RvCategory category) {
        ((BaseFragmentResponder) getActivity()).goToDishesFragment(category);
    }
    protected void goToDishesFragment(RvSubCategory category) {
        ((BaseFragmentResponder) getActivity()).goToDishesFragment(category);
    }
    protected void goToDishDetailFragment(RvDish dishKey) {
        ((BaseFragmentResponder) getActivity()).goToDishDetailFragment(dishKey);
    }
    protected void showDishDetailDialog(RvDish dishModel) {
        ((BaseFragmentResponder) getActivity()).showDishDetailDialog(dishModel);
    }

    protected void showPaymentGuide() {
        ((BaseFragmentResponder) getActivity()).showPaymentGuide();
    }

    public void showConfirmPagamento(String card, String name, String exp) {
        ((BaseFragmentResponder) getActivity()).showConfirmPagamento(card, name, exp);
    }

    protected void goToPedido() {
        ((BaseFragmentResponder) getActivity()).goToCarrinho();
    }

    public interface BaseFragmentResponder extends BaseFragment.BaseFragmentResponder {

        void goToDishesFragment(RvCategory category);

        void goToDishesFragment(RvSubCategory category);

        void goToDishDetailFragment(RvDish dishKey);

        void goToCarrinho();

        void showDishDetailDialog(RvDish dishModel);

        void showPaymentGuide();

        void showConfirmPagamento(String card, String name, String exp);
    }
}
