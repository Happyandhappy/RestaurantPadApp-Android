package com.example.fleissig.restaurantserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class WelcomeServerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = WelcomeServerActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    View mRootView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_welcome);

        mRootView = findViewById(R.id.root);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(makeIntent());
            finish();
        }
    }

    @NonNull
    private Intent makeIntent() {
        return ShareFunctions.makeIntent(getApplicationContext(), MainActivity.class);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.sign_in_button:
                List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
                selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

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
            ShareFunctions.handleSignInResponse(this, makeIntent(), mRootView, resultCode, data);
        }

//        Functions.showSnackbar(mRootView, R.string.unknown_response);
    }
}
