package com.example.fleissig.restaurantapp.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.example.fleissig.restaurantapp.MyApplication;
import com.squareup.picasso.Picasso;

/**
 * Created by PhucTV on 7/17/15.
 */
public class PicassoUtils {
    public static void setImage(String picUrl, ImageView view, int defaultIcon) {
        Picasso.with(MyApplication.getInstance()).load(TextUtils.isEmpty(picUrl) ? null : picUrl).placeholder(defaultIcon).error(MyApplication.getInstance().getResources().getDrawable(defaultIcon)).into(view);
    }
}
