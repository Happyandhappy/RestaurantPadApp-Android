package com.example.fleissig.restaurantapp.ui.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.WelcomeClientActivity;
import com.example.fleissig.restaurantapp.services.DownloadService;
import com.example.fleissig.restaurantapp.ui.models.AdvVideo;
import com.example.fleissig.restaurantapp.utils.bus.BusCenter;
import com.example.fleissig.restaurantapp.utils.bus.SubscribleEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.List;
import java.util.Set;

import static com.example.fleissig.restaurantapp.services.DownloadService.DOWNLOAD_PODCAST_NAME;
import static com.example.fleissig.restaurantapp.services.DownloadService.DOWNLOAD_PODCAST_RETRY;
import static com.example.fleissig.restaurantapp.services.DownloadService.DOWNLOAD_PODCAST_URL;
import static com.example.fleissig.restaurantapp.services.DownloadService.DOWNLOAD_STATUS_FAILURE;
import static com.example.fleissig.restaurantapp.services.DownloadService.DOWNLOAD_STATUS_SUCCESS;
import static com.example.fleissig.restaurantapp.utils.bus.SubscribleEvent.EVENT_DOWNLOAD_SERVICE;

/**
 * Created by phuctran on 6/28/17.
 */

public class HomeActivity extends BaseActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private List<String> mFileLocalUrl = new ArrayList<>();
    private int currentUrlPlayingIndex;
    private boolean isPlaying = false;
    private boolean isPause = false;

    @BindView(R.id.videoAdvertising)
    VideoView videoAdvertising;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void updateFollowingViewBinding(Bundle savedInstanceState) {
        initView();
        loadAndSetAdvertising();
    }

    private void initView() {
        BusCenter.getInstance().register(this);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(ShareFunctions.makeIntent(getApplicationContext(),
                    WelcomeClientActivity.class));
            finish();
            return;
        }
        MediaController mediaController = new MediaController(this);
        mediaController.setVisibility(View.GONE);
        mediaController.setAnchorView(videoAdvertising);
        videoAdvertising.setMediaController(mediaController);
        videoAdvertising.setOnCompletionListener(this);
        videoAdvertising.setOnErrorListener(this);
        videoAdvertising.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!MyApplication.getInstance().hasVisitedSplash) {
                        Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                        startActivity(intent);

                    }
                    HomeActivity.this.finish();
                    return true;
                }
                return false;

            }
        });

    }

    @Override
    protected void onPause() {
        isPause = true;
        if (videoAdvertising != null && videoAdvertising.isPlaying()) {
            videoAdvertising.pause();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        isPause = false;
        if (isPlaying) {
            videoAdvertising.start();
        }
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusCenter.getInstance().unregister(this);
        Log.d(TAG, "onDestroy Activity");
    }


    protected void loadAndSetAdvertising() {
        FirebaseDatabase.getInstance().getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AdvVideo advertising = dataSnapshot.getValue(AdvVideo.class);
                        mFileLocalUrl.clear();
                        if (advertising.add != null) {
                            for (Map.Entry<String, String> entry : advertising.add.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();

                                Intent msgIntent = new Intent(getApplicationContext(), DownloadService.class);
                                msgIntent.putExtra(DOWNLOAD_PODCAST_URL, value);
                                msgIntent.putExtra(DOWNLOAD_PODCAST_NAME, key);
                                startService(msgIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (mFileLocalUrl.size() > 0) {
                            forcePlayingAdv();
                        }
                        Log.d(TAG, "Reading restaurant has been failed");
                    }
                });
    }

    private void forcePlayingAdv() {
        Log.d(TAG, "forcePlayingAdv " + mFileLocalUrl.size());
        if (mFileLocalUrl.size() > 0) {
            Uri video = Uri.fromFile(new File(mFileLocalUrl.get(currentUrlPlayingIndex)));
            currentUrlPlayingIndex++;
            if (currentUrlPlayingIndex >= mFileLocalUrl.size())
                currentUrlPlayingIndex = 0;
            videoAdvertising.setVideoURI(video);
            if (!isPause) {
                videoAdvertising.start();
            }
            isPlaying = true;
        }
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.d(TAG, "onError");
        int oldIndex = currentUrlPlayingIndex - 1;
        if(oldIndex < 0){
            oldIndex = 0;
        }
        isPlaying = false;
        File errorFile = new File(mFileLocalUrl.get(oldIndex));
        errorFile.delete();
        mFileLocalUrl.remove(oldIndex);
        if(currentUrlPlayingIndex > 0){
            currentUrlPlayingIndex --;
        }
        if(mFileLocalUrl.size() >0){
            forcePlayingAdv();
            return true;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onCompletion");
        isPlaying = false;
        forcePlayingAdv();
    }

    @Subscribe
    public void getMessage(SubscribleEvent subscribleEvent) {
        Log.d(TAG, subscribleEvent.toString());
        if (subscribleEvent.event.equals(EVENT_DOWNLOAD_SERVICE)) {
            if (subscribleEvent.data.getString(SubscribleEvent.EX_DOWNLOAD_STATUS).equals(DOWNLOAD_STATUS_SUCCESS)) {
                String localUrl = subscribleEvent.data.getString(SubscribleEvent.EX_DOWNLOAD_PODCAST_LOCAL_URL);
                for (String url : mFileLocalUrl) {
                    if (url.equals(localUrl)) {
                        return;
                    }
                }
                mFileLocalUrl.add(localUrl);
                Log.d(TAG, "getMessage " + isPlaying + " " + videoAdvertising.isPlaying());
                if (!isPlaying && !videoAdvertising.isPlaying()) {
                    forcePlayingAdv();
                }
            } else if (subscribleEvent.data.getString(SubscribleEvent.EX_DOWNLOAD_STATUS).equals(DOWNLOAD_STATUS_FAILURE)) {
                String name = subscribleEvent.data.getString(SubscribleEvent.EX_DOWNLOAD_PODCAST_NAME_RETRIVAL);
                String url = subscribleEvent.data.getString(SubscribleEvent.EX_DOWNLOAD_PODCAST_URL_RETRIVAL);
                int numberOfRetry = subscribleEvent.data.getInt(SubscribleEvent.EX_DOWNLOAD_PODCAST_COUNT_RETRIVAL);
                Intent msgIntent = new Intent(getApplicationContext(), DownloadService.class);
                msgIntent.putExtra(DOWNLOAD_PODCAST_URL, url);
                msgIntent.putExtra(DOWNLOAD_PODCAST_NAME, name);
                msgIntent.putExtra(DOWNLOAD_PODCAST_RETRY, numberOfRetry++);
                startService(msgIntent);
            }

        }
    }
}
