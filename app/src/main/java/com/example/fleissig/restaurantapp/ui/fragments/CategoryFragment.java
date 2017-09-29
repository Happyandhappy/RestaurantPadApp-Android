package com.example.fleissig.restaurantapp.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.Restaurant;
import com.example.fleissig.restaurantapp.category.Category;
import com.example.fleissig.restaurantapp.subcategory.SubCategory;
import com.example.fleissig.restaurantapp.ui.adapters.CategoryAdapter;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseMenuListFragment;
import com.example.fleissig.restaurantapp.ui.models.RvCategory;
import com.example.fleissig.restaurantapp.ui.models.RvDish;
import com.example.fleissig.restaurantapp.ui.models.RvSubCategory;
import com.example.fleissig.restaurantapp.utils.DataUtils;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by phuctran on 5/17/17.
 */

public class CategoryFragment extends BaseMenuListFragment {
    private static final String TAG = CategoryFragment.class.getSimpleName();

    @BindView(R.id.categories_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;

    private List<RvCategory> mRvCategory = new ArrayList();
    private CategoryAdapter mCategoryAdapter;

    private FirebaseListAdapter<Category> mAdapter;
    private String mRestaurantId;
    private boolean isFirstItemChoosed = false;
    protected StorageReference mBackgroundRef;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_category;
    }

    @Override
    protected void updateFollowingViewBinding() {
        setupData();
    }


    private void setupData() {
        mRestaurantId = ShareFunctions.readRestaurantId(getContext());
        loadAndSetRestaurantLogo();
        mRef.child(FirebaseConstants.CATEGORIES_KEY).child(mRestaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, postSnapshot.toString());
                    Category c = postSnapshot.getValue(Category.class);
                    RvCategory rvc = new RvCategory();
                    rvc.text = c.getText();
                    rvc.subcategories = new ArrayList<>();
                    if (c.getSubcategories() != null) {
                        for (Map.Entry<String, SubCategory> entry : c.getSubcategories().entrySet()) {
                            String key = entry.getKey();
                            SubCategory value = entry.getValue();
                            RvSubCategory rvSubCategory = new RvSubCategory(value.getText());

                            if (value.getDishes() != null && value.getDishes().size() > 0) {
                                rvSubCategory.dishes = new ArrayList<RvDish>();
                                for (Map.Entry<String, Boolean> dishentry : value.getDishes().entrySet()) {
                                    rvSubCategory.dishes.add(new RvDish(dishentry.getKey()));
                                }
                            }

                            rvc.subcategories.add(rvSubCategory);
                        }
                    }
                    rvc.dishes = new ArrayList<RvDish>();
                    if (c.getDishes() != null && c.getDishes().size() > 0) {
                        for (Map.Entry<String, Boolean> entry : c.getDishes().entrySet()) {
                            String key = entry.getKey();
                            rvc.dishes.add(new RvDish(key));
                        }
                    }
                    mRvCategory.add(rvc);
                }
                setupListView();
                if (mRvCategory != null && mRvCategory.size() > 0) {
                    if (mRvCategory.get(0).dishes != null && mRvCategory.get(0).dishes.size() > 0) {
                        goToDishesFragment(mRvCategory.get(0));
                    } else {
                        if (mRvCategory.get(0).subcategories != null && mRvCategory.get(0).subcategories.size() > 0) {
                            goToDishesFragment(mRvCategory.get(0).subcategories.get(0));
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupListView() {
        mCategoryAdapter = new CategoryAdapter(getContext(), mRvCategory);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCategoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onSubCategoryItemClicked(RvSubCategory dishModel) {
                goToDishesFragment(dishModel);
            }

            @Override
            public void onCategoryItemClicked(RvCategory categoryModel) {
                goToDishesFragment(categoryModel);
            }
        });
    }

    @Override
    protected String getSubclassName() {
        return TAG;
    }

    protected void loadAndSetRestaurantLogo() {
        if (mBackgroundRef == null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.RESTAURANTS_KEY)
                    .child(ShareFunctions.readRestaurantId(getActivity()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                            DataUtils.currentRestaurant = restaurant;
                            if (restaurant != null &&  restaurant.background!= null) {
                                mBackgroundRef = FirebaseStorage.getInstance().getReferenceFromUrl(restaurant.background);
                                setRestaurantLogo();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "Reading restaurant has been failed");
                        }
                    });
        } else {
            setRestaurantLogo();
        }
    }

    private void setRestaurantLogo(){
        Glide.with(getActivity().getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(mBackgroundRef).asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), resource);
                        ivLogo.setImageDrawable(bitmapDrawable);
                    }
                });
    }
}
