package com.example.fleissig.restaurantapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;


import com.example.fleissig.restaurantapp.utils.bus.BusCenter;
import com.example.fleissig.restaurantapp.utils.bus.MainThreadBus;
import com.example.fleissig.restaurantapp.utils.bus.SubscribleEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by phuctran on 2/15/17.
 */

public class DownloadService extends IntentService {

    private static String TAG = DownloadService.class.getSimpleName();

    public static final String DOWNLOAD_STATUS_FAILURE = "FAIL";
    public static final String DOWNLOAD_STATUS_SUCCESS = "SUCCESS";

    public static final String DOWNLOAD_PODCAST_URL = "DOWNLOAD_PODCAST_URL";
    public static final String DOWNLOAD_PODCAST_NAME = "DOWNLOAD_PODCAST_NAME";
    public static final String DOWNLOAD_PODCAST_RETRY = "DOWNLOAD_PODCAST_RETRY_COUNT";

    public static final String FOLDER_DOWNLOAD = "restaurant";

    public DownloadService() {
        super(DownloadService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        final String url = intent.getStringExtra(DOWNLOAD_PODCAST_URL);
        final String filename = intent.getStringExtra(DOWNLOAD_PODCAST_NAME);
        final int numberRetry = intent.getIntExtra(DOWNLOAD_PODCAST_RETRY,0);
        new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER_DOWNLOAD).mkdirs();

        final File downloadFile = new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER_DOWNLOAD + File.separator + filename);
        if (!downloadFile.exists()) {
            try {
                downloadFile.createNewFile();
                StorageReference mStorageReference = FirebaseStorage.getInstance().getReference(url);
                mStorageReference.getFile(downloadFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bundle successBundle = new Bundle();
                        successBundle.putString(SubscribleEvent.EX_DOWNLOAD_STATUS, DOWNLOAD_STATUS_SUCCESS);
                        successBundle.putString(SubscribleEvent.EX_DOWNLOAD_PODCAST_ID, filename);
                        successBundle.putString(SubscribleEvent.EX_DOWNLOAD_PODCAST_LOCAL_URL, downloadFile.getPath());
                        SubscribleEvent subscribleEvent = new SubscribleEvent(SubscribleEvent.EVENT_DOWNLOAD_SERVICE, successBundle);
                        BusCenter.getInstance().post(subscribleEvent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        downloadFile.delete();
                        Bundle successBundle = new Bundle();
                        successBundle.putString(SubscribleEvent.EX_DOWNLOAD_STATUS, DOWNLOAD_STATUS_FAILURE);
                        successBundle.putString(SubscribleEvent.EX_DOWNLOAD_PODCAST_ID, filename);
                        successBundle.putString(SubscribleEvent.EX_DOWNLOAD_PODCAST_URL_RETRIVAL, url);
                        successBundle.putString(SubscribleEvent.EX_DOWNLOAD_PODCAST_NAME_RETRIVAL, filename);
                        successBundle.putInt(SubscribleEvent.EX_DOWNLOAD_PODCAST_COUNT_RETRIVAL, numberRetry);
                        successBundle.putString(SubscribleEvent.EX_DOWNLOAD_PODCAST_LOCAL_URL, downloadFile.getPath());
                        SubscribleEvent subscribleEvent = new SubscribleEvent(SubscribleEvent.EVENT_DOWNLOAD_SERVICE, successBundle);
                        BusCenter.getInstance().post(subscribleEvent);
                    }
                });
            } catch (Exception e) {
            }
        } else {
            Bundle successBundle = new Bundle();
            successBundle.putString(SubscribleEvent.EX_DOWNLOAD_STATUS, DOWNLOAD_STATUS_SUCCESS);
            successBundle.putString(SubscribleEvent.EX_DOWNLOAD_PODCAST_ID, filename);
            successBundle.putString(SubscribleEvent.EX_DOWNLOAD_PODCAST_LOCAL_URL, downloadFile.getPath());
            SubscribleEvent subscribleEvent = new SubscribleEvent(SubscribleEvent.EVENT_DOWNLOAD_SERVICE, successBundle);
            BusCenter.getInstance().post(subscribleEvent);
        }
    }
}
