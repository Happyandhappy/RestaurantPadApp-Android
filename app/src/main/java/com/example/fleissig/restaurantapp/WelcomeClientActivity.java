package com.example.fleissig.restaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.category.CategoriesActivity;
import com.example.fleissig.restaurantapp.ui.activities.MenuListActivity;
import com.example.fleissig.restaurantapp.ui.activities.SplashActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class WelcomeClientActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = WelcomeClientActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    private final Class<MenuListActivity> c = MenuListActivity.class;
    View mRootView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mRootView = findViewById(R.id.root);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.explore_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(ShareFunctions.makeIntent(getApplicationContext(), c));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.explore_button:
                mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(ShareFunctions.makeIntent(getApplicationContext(), c));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WelcomeClientActivity.this, "Unable to sign in anonymously.",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getMessage());
                    }
                });
                break;
            case R.id.sign_in_button:
                List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
                selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
                selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                                .setTheme(R.style.DarkTheme)
                                .setLogo(R.drawable.firebase_auth_120dp)
                                .setProviders(selectedProviders)
                                .setTosUrl(FirebaseConstants.FIREBASE_TOS_URL)
                                .setIsSmartLockEnabled(true)
                                .build(),
                        RC_SIGN_IN);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            ShareFunctions.handleSignInResponse(this, ShareFunctions.makeIntent(getApplicationContext(), c),
                    mRootView, resultCode, data);
        }

//        Functions.showSnackbar(mRootView, R.string.unknown_response);
    }
}
