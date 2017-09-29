package com.example.fleissig.restaurantapp.subcategory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.BackgroundActivity;
import com.example.fleissig.restaurantapp.category.CategoriesActivity;
import com.example.fleissig.restaurantapp.category.Category;
import com.example.fleissig.restaurantapp.category.DishesActivity;
import com.example.fleissig.restaurantapp.Functions;
import com.example.fleissig.restaurantapp.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class SubCategoriesActivity extends BackgroundActivity {
    public static final String SUBCATEGORY_KEY_EXTRA = "subcategory_key_extra";
    View mRootView;
    private FirebaseListAdapter<SubCategory> mAdapter;

    private String mRestaurantId;
    private String mCategoryName;
    private String mCategoryKey;

    @Override
    protected View getRootView() {
        return mRootView;
    }

    @Override
    protected void onResume() {
        super.onResume();

        String restaurantId = ShareFunctions.readRestaurantId(this);
        if (!restaurantId.equals(mRestaurantId)) {
            mRestaurantId = restaurantId;
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CategoriesActivity.CATEGORY_KEY_EXTRA, mCategoryKey);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategories);

        mRootView = findViewById(R.id.root);

        mCategoryName = "";

        if (savedInstanceState != null) {
            mCategoryKey = savedInstanceState.getString(CategoriesActivity.CATEGORY_KEY_EXTRA);
            mCategoryName =savedInstanceState.getString(CategoriesActivity.CATEGORY_NAME);
        }

        Intent intent = getIntent();
        if (intent != null) {
            String backgroundUrl = intent.getStringExtra(BackgroundActivity.REFERENCE_KEY);
            if (backgroundUrl != null) {
                mBackgroundRef = FirebaseStorage.getInstance().getReferenceFromUrl(backgroundUrl);
            }

            mCategoryKey = intent.getStringExtra(CategoriesActivity.CATEGORY_KEY_EXTRA);
            mCategoryName = intent.getStringExtra(CategoriesActivity.CATEGORY_NAME);
        }

        setUpToolbar(mCategoryName);

        loadAndSetBackground();

        mRestaurantId = ShareFunctions.readRestaurantId(this);

        ListView subcategoriesListView = (ListView) findViewById(R.id.subcategories_listview);
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        Functions.addEmptyHeader(this, subcategoriesListView);
        String restaurant_id = ShareFunctions.readRestaurantId(getBaseContext());
        mAdapter = new FirebaseListAdapter<SubCategory>(this, SubCategory.class,
                R.layout.my_simple_list_item_1,
                mRef.child(FirebaseConstants.CATEGORIES_KEY).child(mRestaurantId).child(mCategoryKey)
                        .child(FirebaseConstants.SUBCATEGORIES_KEY)) {
            @Override
            protected void populateView(View v, SubCategory subCategory, int position) {
                ((TextView) v.findViewById(R.id.category_text)).setText(subCategory.getText());
            }
        };
        subcategoriesListView.setAdapter(mAdapter);

        subcategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String subcategoryKey = mAdapter.getRef(position - 1).getKey();
                SubCategory subCategory = mAdapter.getItem(position - 1);
                if(subCategory.getDishes() != null && !subCategory.getDishes().isEmpty()) {
                    Intent intent = new Intent(SubCategoriesActivity.this, DishesSubCategories.class);
                    intent.putExtra(CategoriesActivity.CATEGORY_KEY_EXTRA, mCategoryKey);
                    intent.putExtra(SUBCATEGORY_KEY_EXTRA, subcategoryKey);
                    //intent.putExtra(CATEGORY_NAME, category.getText());
                    if (mBackgroundRef != null) {
                        intent.putExtra(BackgroundActivity.REFERENCE_KEY,
                                mBackgroundRef.toString());
                    }
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
