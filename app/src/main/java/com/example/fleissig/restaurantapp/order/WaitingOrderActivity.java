package com.example.fleissig.restaurantapp.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.MyFirebaseMessagingService;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.category.CategoriesActivity;
import com.example.fleissig.restaurantapp.payment.PaymentActivity;

public class WaitingOrderActivity extends AppCompatActivity {
    public static final String MESSAGE = "message";
    private static final String TAG = WaitingOrderActivity.class.getSimpleName();
    private BroadcastReceiver mReceiver;
    private TextView mResultTextView;
    private Button paymentBtn;
    private Button anotherOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waiting_order);

        mResultTextView = (TextView) findViewById(R.id.result_textview);
        paymentBtn = (Button) findViewById(R.id.payment);
        anotherOrderBtn = (Button) findViewById(R.id.anotherOrder);

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });

        anotherOrderBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
                startActivity(intent);
            }
        });

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        setMessage(getIntent());

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setMessage(intent);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(MyFirebaseMessagingService.RESULT)
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setMessage(intent);
    }

    private void setMessage(Intent intent) {
        String s = intent.getStringExtra(MESSAGE);
        if(s != null) mResultTextView.setText(s);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}
