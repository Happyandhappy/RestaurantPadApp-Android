package com.example.fleissig.restaurantapp.ui.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by PhucTV on 5/27/15.
 */
public class Toaster {
    private static String TAG = Toaster.class.getName();
    static Duration mDefaultDuration = Duration.LONG;
    static Handler mHandler = new Handler(Looper.getMainLooper());
    static Toast mToast;

    public static void setDefaultDuration(Duration duration) {
        mDefaultDuration = duration;
    }

    public static void showToast(Context context, int i) {
        showToast(context, i, mDefaultDuration);
    }

    public static void showToast(Context context, int i, Duration duration) {
        showToast(context, i, duration, 0, 0, 0);
    }

    public static void showToast(Context context, int i, Duration duration, int gravity, int xOffset, int yOffset) {
        if (context == null) {
            Log.e(TAG, "showToast - Context was null");
        } else {
            showToast(context, context.getResources().getString(i), duration, gravity, xOffset, yOffset);
        }
    }

    public static void showToast(Context context, String message) {
        showToast(context, message, mDefaultDuration);
    }

    public static void showToast(Context context, String message, int gravity, int xOffset, int yOffset) {
        showToast(context, message, mDefaultDuration, gravity, xOffset, yOffset);
    }

    public static void showToast(Context context, String message, Duration duration) {
        showToast(context, message, duration, 0, 0, 0);
    }

    public static void showToast(final Context context, final String text, final Duration duration, final int gravity, final int xOffset, final int yOffset) {
        if (context == null) {
            Log.e(TAG, "showToast - Context was null");
            return;
        }
        if (text == null) {
            Log.e(TAG, "showToast - text was null");
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (Toaster.mToast != null) {
                        Toaster.mToast.cancel();
                    }
                    int toastDuration;
                    if (duration == Duration.LONG) {
                        toastDuration = Toast.LENGTH_LONG;
                    } else {
                        toastDuration = Toast.LENGTH_SHORT;
                    }
                    Toaster.mToast = Toast.makeText(context, text, toastDuration);
                    if (gravity != 0) {
                        Toaster.mToast.setGravity(gravity, xOffset, yOffset);
                    }
                    Toaster.mToast.show();
                }
            });
        }
    }

    public enum Duration {
        SHORT, LONG
    }
}
