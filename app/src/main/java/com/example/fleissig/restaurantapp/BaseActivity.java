package com.example.fleissig.restaurantapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.order.CartActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public abstract class BaseActivity extends AppCompatActivity {
    private final static String TAG = BaseActivity.class.getSimpleName();
    private static final int SETTINGS_REQUEST = 1000;
    private int mTotalCartCount = 0;
    private Button mCartButton;
    private TextView textRestaurantName;

    abstract protected View getRootView();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(),
                                    WelcomeClientActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            ShareFunctions.showSnackbar(getRootView(), R.string.sign_out_failed);
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, SETTINGS_REQUEST);
                break;
            case R.id.menu_signout:
                signOut();
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCartItemsAmount();
    }

    protected void updateCartItemsAmount() {
        int totalCartCount = MyApplication.getInstance().CART.getTotalCount();
        if (totalCartCount != mTotalCartCount) {
            mTotalCartCount = totalCartCount;
            String text = String.valueOf(mTotalCartCount);
            if (mTotalCartCount == 0) {
                text += " item";
            }
            else {
                text += " items";
            }
            mCartButton.setText(text);
        }
    }

    protected void setUpToolbar(String title) {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        ((TextView) myToolbar.findViewById(R.id.toolbar_title)).setText(title);
        mCartButton = (Button) myToolbar.findViewById(R.id.cart_button);
        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(myToolbar);
        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
        }
        textRestaurantName = (TextView) myToolbar.findViewById(R.id.toolbar_restaurant_title);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SETTINGS_REQUEST:
                Log.d(TAG, "onActivityResult(): SETTINGS_REQUEST");
                break;
        }
    }

    public void setTextRestaurantName(String restaurantName){
        textRestaurantName.setText(restaurantName);
    }

}
