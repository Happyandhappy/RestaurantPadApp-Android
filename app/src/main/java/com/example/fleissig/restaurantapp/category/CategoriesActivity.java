package com.example.fleissig.restaurantapp.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.BackgroundActivity;
import com.example.fleissig.restaurantapp.Functions;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.WelcomeClientActivity;
import com.example.fleissig.restaurantapp.subcategory.SubCategoriesActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CategoriesActivity extends BackgroundActivity {
    private static final String TAG = CategoriesActivity.class.getSimpleName();
    public static final String CATEGORY_KEY_EXTRA = "category_key_extra";
    public static final String CATEGORY_NAME = "category_name";
    View mRootView;
    private FirebaseListAdapter<Category> mAdapter;
    private DatabaseReference mRef;
    private String mRestaurantId;
    private ListView mCategoriesListView;

    @Override
    protected View getRootView() {
        return mRootView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        mRootView = findViewById(R.id.root);

        setUpToolbar("Cardápio Categorias");
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }

        //MyApplication.getInstance().clear();

        loadAndSetBackground();

        mCategoriesListView = (ListView) findViewById(R.id.categories_list);

        Functions.addEmptyHeader(this, mCategoriesListView);

        mRef = FirebaseDatabase.getInstance().getReference();

        mRestaurantId = ShareFunctions.readRestaurantId(this);

        mCategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mAdapter != null) {
                    String categoryKey = mAdapter.getRef(position - 1).getKey();
                    Category category = mAdapter.getItem(position - 1);
                    if(category.getDishes() != null && !category.getDishes().isEmpty()){
                        Intent intent = new Intent(CategoriesActivity.this, DishesActivity.class);
                        intent.putExtra(CATEGORY_KEY_EXTRA, categoryKey);
                        intent.putExtra(CATEGORY_NAME, category.getText());
                        if (mBackgroundRef != null) {
                            intent.putExtra(BackgroundActivity.REFERENCE_KEY,
                                    mBackgroundRef.toString());
                        }
                        startActivity(intent);
                        return;
                    }
                    if(category.getSubcategories() != null && !category.getSubcategories().isEmpty()){
                        Intent intent = new Intent(CategoriesActivity.this, SubCategoriesActivity.class);
                        intent.putExtra(CATEGORY_KEY_EXTRA, categoryKey);
                        intent.putExtra(CATEGORY_NAME, category.getText());
                        if (mBackgroundRef != null) {
                            intent.putExtra(BackgroundActivity.REFERENCE_KEY,
                                    mBackgroundRef.toString());
                        }
                        startActivity(intent);
                        return;
                    }

                }
            }
        });

        setAdapter();
    }

    private void setAdapter() {
        mAdapter = new FirebaseListAdapter<Category>(this, Category.class,
                R.layout.my_simple_list_item_1,
                mRef.child(FirebaseConstants.CATEGORIES_KEY).child(mRestaurantId)) {
            @Override
            protected void populateView(View v, Category category, int position) {
                ((TextView) v.findViewById(R.id.category_text)).setText(category.getText());
            }
        };
        mCategoriesListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String restaurantId = ShareFunctions.readRestaurantId(this);
        if (!restaurantId.equals(mRestaurantId)) {
            mRestaurantId = restaurantId;
            mBackgroundRef = null;
            mRootView.setBackground(null);
            loadAndSetBackground();
            if (mAdapter != null) {
                mAdapter.notifyDataSetInvalidated();
                setAdapter();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdapter != null) mAdapter.cleanup();

        super.onDestroy();
    }
}
