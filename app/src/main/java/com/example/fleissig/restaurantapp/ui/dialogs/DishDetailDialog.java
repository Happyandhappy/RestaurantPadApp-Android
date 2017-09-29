package com.example.fleissig.restaurantapp.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.activities.DishDetailActivity;
import com.example.fleissig.restaurantapp.ui.activities.MenuListActivity;
import com.example.fleissig.restaurantapp.ui.fragments.DishDetailFragment;
import com.example.fleissig.restaurantapp.ui.fragments.DishesFragment;
import com.example.fleissig.restaurantapp.ui.models.RvDish;
import com.example.fleissig.restaurantapp.utils.StringUtils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by phuctran on 6/14/17.
 */

public class DishDetailDialog extends DialogFragment {
    private static final String ARGS_DISH_KEY = "ARGS_DISH_KEY";

    @BindView(R.id.ivDishAvatar)
    ImageView ivDishAvatar;
    @BindView(R.id.tvDishName)
    TextView tvDishName;
    @BindView(R.id.tvDishDescription)
    TextView tvDishDescription;
    @BindView(R.id.btnAdicionar)
    Button btnAdicionar;
    @BindView(R.id.btnMinusItem)
    Button btnMinusItem;
    @BindView(R.id.btnPlusItem)
    Button btnPlusItem;
    @BindView(R.id.quantity_textview)
    TextView tvQuantity;
    @BindView(R.id.tvDishPrice)
    TextView tvDishPrice;
    @BindView(R.id.btnXClose)
    Button btnXClose;
    private RvDish mRvDish;
    private int mQuanlity;
    private Unbinder unbinder;

    public static DishDetailDialog newInstance(RvDish dishKey) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_DISH_KEY, dishKey);

        DishDetailDialog fragment = new DishDetailDialog();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.dialog_dish_detail, container,
                false);
        init(v);
        unbinder = ButterKnife.bind(this, v);
        processLoadData(mRvDish);
        return v;
    }

    private void init(View view) {
        int width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
        int height = getResources()
                .getDimensionPixelSize(R.dimen.dialog_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LinearLayout root = new LinearLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
        tvDishPrice.setText(StringUtils.formatMoney(mDish.price));
        tvQuantity.setText(mQuanlity+"");
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
        String restaurant_id = ShareFunctions.readRestaurantId(getActivity());
        DatabaseReference mDishRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.DISHES_KEY).child(restaurant_id).child(mRvDish.id);
        MyApplication.getInstance().CART.setDish(
                mDishRef, d, mQuanlity);
        FragmentManager fm = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        DishesFragment parent = (DishesFragment) fm.findFragmentByTag(MenuListActivity.DISHES_FRAGMENT);
        parent.notifyMeusPedidosChanged(mDishRef.getKey());
        getDialog().dismiss();
    }

    @OnClick(R.id.btnXClose)
    void onClickClose() {
        getDialog().dismiss();
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
//        tvPrice.setText("$ " + String.valueOf(totalPrice));
    }
}
