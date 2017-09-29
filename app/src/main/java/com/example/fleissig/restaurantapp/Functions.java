package com.example.fleissig.restaurantapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class Functions {
    public static final String TOKEN_ID = "TOKEN_ID_KEY";
    private static final String PREFERENCE_FILE_KEY = "com.example.fleissig.restaurantapp,PREFERENCE_FILE_KEY";
    private static final String TAG = Functions.class.getSimpleName();

    public static void addEmptyHeader(Activity activity, ListView listView) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View header = inflater.inflate(R.layout.header, listView, false);
        listView.addHeaderView(header, null, false);
    }

    public static void writeTokenId(Context context, String tokenId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TOKEN_ID, tokenId);
        editor.apply();
    }

    public static
    @Nullable
    String readReadTokenId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY,
                Context.MODE_PRIVATE);
        String string = sharedPref.getString(TOKEN_ID, null);
        return string;
    }

    public static
    @NonNull
    int readTableNumber(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String string = sharedPref.getString(
                context.getString(R.string.table_number_key), "1");
        return Integer.valueOf(string);
    }

    public static
    @NonNull
    boolean readPossibilityChangeCartItem(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean b = sharedPref.getBoolean(context
                .getString(R.string.possibility_change_cart_switch_key), true);
        return b;
    }

}
