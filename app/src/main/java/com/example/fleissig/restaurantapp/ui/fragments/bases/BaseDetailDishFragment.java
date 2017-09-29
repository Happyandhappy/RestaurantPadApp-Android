package com.example.fleissig.restaurantapp.ui.fragments.bases;

import com.example.fleissig.restaurantapp.category.Category;
import com.example.fleissig.restaurantapp.ui.models.RvDish;

/**
 * Created by phuctran on 5/18/17.
 */

public abstract class BaseDetailDishFragment extends BaseFirebaseFragment  {
    protected void goToDishDetailFragment(RvDish dishKey) {
        ((BaseFragmentResponder) getActivity()).goToDishDetailFragment(dishKey);
    }

    protected void goToPreviousScreen() {
        ((BaseFragmentResponder) getActivity()).goToPreviousScreen();
    }

    public interface BaseFragmentResponder extends BaseFragment.BaseFragmentResponder {
        void goToDishDetailFragment(RvDish dishKey);

        void goToPreviousScreen();
    }
}
