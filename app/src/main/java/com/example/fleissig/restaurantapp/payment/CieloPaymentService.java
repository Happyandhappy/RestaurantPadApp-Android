package com.example.fleissig.restaurantapp.payment;

/**
 * Created by Fabio on 06/04/2017.
 */

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Fabio on 12/03/2017.
 */



public class CieloPaymentService {

    // Test Keys for Sandbox
    public static final String MERCHANT_ID = "5a9f3c0f-e3d2-4905-8cb7-6407847f2bce";
    public static final String MERCHANT_KEY = "JCSSNPNRTFGVLMVDWLHSSCVPAUYOROAIPHRTAHXT";

    public static final String MERCHANT_ORDER_ID = "MerchantOrderId";
    public static final String CUSTOMER = "Customer";
    public static final String TYPE = "Type";
    public static final String AMOUNT = "Amount";
    public static final String PROVIDER = "Provider";
    public static final String INSTALLMENTS = "Installments";
    public static final String SOFT_DESCRIPTOR = "SoftDescriptor";
    public static final String PAYMENT = "Payment";
    public static final String CREDIT_CARD = "CreditCard";
    public static final String CARD_NUMBER = "CardNumber";
    public static final String HOLDER = "Holder";
    public static final String EXPIRATION_DATE = "ExpirationDate";
    public static final String SECURITY_CODE = "SecurityCode";
    public static final String BRAND = "Brand";
    public static final String CUSTOMER_NAME = "Name";
    private static String REQUEST_OF_TRANSACTION_PRODUCTION = "https://api.cieloecommerce.cielo.com.br/";
    private static String QUERY_OF_TRANSACTION_PRODUCTION = "https://apiquery.cieloecommerce.cielo.com.br/";

    private static String REQUEST_OF_TRANSACTION_SANDBOX = "https://apisandbox.cieloecommerce.cielo.com.br";
    private static String QUERY_OF_TRANSACTION_SANDBOX = "https://apiquerysandbox.cieloecommerce.cielo.com.br";

    private static String SALES_POST = "/1/sales/";

    private boolean isSandboxMode = true;

    private String getRequestOfTransaction() {
        if (!isSandboxMode) {
            return REQUEST_OF_TRANSACTION_PRODUCTION;
        } else {
            return REQUEST_OF_TRANSACTION_SANDBOX;
        }
    }

    private String getQueryOfTransaction() {
        if (!isSandboxMode) {
            return QUERY_OF_TRANSACTION_PRODUCTION;
        } else {
            return QUERY_OF_TRANSACTION_SANDBOX;
        }
    }

    public void processPayment(Context context, String ccNumber, String ccv, String ccDate, String ccHolder, String ccBrand, Double amount,
                               final PaymentServiceCompleteListener paymentServiceCompleteListener) throws AddMobileException {
        JSONObject json = new JSONObject();
        try {
            JSONObject customerJson = new JSONObject();
            customerJson.put(CUSTOMER_NAME, "Consumidor");
            json.put(CUSTOMER, customerJson);
            json.put(MERCHANT_ORDER_ID, "021");
            JSONObject paymentJson = new JSONObject();
            paymentJson.put(SOFT_DESCRIPTOR, "123456789ABCD");
            paymentJson.put(AMOUNT, amount);
            paymentJson.put(INSTALLMENTS, "1");
            JSONObject creditCardJson = new JSONObject();
            creditCardJson.put(CARD_NUMBER, ccNumber);
            creditCardJson.put(BRAND, ccBrand);
            creditCardJson.put(SECURITY_CODE, ccv);
            creditCardJson.put(HOLDER, ccHolder);
            creditCardJson.put(EXPIRATION_DATE, ccDate);
            paymentJson.put(CREDIT_CARD, creditCardJson);
            paymentJson.put(TYPE, "CreditCard");
            paymentJson.put(PROVIDER, "Simulado");
            json.put(PAYMENT, paymentJson);
        } catch (JSONException e) {
            throw new AddMobileException(e);
        }

        RequestQueue queue = null;
        try {
            queue = Volley.newRequestQueue(context, new HurlStack(null, new Tls12HttpStack()));
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new AddMobileException(e);
        }

        String url = getRequestOfTransaction() + SALES_POST;

        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the first 500 characters of the response string.
                //returnString[0] = "Response is: "+ response.toString();
                paymentServiceCompleteListener.responseReceived(200,response.toString());
                Log.i("Fabio", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = error.getMessage();
                msg = msg == null ? "HTTP " + new Integer(((VolleyError)error).networkResponse.statusCode).toString() : msg;
                Log.i("Fabio", msg);
                if(error.networkResponse!=null) {
                    try {
                        String body = new String(error.networkResponse.data,"UTF-8");
                        paymentServiceCompleteListener.responseReceived(error.networkResponse.statusCode,body);
                        Log.i("Fabio", body);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("MerchantId", MERCHANT_ID);
                params.put("MerchantKey", MERCHANT_KEY);
                //params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Add the request to the RequestQueue.

        Log.i("Fabio", json.toString());
        Log.i("Fabio", url);

        queue.add(stringRequest);
    }

    public interface PaymentServiceCompleteListener {
        void responseReceived(int statusCode, String msg);
    }
}

