package com.example.fleissig.restaurantapp.category;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.Rating;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.AbstractDishActivity;
import com.example.fleissig.restaurantapp.BaseActivity;
import com.example.fleissig.restaurantapp.Functions;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ReviewActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DishActivity extends AbstractDishActivity {

}
