package com.example.fleissig.restaurantapp.ui.activities;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import com.example.fleissig.restaurantapp.R;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;

/**
 * Created by phuctran on 6/28/17.
 */

public abstract class BaseAdvertisingActivity extends BaseActivity {
    @BindView(R.id.videoAdvertising)
    VideoView videoAdvertising;

    private StorageReference mStorageReference;

    private void initView() {
//        videoAdvertising.setOnCompletionListener(this);
//        videoAdvertising.setOnErrorListener(this);
        videoAdvertising.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
//                startActivity(intent);
                return true;
            }
        });

    }
}
