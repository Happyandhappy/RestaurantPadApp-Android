package com.example.fleissig.restaurantapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.DishOrder;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.Order;
import com.example.fleissig.mylibrary.PayingOrder;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.Functions;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ui.dialogs.ConfirmPagamentoDialog;
import com.example.fleissig.restaurantapp.ui.dialogs.DishDetailDialog;
import com.example.fleissig.restaurantapp.ui.dialogs.PaymentGuideDialog;
import com.example.fleissig.restaurantapp.ui.fragments.CategoryFragment;
import com.example.fleissig.restaurantapp.ui.fragments.DishesFragment;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseMenuListFragment;
import com.example.fleissig.restaurantapp.ui.models.RvCategory;
import com.example.fleissig.restaurantapp.ui.models.RvDish;
import com.example.fleissig.restaurantapp.ui.models.RvSubCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by phuctran on 5/13/17.
 */

public class MenuListActivity extends BaseActivity implements BaseMenuListFragment.BaseFragmentResponder {
    private static final String TAG = MenuListActivity.class.getSimpleName();

    public static final String CATEGORY_FRAGMENT = "CATEGORY_FRAGMENT";
    public static final String DISHES_FRAGMENT = "DISHES_FRAGMENT";

    @BindView(R.id.leftFragmentcontainer)
    View leftFragmentcontainer;
    @BindView(R.id.rightFragmentcontainer)
    View rightFragmentcontainer;

    CategoryFragment categoryFragment;
    DishesFragment dishFragment;
    private String mRestaurantId;
    private Map<String, RvDish> dishMap = new HashMap<>();
    private boolean isInitDishes = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_menulist;
    }

    @Override
    protected void updateFollowingViewBinding(Bundle savedInstanceState) {
        setupToolbar();
        setupFragment();
        setupCategoryView();
        setupData();
    }

    private void setupData() {
        mRestaurantId = ShareFunctions.readRestaurantId(this);
        mRef.child(FirebaseConstants.DISHES_KEY).child(mRestaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    dishMap.put(postSnapshot.getKey(), postSnapshot.getValue(RvDish.class));
                }

                if (!isInitDishes) {
                    isInitDishes = true;
                    mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Order order = snapshot.getValue(Order.class);
                                if (!order.closed && order.table_number == Functions.readTableNumber(MenuListActivity.this)) {
                                    MyApplication.getInstance().CART.orderId = snapshot.getKey();
                                    if (order.getPays() != null) {
                                        MyApplication.getInstance().CART.setPays(order.getPays());
                                    }else{
                                        MyApplication.getInstance().CART.setPays(new ArrayList<PayingOrder>());
                                    }
                                    Map<String, DishOrder> disheOrder = order.getPreorderDishes();
                                    Map<String, DishOrder> orderedDishes = order.getOrderedDishes();
                                    if (disheOrder != null) {
                                        Iterator it = disheOrder.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry<String, DishOrder> pair = (Map.Entry) it.next();
                                            if (dishMap.containsKey(pair.getKey())) {

                                                RvDish mRvDish = dishMap.get(pair.getKey());
                                                if (pair.getValue().getQuantity() > 0) {
                                                    MyApplication.getInstance().CART.addDish(pair.getKey(), new Dish(mRvDish.text, mRvDish.price, mRvDish.price_bottle, mRvDish.image_url, mRvDish.desc), pair.getValue().getQuantity());
                                                }
                                            }
                                        }
                                    }
                                    if (orderedDishes != null) {
                                        Iterator it = orderedDishes.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry<String, DishOrder> pair = (Map.Entry) it.next();
                                            if (dishMap.containsKey(pair.getKey())) {

                                                RvDish mRvDish = dishMap.get(pair.getKey());
                                                if (pair.getValue().getQuantity() > 0) {
                                                    MyApplication.getInstance().CART.addToOrdered(pair.getKey(), new Dish(mRvDish.text, mRvDish.price, mRvDish.price_bottle, mRvDish.image_url, mRvDish.desc), pair.getValue().getQuantity());
                                                }
                                            }
                                        }
                                    }
                                    if (dishFragment != null) {
                                        dishFragment.notifyLoadDataFromFirebaseChanged();
                                    }
                                    return;
                                }
                            }

                            Order order = new Order(Functions.readTableNumber(MenuListActivity.this), System.currentTimeMillis(), false);
                            String orderId = mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).push().getKey();
                            mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).child(orderId).setValue(order);
                            MyApplication.getInstance().CART.createNewOrder(orderId);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupFragment() {
        categoryFragment = CategoryFragment.newInstance();
        showFragment(categoryFragment, CATEGORY_FRAGMENT, R.id.leftFragmentcontainer);
        dishFragment = DishesFragment.newInstance();
        showFragment(dishFragment, DISHES_FRAGMENT, R.id.rightFragmentcontainer);
    }

    private void setupCategoryView() {

    }

    private void setupToolbar() {
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        setActionBarTitle(DataUtils.currentRestaurant.name);
    }

    @Override
    public void goToDishesFragment(RvCategory category) {
        dishFragment.setupAsCategoryData(category);
    }

    @Override
    public void goToDishesFragment(RvSubCategory category) {
        dishFragment.setupAsSubCategoryData(category);
    }

    @Override
    public void goToDishDetailFragment(RvDish dishKey) {
        Intent intent = new Intent(MenuListActivity.this, DishDetailActivity.class);
        intent.putExtra(DishDetailActivity.ARGS_DISH_KEY, dishKey);
        startActivity(intent);
    }

    @Override
    public void goToCarrinho() {
        Intent intent = new Intent(MenuListActivity.this, CarrinhoActivity.class);
        startActivity(intent);
    }

    @Override
    public void showDishDetailDialog(RvDish dishModel) {
        DishDetailDialog.newInstance(dishModel).show(getFragmentManager(), "DISH_DETAIL_DIALOG");

    }

    @Override
    public void showPaymentGuide() {
        PaymentGuideDialog.newInstance().show(getFragmentManager(), "PAYMENT_GUIDE_DIALOG");
    }

    @Override
    public void showConfirmPagamento(String card, String name, String exp) {
        ConfirmPagamentoDialog.newInstance(card, name, exp, this.dishFragment).show(getFragmentManager(), "CONFIRM_PAGAMENTO_DIALOG");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        MyApplication.getInstance().hasVisitedSplash = false;
    }
}
