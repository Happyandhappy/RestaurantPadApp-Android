package com.example.fleissig.restaurantapp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class BackgroundActivity extends BaseActivity {
    public static final String REFERENCE_KEY = "reference";
    private static final String TAG = BackgroundActivity.class.getSimpleName();
    protected StorageReference mBackgroundRef;

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

    protected void loadAndSetBackground() {
        if (mBackgroundRef == null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.RESTAURANTS_KEY)
                    .child(ShareFunctions.readRestaurantId(this))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                            String background = null;
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            if (restaurant != null && (background = restaurant.background) != null) {
                                mBackgroundRef = storage.getReferenceFromUrl(background);
                                setBackground();
                                setTextRestaurantName(restaurant.name);
                            }
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
                        getRootView().setBackground(bitmapDrawable);
                    }
                });
    }
}
