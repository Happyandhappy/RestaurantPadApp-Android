package com.example.fleissig.restaurantapp.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.Restaurant;
import com.example.fleissig.restaurantapp.WelcomeClientActivity;
import com.example.fleissig.restaurantapp.category.CategoriesActivity;
import com.example.fleissig.restaurantapp.utils.DataUtils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phuctran on 5/13/17.
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int SPLASH_SCREEN_SHOW_TIME_MS = 2000;

    public static final String REFERENCE_KEY = "reference";

    private FirebaseAuth mAuth;
    private long currentTimeMillis = 0;
    protected StorageReference mBackgroundRef;
    private Runnable getOverTheSplash = new Runnable() {
        @Override
        public void run() {
            if (mAuth.getCurrentUser() == null) {
                startActivity(ShareFunctions.makeIntent(getApplicationContext(),
                        WelcomeClientActivity.class));
            } else {
                Intent intent = new Intent(SplashActivity.this, MenuListActivity.class);
                startActivity(intent);
            }
            SplashActivity.this.finish();
        }
    };
    private Handler handler = new Handler();

    @BindView(R.id.rootView)
    View rootView;
    @BindView(R.id.tvRestaurantName)
    TextView tvRestaurantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created Splash");
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mBackgroundRef != null) {
            outState.putString(REFERENCE_KEY, mBackgroundRef.toString());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        final String stringRef = savedInstanceState.getString(REFERENCE_KEY);
        if (stringRef == null) {
            return;
        }

        mBackgroundRef = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().hasVisitedSplash = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFirebase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(getOverTheSplash);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        if (mAuth.getCurrentUser() == null) {
            handler.postDelayed(getOverTheSplash, SPLASH_SCREEN_SHOW_TIME_MS);

        } else {
            currentTimeMillis = System.currentTimeMillis();
            loadAndSetBackground();
        }


    }


    protected void loadAndSetBackground() {
        if (mBackgroundRef == null) {
            FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConstants.RESTAURANTS_KEY)
                    .child(ShareFunctions.readRestaurantId(this))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                            DataUtils.currentRestaurant = restaurant;

                            tvRestaurantName.setText(restaurant.name);
                            setBackground();

                            handler.postDelayed(getOverTheSplash, System.currentTimeMillis() - currentTimeMillis > SPLASH_SCREEN_SHOW_TIME_MS ? 0 : SPLASH_SCREEN_SHOW_TIME_MS - (System.currentTimeMillis() - currentTimeMillis));


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "Reading restaurant has been failed");
                        }
                    });
        } else {
            setBackground();
        }


    }

    private void setBackground() {
        Glide.with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(mBackgroundRef).asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), resource);
                        rootView.setBackground(bitmapDrawable);
                    }
                });
    }
}
