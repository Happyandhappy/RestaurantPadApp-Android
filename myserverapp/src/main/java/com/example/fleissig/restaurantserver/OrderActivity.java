package com.example.fleissig.restaurantserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.firebase.ui.database.FirebaseIndexListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = OrderActivity.class.getSimpleName();
    private FirebaseIndexListAdapter<Dish> mAdapter;
    private DatabaseReference mRef;
    private ArrayList<Integer> quantities = new ArrayList<>();
    private DatabaseReference mOrderRef;
    private String mOrderKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mRef = FirebaseDatabase.getInstance().getReference();

        if (savedInstanceState != null) {
            mOrderKey = savedInstanceState.getString(OrdersFragment.ORDER_KEY_EXTRA);
        }

        Intent intent = getIntent();
        if (intent != null) {
            mOrderKey = intent.getStringExtra(OrdersFragment.ORDER_KEY_EXTRA);
        }

        mOrderRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.ORDERS_KEY)
                .child(ShareFunctions.readRestaurantId(this))
                .child(mOrderKey);

        mOrderRef.child(FirebaseConstants.DISHES_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    quantities.add(snapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled(): " + databaseError.getMessage());
            }
        });

        ListView orderList = (ListView) findViewById(R.id.order_list);

        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MyRetrofitRequests.ResponseData message = MyRetrofitRequests
                                    .sendNotification(mOrderRef.getKey(),
                                            getString(R.string.app_name),
                                    getString(R.string.order_ready_title),
                                            getString(R.string.order_ready_title));
                            Log.d(TAG, String.valueOf(message.getMessage_id()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                mOrderRef.removeValue();
                finish();
            }
        });

        mAdapter = new FirebaseIndexListAdapter<Dish>(this, Dish.class,
                android.R.layout.two_line_list_item,
                mOrderRef.child(FirebaseConstants.DISHES_KEY),
                mRef.child(FirebaseConstants.DISHES_KEY)) {
            @Override
            protected void populateView(View v, Dish model, final int position) {

                ((TextView) v.findViewById(android.R.id.text1)).setText(model.text);
                ((TextView) v.findViewById(android.R.id.text2))
                        .setText(String.valueOf(quantities.get(position)));

            }
        };

        orderList.setAdapter(mAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(OrdersFragment.ORDER_KEY_EXTRA, mOrderKey);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
