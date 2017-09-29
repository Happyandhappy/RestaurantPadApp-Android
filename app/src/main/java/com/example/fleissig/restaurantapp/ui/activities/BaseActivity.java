package com.example.fleissig.restaurantapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.Restaurant;
import com.example.fleissig.restaurantapp.ui.fragments.bases.BaseFragment;
import com.example.fleissig.restaurantapp.ui.widgets.Toaster;
import com.example.fleissig.restaurantapp.utils.DataUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phuctran on 5/13/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseFragment.BaseFragmentResponder {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int IDLE_DELAY_MINUTES = 10;
    protected String mTopFragmentTag;

    @BindView(R.id.advPanel)
    View advPanel;
    private FirebaseDatabase mFirebaseInstance;
    protected DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        updateFollowingViewBinding(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initAdvFirebase();
    }

    private void initAdvFirebase() {
//        mFirebaseInstance.getReference().child(FirebaseConstants.MESSAGES_KEY).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, dataSnapshot.toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        Log.d(TAG, "onUserInteraction");
        if (!(this instanceof HomeActivity)) {
            delayedIdle(IDLE_DELAY_MINUTES);
        }

    }

    Handler _idleHandler = new Handler();
    Runnable _idleRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(BaseActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    };

    private void delayedIdle(int delayMinutes) {
        _idleHandler.removeCallbacks(_idleRunnable);
        _idleHandler.postDelayed(_idleRunnable, (delayMinutes * 1000 * 60));
    }

    protected abstract int getLayoutResource();

    protected abstract void updateFollowingViewBinding(Bundle savedInstanceState);

    @Override
    public void popFragment(BaseFragment basefragment) {
        Log.d(TAG, (new StringBuilder()).append("popFragment - called for fragment with tag: ").append(basefragment.getTag()).toString());
        FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
        fragmenttransaction.remove(basefragment);
        fragmenttransaction.commit();
    }

    @Override
    public void showFragment(BaseFragment fragment, String fragmentTag, int fragmentContainer) {
        Log.w(TAG, "showFragment - DEFAULT implementation called in BaseActivity");
        FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();//.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmenttransaction.replace(fragmentContainer, fragment, fragmentTag);
        mTopFragmentTag = fragmentTag;
//        fragmenttransaction.addToBackStack(mTopFragmentTag);
        fragmenttransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int iBackStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG, (new StringBuilder()).append("onBackPressed - backStackEntryCount is: ").append(iBackStackEntryCount).toString());
        if (iBackStackEntryCount <= 0) finish();

    }

    protected void showToast(int resourceText) {
        showToast(getResources().getString(resourceText), Toaster.Duration.LONG);
    }

    protected void showToast(int resourceText, Toaster.Duration duration) {
        showToast(getResources().getString(resourceText), duration);
    }

    protected void showToast(String text) {
        showToast(text, Toaster.Duration.LONG);
    }

    protected void showToast(String text, Toaster.Duration duration) {
        Toaster.showToast(this, text, duration);
    }

    @Override
    public void popBackStackByFragmentTag(String fragmentTag) {
        Log.w(TAG, "popBackStackByFragmentTag - DEFAULT implementation called in BaseActivity");
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackEntryCount; i++) {
            if (fragmentManager.getBackStackEntryAt(i).getName().equals(fragmentTag)) {
                while (backStackEntryCount > i) {
                    fragmentManager.popBackStack();
                    backStackEntryCount--;
                }
            }
        }
    }

    public void setActionBarTitle(CharSequence charsequence) {
        if (charsequence == null) {
            Log.w(TAG, "setActionBarTitle - title is null, so defaulting to full_app_name.");
            charsequence = getString(R.string.app_name);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(charsequence);
        }
    }

    public void setActionBarTitle(int actionBarTitle) {
        setActionBarTitle(getResources().getString(actionBarTitle));
    }

    public void setActionBarTitle(String actionBarTitle) {
        if (actionBarTitle == null) {
            Log.w(TAG, "setActionBarTitle - title is null, so defaulting to full_app_name.");
            actionBarTitle = getString(R.string.app_name);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
        }
    }
}
