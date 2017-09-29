package com.example.fleissig.mylibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class ShareFunctions {
    public static
    @NonNull
    String readRestaurantId(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String string = sharedPref.getString(
                context.getString(R.string.restaurant_list_key),
                context.getString(R.string.default_restaurant_id));
//        String value = getTablet();
        return "restaurant_id3";
    }

//    private static String getTablet(){
//        DatabaseReference mDishRef = FirebaseDatabase.getInstance().getReference()
//                .child("Tablet").child("1").child("restaurant_id");
//        return mDishRef.;
//    }

    public static void handleSignInResponse(Activity caller, Intent startActivityIntent, View rootView, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
            caller.startActivity(startActivityIntent);
            caller.finish();
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(rootView, R.string.sign_in_cancelled);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(rootView, R.string.no_internet_connection);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(rootView, R.string.unknown_error);
                return;
            }
        }

        showSnackbar(rootView, R.string.unknown_sign_in_response);
    }

    public static void showSnackbar(View mRootView, @StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @NonNull
    public static Intent makeIntent(Context context, Class c) {
        Intent intent = new Intent(context, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void writeRegularRating(final String TAG, final float rating, String dishId) {
        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.RATING_KEY);
        DatabaseReference ratingDishId = ratingRef.child(dishId);
        ratingDishId.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Rating r = mutableData.getValue(Rating.class);
                if (r == null) {
                    r = new Rating();
                    r.inc(rating);
                    mutableData.setValue(r);
                    return Transaction.success(mutableData);
                }

                r.inc(rating);
                Log.d(TAG, String.valueOf(r.getAverage()));

                // Set value and report transaction success
                mutableData.setValue(r);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
}
