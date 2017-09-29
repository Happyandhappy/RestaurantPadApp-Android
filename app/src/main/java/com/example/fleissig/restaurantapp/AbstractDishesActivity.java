package com.example.fleissig.restaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.category.CategoriesActivity;
import com.firebase.ui.database.FirebaseIndexListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by carlosnetto on 15/04/17.
 */

public abstract class AbstractDishesActivity extends BackgroundActivity{
    public static final String DISH_KEY_EXTRA = "dish_key_extra";

    View mRootView;
    private FirebaseIndexListAdapter<Dish> mAdapter;
    private String mRestaurantId;
    private String mOriginKey;

    protected abstract String getOriginKeySavedInstance(Bundle savedInstanceState);
    protected abstract String getOriginKeyIntent(Intent intent);
    protected abstract DatabaseReference getDatabaseReference(String mRestaurantId, String mCategoryKey, String mOriginKey);
    protected abstract void putOutState(Bundle outState, String mOriginKey);
    protected abstract Intent generateIntent();

    protected String getCategoryKeySavedInstance(Bundle savedInstanceState) {
        return savedInstanceState.getString(CategoriesActivity.CATEGORY_KEY_EXTRA);
    }

    protected String getCategoryKeyIntent(Intent intent) {
        return intent.getStringExtra(CategoriesActivity.CATEGORY_KEY_EXTRA);
    }

    protected void putCategoryOutState(Bundle outState, String mOriginKey) {
        outState.putString(CategoriesActivity.CATEGORY_KEY_EXTRA, mOriginKey);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dishes);

        mRootView = findViewById(R.id.root);

        setUpToolbar("Pratos");
        String mCategoryKey = "";
        if (savedInstanceState != null) {
            mOriginKey = getOriginKeySavedInstance(savedInstanceState);
            mCategoryKey = getCategoryKeySavedInstance(savedInstanceState);
        }

        Intent intent = getIntent();
        if (intent != null) {
            String backgroundUrl = intent.getStringExtra(BackgroundActivity.REFERENCE_KEY);
            if (backgroundUrl != null) {
                mBackgroundRef = FirebaseStorage.getInstance().getReferenceFromUrl(backgroundUrl);
            }
            mCategoryKey = getCategoryKeyIntent(intent);
            mOriginKey = getOriginKeyIntent(intent);
        }

        mRestaurantId = ShareFunctions.readRestaurantId(this);
        DatabaseReference mOriginRef = getDatabaseReference(mRestaurantId, mCategoryKey, mOriginKey);


        loadAndSetBackground();
        

        ListView dishesListView = (ListView) findViewById(R.id.dishes_listview);

        Functions.addEmptyHeader(this, dishesListView);
        String restaurant_id = ShareFunctions.readRestaurantId(getBaseContext());
        mAdapter = new FirebaseIndexListAdapter<Dish>(this, Dish.class,
                R.layout.dishes_dish_item,
                mOriginRef.child(FirebaseConstants.DISHES_KEY),
                mOriginRef.getRoot().child(FirebaseConstants.DISHES_KEY).child(restaurant_id))
        {
            @Override
            protected void populateView(View v, Dish dish, int position) {
                ((TextView) v.findViewById(R.id.dish_name_textview))
                        .setText(dish.text);
                Double price = dish.price;
                TextView priceTextView = (TextView) v.findViewById(R.id.dish_price_textview);
                if (price != null) {
                    priceTextView.setText("$" + String.valueOf(price));
                }
                Double bottlePrice = dish.price_bottle;
                if (bottlePrice != null) {
                    priceTextView.setText("$" + String.valueOf(bottlePrice) + " por garrafa");
                }
            }
        };
        dishesListView.setAdapter(mAdapter);

        dishesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String dishKey = mAdapter.getRef(position - 1).getKey();
                Intent intent = generateIntent();
                intent.putExtra(DISH_KEY_EXTRA, dishKey);
                startActivity(intent);
            }
        });
    }

    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

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
        putOutState(outState, mOriginKey);
        super.onSaveInstanceState(outState);
    }
}
