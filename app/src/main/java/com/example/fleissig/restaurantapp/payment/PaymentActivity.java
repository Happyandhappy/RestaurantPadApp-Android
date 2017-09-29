package com.example.fleissig.restaurantapp.payment;


/**
 * Created by Fabio on 06/04/2017.
 */

import android.os.Bundle;
import android.app.Activity;
import com.example.fleissig.restaurantapp.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class PaymentActivity extends AppCompatActivity {

    public static final String LAST_UDYNAMO_DATA = "LastUDynamoData";
    String TAG = "Fabio";

    EditText editCCNumber;
    EditText editCCV;
    EditText editNada;
    EditText editNada2;
    Button buttonPay;
    EditText mTextView;
    Button buttonReadCC;

    CieloPaymentService cieloPaymentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);

        cieloPaymentService = new CieloPaymentService();

        editCCNumber   = (EditText)findViewById(R.id.editCCNumber);
        editCCV   = (EditText)findViewById(R.id.editCCV);
        editNada   = (EditText)findViewById(R.id.editNada);
        editNada2   = (EditText)findViewById(R.id.editNada2);
        buttonPay   = (Button)findViewById(R.id.buttonPay);
        mTextView = (EditText)findViewById(R.id.mTextView);
        buttonReadCC = (Button)findViewById(R.id.buttonReadCC);

        try {
            Volley.newRequestQueue(this, new HurlStack(null, new Tls12HttpStack()));
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        final Context context = this;

        final String[] fullString = new String[1];
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardRead = editCCNumber.getText().toString();

//                String ccNumber = editCCNumber.getText().toString();
//                String CCV = editCCV.getText().toString();
//                String ccDate = editCCDate.getText().toString();

                String ccNumber = cardRead.substring(2,18);
                String ccName = cardRead.substring(19, cardRead.indexOf(" ^") - 2);
                String ccDate = cardRead.substring(cardRead.indexOf(" ^") + 2, cardRead.indexOf(" ^") + 6);
                ccDate = ccDate.substring(2) + "/" + "20" + ccDate.substring(0, 2);
                String CCV = editCCV.getText().toString();
                String ccBrand = "Master";
                String amount = "1";

                try {
                    cieloPaymentService.processPayment(context, ccNumber, CCV, ccDate, ccName, ccBrand, Double.parseDouble(amount), new CieloPaymentService.PaymentServiceCompleteListener() {
                        @Override
                        public void responseReceived(int statusCode, String msg) {
                            mTextView.setText(msg);
                        }
                    });
                } catch (AddMobileException e) {
                    Log.e("Fabio", "Error processing payment", e);
                }
            }
        });

//        editCCNumber.setVisibility(View.INVISIBLE);
//        editCCNumber.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                fullString[0] += Character.toChars(keyCode);
//                if (keyCode == Character.valueOf('?')) {
//                }
//                return false;
//            }
//        });

//        editCCV.setVisibility(View.INVISIBLE);
//        editCCV.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == Character.valueOf('?')) {
//
//                }
//                return false;
//            }
//        });

//        editNada.setVisibility(View.INVISIBLE);
//        editNada2.setVisibility(View.INVISIBLE);
//        editCCDate.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == Character.valueOf('?')) {
//
//                }
//                return false;
//            }
//        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}

