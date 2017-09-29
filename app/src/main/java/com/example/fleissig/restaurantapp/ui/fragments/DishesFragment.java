package com.example.fleissig.restaurantapp.ui.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.DishOrder;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.Order;
import com.example.fleissig.mylibrary.PayingOrder;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.Functions;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.activities.MenuListActivity;
import com.example.fleissig.restaurantapp.ui.adapters.DishAdapter;
import com.example.fleissig.restaurantapp.ui.adapters.MeusPedidosAdapter;
import com.example.fleissig.restaurantapp.ui.adapters.MinhaContaAdapter;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseMenuListFragment;
import com.example.fleissig.restaurantapp.ui.models.RvCategory;
import com.example.fleissig.restaurantapp.ui.models.RvDish;
import com.example.fleissig.restaurantapp.ui.models.RvSubCategory;
import com.example.fleissig.restaurantapp.utils.DataUtils;
import com.example.fleissig.restaurantapp.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by phuctran on 5/18/17.
 */

public class DishesFragment extends BaseMenuListFragment {

    private static final String TAG = DishesFragment.class.getSimpleName();

    public static final String ARGS_CATEGORY = "ARGS_CATEGORY";
    public static final String ARGS_SUBCATEGORY = "ARGS_SUBCATEGORY";

    @BindView(R.id.rvMeusPedidos)
    RecyclerView mRecycleViewMeusPedidos;
    @BindView(R.id.dishes_listview)
    RecyclerView mRecyclerView;
    @BindView(R.id.rvMinhaConta)
    RecyclerView mRvMinhaConta;
    @BindView(R.id.tvTotalMeusPedidos)
    TextView tvTotalMeusPedidos;
    @BindView(R.id.tvTotalPaid)
    TextView tvTotalPaid;
    @BindView(R.id.tvTotalLeft)
    TextView tvTotalLeft;
    @BindView(R.id.tvTotal)
    TextView tvTotalMinhaConta;
    @BindView(R.id.tvOpcionalValue)
    TextView tvOpcionalValue;
    @BindView(R.id.tvTotalConfirmedOrderWithOpcional)
    TextView tvTotalConfirmedOrderWithOpcional;
    @BindView(R.id.btnConfirmedOrders)
    Button btnConfirmedOrders;
    @BindView(R.id.btnOrderBeforeSendToTheKitchen)
    Button btnOrderBeforeSendToTheKitchen;
    @BindView(R.id.rightLayoutMeusPedidos)
    View rightLayoutMeusPedidos;
    @BindView(R.id.leftLayoutMinhaConta)
    View leftLayoutMinhaConta;
    @BindView(R.id.tvValorAtual)
    TextView tvValorAtual;
    @BindView(R.id.root)
    View view;

    private StaggeredGridLayoutManager layoutManager;
//    @BindView(R.id.tvCartItemDescription)
//    TextView tvCartItemDescription;
//    @BindView(R.id.rlBottomLayout)
//    View rlBottomLayout;

    private int mTotalCartCount;
    private DishAdapter mDishesAdapter;
    private MeusPedidosAdapter mMeusPedidosAdapter;
    private MinhaContaAdapter mMinhaContaAdapter;

    private RvCategory mCategory;
    private RvSubCategory mSubCategory;
    private String mRestaurantId;
    private Map<String, RvDish> dishMap = new HashMap<>();
    private List<RvDish> mDishes = new ArrayList<>();
    private boolean isClosedMinhaConta = true;
    private boolean isClosedMeusPedidos = true;

    public static DishesFragment newInstance() {
        DishesFragment fragment = new DishesFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(ARGS_SUBCATEGORY, category);
//        fragment.setArguments(bundle);
        return fragment;
    }
//
//    public static DishesFragment newInstance() {
//        DishesFragment fragment = new DishesFragment();
////        Bundle bundle = new Bundle();
////        bundle.putParcelable(ARGS_CATEGORY, category);
////        fragment.setArguments(bundle);
//        return fragment;
//    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_dishes;
    }

    @Override
    protected void updateFollowingViewBinding() {
        setupMeusPedidosRecycleView();
        setupConfirmedOrderRecycleView();
        setupView();
    }

    private void setupView() {

    }

    @Override
    public void onViewCreated(final View viewCreated, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewCreated.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewCreated.post(new Runnable() {
                    public void run() {
                        if (view == null) return;
                        int fragmentWidth = view.getWidth();

                        ViewGroup.LayoutParams leftparams = leftLayoutMinhaConta.getLayoutParams();
                        leftparams.width = fragmentWidth / 3;
                        leftLayoutMinhaConta.setLayoutParams(leftparams);

                        ViewGroup.LayoutParams rightParams = rightLayoutMeusPedidos.getLayoutParams();
                        rightParams.width = fragmentWidth / 3;
                        rightLayoutMeusPedidos.setLayoutParams(rightParams);

//                        ViewGroup.LayoutParams centerparams = mRecyclerView.getLayoutParams();
//                        centerparams.width = fragmentWidth/3;
//                        mRecyclerView.setLayoutParams(centerparams);

                    }
                });
            }
        });
        viewCreated.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator.ofFloat(rightLayoutMeusPedidos,
                        View.TRANSLATION_X, rightLayoutMeusPedidos.getWidth()).setDuration(0).start();
                ObjectAnimator.ofFloat(leftLayoutMinhaConta,
                        View.TRANSLATION_X, -leftLayoutMinhaConta.getWidth()).setDuration(0).start();
                rightLayoutMeusPedidos.setVisibility(View.GONE);
                leftLayoutMinhaConta.setVisibility(View.GONE);
            }
        });
    }

    public void setupAsCategoryData(RvCategory category) {
        this.mCategory = category;
        initData();
    }

    public void setupAsSubCategoryData(RvSubCategory subCategory) {
        this.mSubCategory = subCategory;
        initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartItemsAmount();
    }

    private void initData() {
        mDishes = new ArrayList<>();
        mRestaurantId = ShareFunctions.readRestaurantId(getContext());
        if (dishMap == null || dishMap.size() == 0) {
            mRef.child(FirebaseConstants.DISHES_KEY).child(mRestaurantId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        dishMap.put(postSnapshot.getKey(), postSnapshot.getValue(RvDish.class));
                    }
                    processDishesData();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            processDishesData();
        }
    }

    private void processDishesData() {
        if (mCategory != null && mCategory.dishes != null && mCategory.dishes.size() > 0) {
            for (RvDish tdishes : mCategory.dishes) {
                if (dishMap.containsKey(tdishes.id)) {
                    RvDish rvDish = dishMap.get(tdishes.id);
                    rvDish.id = tdishes.id;
                    mDishes.add(rvDish);
                }
            }
            setupDishesListView();
        } else if (mSubCategory != null && mSubCategory.dishes != null && mSubCategory.dishes.size() > 0) {
            for (RvDish tdishes : mSubCategory.dishes) {
                if (dishMap.containsKey(tdishes.id)) {
                    RvDish rvDish = dishMap.get(tdishes.id);
                    rvDish.id = tdishes.id;
                    mDishes.add(rvDish);
                }
            }
            setupDishesListView();
        }
    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getArguments() != null) {
            mCategory = getArguments().getParcelable(ARGS_CATEGORY);
            mSubCategory = getArguments().getParcelable(ARGS_SUBCATEGORY);
        }
    }

    private void setupConfirmedOrderRecycleView() {
        mMinhaContaAdapter = new MinhaContaAdapter(getContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.mRvMinhaConta.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        this.mRvMinhaConta.setLayoutManager(mLayoutManager);
        this.mRvMinhaConta.setAdapter(mMinhaContaAdapter);

    }

    private void setupMeusPedidosRecycleView() {
        mMeusPedidosAdapter = new MeusPedidosAdapter(getContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.mRecycleViewMeusPedidos.setLayoutManager(mLayoutManager);
        this.mRecycleViewMeusPedidos.setAdapter(mMeusPedidosAdapter);
        mMeusPedidosAdapter.setOnRemoveItemClickListener(new MeusPedidosAdapter.OnRemoveItemClickListener() {
            @Override
            public void onRemoveItemClicked(CartItem cardItem, int position) {
                MyApplication.getInstance().CART.removeDish(cardItem.getDish());
                removeDishOutOfFirebase(cardItem.getDishId());
                mMeusPedidosAdapter.notifyItemRemoved(position);
                calculateTotalMeusPedidos();
            }
        });

    }

    private void updateDishToFirebase(CartItem cartItem) {
        mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).child(MyApplication.getInstance().CART.orderId).child(FirebaseConstants.DISH_ORDERS_KEY).child(cartItem.getDishId()).setValue(cartItem.getDishOrder());
    }

    private void updateToOrderdDishToFirebase() {
        Map<String, DishOrder> orderedDishes = MyApplication.getInstance().CART.makeOrder();
        mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).child(MyApplication.getInstance().CART.orderId).child(FirebaseConstants.ORDERED_DISH_ORDERS_KEY).setValue(orderedDishes);
        mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).child(MyApplication.getInstance().CART.orderId).child(FirebaseConstants.DISH_ORDERS_KEY).removeValue();
    }

    public void notifyPaySuccessfully() {
        double countLeft = DataUtils.countLeft();
        boolean isPayFull = false;
        if(countLeft <= 0){
            isPayFull = true;
        }
        List<PayingOrder> pays = MyApplication.getInstance().CART.getPays();
        mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).child(MyApplication.getInstance().CART.orderId).child(FirebaseConstants.PAID_ORDER_KEY).setValue(pays);
        if(isPayFull){
            mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).child(MyApplication.getInstance().CART.orderId).child(FirebaseConstants.CLOSED_KEY).setValue(isPayFull);

            //create new order
            Order order = new Order(Functions.readTableNumber(getContext()), System.currentTimeMillis(), false);
            String orderId = mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).push().getKey();
            mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).child(orderId).setValue(order);
            MyApplication.getInstance().CART.createNewOrder(orderId);
        }

    }

    private void removeDishOutOfFirebase(String orderId) {
        mRef.child(FirebaseConstants.ORDERS_KEY).child(mRestaurantId).child(MyApplication.getInstance().CART.orderId).child(FirebaseConstants.DISH_ORDERS_KEY).child(orderId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            }
        });
    }

    public void notifyMeusPedidosChanged(String id) {
        CartItem cartItem = MyApplication.getInstance().CART.findCartItem(id);

        updateDishToFirebase(cartItem);
        mMeusPedidosAdapter.notifyDataSetChanged();
        calculateTotalMeusPedidos();
    }

    private void setupDishesListView() {
        mDishesAdapter = new DishAdapter(getContext());
        this.mDishesAdapter.setOnItemClickListener(new DishAdapter.OnItemClickListener() {
            @Override
            public void onDishItemClicked(RvDish dishModel) {
                ((MenuListActivity) getActivity()).showDishDetailDialog(dishModel);
            }

            @Override
            public void onAddDishItemClicked(RvDish mRvDish) {
                Dish d = new Dish(mRvDish.text, mRvDish.price, mRvDish.price_bottle, mRvDish.image_url, mRvDish.desc);
                String restaurant_id = ShareFunctions.readRestaurantId(getActivity());
                DatabaseReference mDishRef = FirebaseDatabase.getInstance().getReference()
                        .child(FirebaseConstants.DISHES_KEY).child(restaurant_id).child(mRvDish.id);
                boolean isOverride = MyApplication.getInstance().CART.addDish(mDishRef, d, 1);
                CartItem cartItem = MyApplication.getInstance().CART.findCartItem(mRvDish.id);

                updateDishToFirebase(cartItem);
                animMoveMeusPedidos(false);
                if (isOverride) {
                    for (int i = 0; i < MyApplication.getInstance().CART.getItems().size(); i++) {
                        CartItem mCardItem = MyApplication.getInstance().CART.getItems().get(i);
                        if (mCardItem.getDishId().equals(mDishRef.getKey())) {
                            mMeusPedidosAdapter.notifyItemChanged(i);
                            final int finalI = i;
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mRecycleViewMeusPedidos.smoothScrollToPosition(finalI);
                                }
                            }, 50);
                            return;
                        }
                    }
                } else {
                    mMeusPedidosAdapter.notifyItemInserted(MyApplication.getInstance().CART.getItems().size() - 1);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecycleViewMeusPedidos.smoothScrollToPosition(MyApplication.getInstance().CART.getItems().size());
                        }
                    }, 50);
                }

                calculateTotalMeusPedidos();
            }
        });
        int numberOfColumns = 3;
        if (!isClosedMeusPedidos && !isClosedMinhaConta) {
            numberOfColumns = 1;
        } else if (!isClosedMeusPedidos || !isClosedMinhaConta) {
            numberOfColumns = 2;
        }
        layoutManager = new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        this.mRecyclerView.setLayoutManager(layoutManager);
        this.mRecyclerView.setAdapter(mDishesAdapter);
        this.mDishesAdapter.setRecycleViewCollection(mDishes);
        this.mDishesAdapter.notifyDataSetChanged();
    }

    private void calculateTotalMinhaConta() {
        double totalPrice = DataUtils.countMinhaConta();

        tvTotalMinhaConta.setText(StringUtils.formatMoney(totalPrice));
        tvOpcionalValue.setText(StringUtils.formatMoney(totalPrice / 10));
        tvTotalConfirmedOrderWithOpcional.setText(StringUtils.formatMoney(totalPrice / 10 + totalPrice));
        tvTotalPaid.setText(StringUtils.formatMoney(DataUtils.countPaids()));
        tvTotalLeft.setText(StringUtils.formatMoney(DataUtils.countLeft()));
    }

    private void calculateTotalMeusPedidos() {
        double totalPrice = 0.0;

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
        tvTotalMeusPedidos.setText(StringUtils.formatMoney(totalPrice));
        tvValorAtual.setText("VALOR ATUAL: " + StringUtils.formatMoney(totalPrice));
    }

    protected void updateCartItemsAmount() {
        int totalCartCount = MyApplication.getInstance().CART.getTotalCount();
        int totalOrderCount = MyApplication.getInstance().CART.getTotalOrderCount();
        if (totalCartCount + totalOrderCount != mTotalCartCount) {
            mTotalCartCount = totalCartCount + totalOrderCount;
            String text = String.valueOf(mTotalCartCount);
            if (mTotalCartCount <= 1) {
                text += " item";
            } else {
                text += " items";
            }
//            tvCartItemDescription.setText(text);
        }
//        if (mTotalCartCount == 0) {
//            rlBottomLayout.setVisibility(View.GONE);
//        } else {
//            rlBottomLayout.setVisibility(View.VISIBLE);
//        }
    }

    //    @OnClick(R.id.btnShoppingCart)
//    void onClickBtnShoppingCart() {
//        ((MenuListActivity) getActivity()).goToCarrinho();
//    }

    @OnClick(R.id.btnConfirmedOrders)
    void onClickConfirmedOrders() {
        animMoveMinhaConta(!isClosedMinhaConta);
    }

    @OnClick(R.id.btnOrderBeforeSendToTheKitchen)
    void onClickOrderBeforeSendToTheKitchen() {
        animMoveMeusPedidos(!isClosedMeusPedidos);

    }

    @OnClick(R.id.btnEnviarPedido)
    void onClickEnviarPedidoButton() {
        MyApplication.getInstance().CART.addToOrdered();
        updateToOrderdDishToFirebase();

        mMeusPedidosAdapter.notifyDataSetChanged();
        mMinhaContaAdapter.notifyDataSetChanged();
        calculateTotalMeusPedidos();
        calculateTotalMinhaConta();
    }

    @OnClick(R.id.btnPagarMinhaConta)
    void onClickPagarMinhaConta() {
        ((MenuListActivity) getActivity()).showPaymentGuide();
    }

    @OnClick(R.id.btnPagar)
    void onClickPayment() {
        ((MenuListActivity) getActivity()).showPaymentGuide();
    }

    private void animMoveMeusPedidos(boolean isClosed) {
        isClosedMeusPedidos = isClosed;

        rightLayoutMeusPedidos.animate().translationX((isClosed ? 1 : 0) * rightLayoutMeusPedidos.getWidth()).withStartAction(new Runnable() {
            @Override
            public void run() {
                if (!isClosedMeusPedidos) {
                    rightLayoutMeusPedidos.setVisibility(View.VISIBLE);
                }
                if (!isClosedMinhaConta && !isClosedMeusPedidos) {
                    layoutManager.setSpanCount(1);
                } else if (layoutManager.getSpanCount() == 3) {
                    layoutManager.setSpanCount(2);
                }
            }
        }).withEndAction(new TimerTask() {
            @Override
            public void run() {
                if (isClosedMeusPedidos) {
                    rightLayoutMeusPedidos.setVisibility(View.GONE);
                    if (!isClosedMinhaConta) {
                        layoutManager.setSpanCount(2);
                    }
                }
                if (isClosedMinhaConta && isClosedMeusPedidos) {
                    layoutManager.setSpanCount(3);
                }

            }
        }).setDuration(500).start();
    }

    private void animMoveMinhaConta(boolean isClosed) {
        isClosedMinhaConta = isClosed;
        leftLayoutMinhaConta.animate().translationX((isClosed ? -1 : 0) * leftLayoutMinhaConta.getWidth()).withStartAction(new Runnable() {
            @Override
            public void run() {
                if (!isClosedMinhaConta)
                    leftLayoutMinhaConta.setVisibility(View.VISIBLE);

                if (!isClosedMinhaConta && !isClosedMeusPedidos) {
                    layoutManager.setSpanCount(1);
                } else if (layoutManager.getSpanCount() == 3) {
                    layoutManager.setSpanCount(2);
                }
            }
        }).withEndAction(new TimerTask() {
            @Override
            public void run() {
                if (isClosedMinhaConta) {
                    leftLayoutMinhaConta.setVisibility(View.GONE);
                    if (!isClosedMeusPedidos) {
                        layoutManager.setSpanCount(2);
                    }
                }
                if (isClosedMinhaConta && isClosedMeusPedidos) {
                    layoutManager.setSpanCount(3);
                }

            }
        }).setDuration(500).start();
//        ObjectAnimator.ofFloat(leftLayoutMinhaConta,
//                View.TRANSLATION_X, (isClosed ? -1 : 0) * leftLayoutMinhaConta.getWidth()).setDuration(500).start();
    }

    public void notifyLoadDataFromFirebaseChanged() {
        mMeusPedidosAdapter.notifyDataSetChanged();
        mMinhaContaAdapter.notifyDataSetChanged();
        calculateTotalMeusPedidos();
        calculateTotalMinhaConta();
    }


}
