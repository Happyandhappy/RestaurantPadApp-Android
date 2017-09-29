package com.example.fleissig.restaurantapp.utils.bus;

import android.os.Bundle;

/**
 * Created by PhucTV on 6/2/15.
 */
public class SubscribleEvent {
    //EVENT
    public static final String EVENT_DOWNLOAD_SERVICE = "EVENT_DOWNLOAD_SERVICE";
    //Data Name
    public static final String EX_DOWNLOAD_STATUS = "mDownloadStatus";
    public static final String EX_DOWNLOAD_PODCAST_ID = "mDownloadPodcastId";
    public static final String EX_DOWNLOAD_PODCAST_LOCAL_URL = "mDownloadPodcastLocalUrl";
    public static final String EX_DOWNLOAD_PODCAST_URL_RETRIVAL = "mPodcastUrlRetrival";
    public static final String EX_DOWNLOAD_PODCAST_NAME_RETRIVAL = "mPodcastNameRetrival";
    public static final String EX_DOWNLOAD_PODCAST_COUNT_RETRIVAL = "mPodcastCountRetrival";


    public String event;
    public Bundle data;

    public SubscribleEvent(String event, Bundle data) {
        this.event = event;
        this.data = data;
    }

    @Override
    public String toString() {
        return "SubscribleEvent{" +
                "event='" + event + '\'' +
                ", data=" + data +
                '}';
    }
}
