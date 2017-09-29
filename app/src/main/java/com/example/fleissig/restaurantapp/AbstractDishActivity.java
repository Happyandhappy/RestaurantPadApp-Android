package com.example.fleissig.restaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.Rating;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.category.DishActivity;
import com.example.fleissig.restaurantapp.category.DishesActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by carlosnetto on 15/04/17.
 */

public class AbstractDishActivity extends BaseActivity {
    public static final int REQUEST_CODE = 1000;
    private static final String TAG = DishActivity.class.getSimpleName();
    private Dish mDish;
    private DatabaseReference mDishRef;
    private Button mAddToCartButton;
    private FirebaseStorage mStorage;
    private ImageView mDishImageView;
    private StorageReference mImageRef = null;
    private View mRootView;
    private String mDishKey;

    @Override
    protected View getRootView() {
        return mRootView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);

        mRootView = findViewById(R.id.activity_dish);

        setUpToolbar("");

        if (savedInstanceState != null) {
            mDishKey = savedInstanceState.getString(AbstractDishesActivity.DISH_KEY_EXTRA);
        }

        Intent intent = getIntent();
        if (intent != null) {
            mDishKey = intent.getStringExtra(AbstractDishesActivity.DISH_KEY_EXTRA);
        }
        String restaurant_id = ShareFunctions.readRestaurantId(getBaseContext());
        mDishRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.DISHES_KEY).child(restaurant_id).child(mDishKey);

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_ratingbar);

        final TextView dishNameTextView = (TextView) findViewById(R.id.dish_name_textview);
        final TextView dishPriceTextView = (TextView) findViewById(R.id.dish_price_textview);
        final TextView dishDescTextView = (TextView) findViewById(R.id.dish_desc_textview);

        mDishImageView = (ImageView) findViewById(R.id.dish_imageview);

        mStorage = FirebaseStorage.getInstance();

        DatabaseReference ratingDishRef = mDishRef.getRoot().child(FirebaseConstants.RATING_KEY)
                .child(mDishRef.getKey());
        ratingDishRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Rating r = dataSnapshot.getValue(Rating.class);
                if (r != null) {
                    ratingBar.setRating(r.getAverage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });

        mDishRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    mDish = dataSnapshot.getValue(Dish.class);
                    dishNameTextView.setText(mDish.text);

                    dishDescTextView.setText(mDish.desc == null ? "" : mDish.desc);

                    String text = "$";
                    Double price = mDish.price;
                    if (price != null) {
                        text += String.valueOf(price);
                    }
                    Double priceBottle = mDish.price_bottle;
                    if (priceBottle != null) {
                        text += String.valueOf(priceBottle) + " per bottle";
                    }
                    dishPriceTextView.setText(text);

                    String url = mDish.image_url;
                    if (url != null) mImageRef = mStorage.getReferenceFromUrl(url);

                    // Load the image using Glide
                    Glide.with(getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(mImageRef)
                            .error(R.drawable.no_image)
                            .into(mDishImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled(): " + databaseError.getMessage());
            }
        });

        mAddToCartButton = (Button) findViewById(R.id.add_to_card_button);
        mAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDish != null) {
                    MyApplication.getInstance().CART.addDish(mDishRef, mDish);
                    updateCartItemsAmount();
                }
            }
        });

        Button postCommentButton = (Button) findViewById(R.id.post_comment_button);
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAddToCartButton.setEnabled(Functions.readPossibilityChangeCartItem(this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(DishesActivity.DISH_KEY_EXTRA, mDishKey);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String comment = data.getStringExtra(ReviewActivity.COMMENT_EXTRA);
            MyApplication app = MyApplication.getInstance();
            if (comment != null) {
                app.dishCommentMap.put(mDishRef, comment);
            }
            float rating = data.getFloatExtra(ReviewActivity.RATING_EXTRA, 0);
            if (rating > 0) app.dishRatingMap.put(mDishRef, rating);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
