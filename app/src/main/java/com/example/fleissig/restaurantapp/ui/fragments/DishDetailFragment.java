package com.example.fleissig.restaurantapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.activities.DishDetailActivity;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseDetailDishFragment;
import com.example.fleissig.restaurantapp.ui.models.RvDish;
import com.example.fleissig.restaurantapp.utils.PicassoUtils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by phuctran on 5/22/17.
 */

public class DishDetailFragment extends BaseDetailDishFragment {
    private static final String TAG = DishDetailFragment.class.getSimpleName();
    private static final String ARGS_DISH_KEY = "ARGS_DISH_KEY";
    private RvDish mRvDish;
    private int mQuanlity;

    @BindView(R.id.ivDishAvatar)
    ImageView ivDishAvatar;
    @BindView(R.id.tvDishName)
    TextView tvDishName;
    @BindView(R.id.tvDishDescription)
    TextView tvDishDescription;
    @BindView(R.id.btnAdicionar)
    Button btnAdicionar;
    @BindView(R.id.btnCancelar)
    Button btnCancelar;
    @BindView(R.id.btnMinusItem)
    Button btnMinusItem;
    @BindView(R.id.btnPlusItem)
    Button btnPlusItem;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.quantity_textview)
    TextView tvQuantity;


    public static DishDetailFragment newInstance(RvDish dishKey) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_DISH_KEY, dishKey);

        DishDetailFragment fragment = new DishDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getArguments() != null) {
            mRvDish = getArguments().getParcelable(ARGS_DISH_KEY);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_dish_detail;
    }

    @Override
    protected void updateFollowingViewBinding() {
        initData();
    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }

    private void initData() {
        processLoadData(mRvDish);
    }

    private void processLoadData(RvDish mDish) {
        CartItem cartItem = MyApplication.getInstance().CART.findCartItem(mRvDish.id);
        if (cartItem != null) {
            mQuanlity = cartItem.getDishOrder().getQuantity();
        }
        if (mQuanlity < 1) {
            mQuanlity = 1;
        }
        //PicassoUtils.setImage(mDish.image_url, ivDishAvatar, R.drawable.no_image);
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference mImageRef = null;

        String url = mDish.image_url;
        if (url != null) mImageRef = mStorage.getReferenceFromUrl(url);
        Glide.with(MyApplication.getInstance())
                .using(new FirebaseImageLoader())
                .load(mImageRef)
                .error(R.drawable.no_image)
                .into(ivDishAvatar);

        tvDishDescription.setText(mDish.desc);
        tvDishName.setText(mDish.text);
        updateUI();
    }

    @OnClick(R.id.btnMinusItem)
    void onClickBtnMinusItem() {
        mQuanlity = mQuanlity > 1 ? mQuanlity - 1 : 1;
        updateUI();
    }

    @OnClick(R.id.btnPlusItem)
    void onClickBtnPlusItem() {
        mQuanlity++;
        updateUI();
    }

    @OnClick(R.id.btnAdicionar)
    void onClickBtnAdicionar() {
        Dish d = new Dish(mRvDish.text, mRvDish.price, mRvDish.price_bottle, mRvDish.image_url, mRvDish.desc);
        String restaurant_id = ShareFunctions.readRestaurantId(getContext());
        DatabaseReference mDishRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.DISHES_KEY).child(restaurant_id).child(mRvDish.id);
        MyApplication.getInstance().CART.addDish(mDishRef, d, mQuanlity);
        ((DishDetailActivity) getActivity()).goToPreviousScreen();
    }

    @OnClick(R.id.btnCancelar)
    void onClickBtnCancelar() {
        ((DishDetailActivity) getActivity()).goToPreviousScreen();
    }

    private void updateUI() {
        double price = 0.0;
        if (mRvDish.price > 0) {
            price = mRvDish.price;
        } else if (mRvDish.price_bottle > 0) {
            price = mRvDish.price_bottle;
        }
        double totalPrice = price * mQuanlity;
        tvQuantity.setText(mQuanlity + "");
        tvPrice.setText("$ " + String.valueOf(totalPrice));
    }
}
