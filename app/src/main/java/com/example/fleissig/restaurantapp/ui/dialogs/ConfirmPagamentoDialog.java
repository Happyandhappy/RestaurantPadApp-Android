package com.example.fleissig.restaurantapp.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.example.fleissig.mylibrary.PayingOrder;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.payment.AddMobileException;
import com.example.fleissig.restaurantapp.payment.CieloPaymentService;
import com.example.fleissig.restaurantapp.payment.Tls12HttpStack;
import com.example.fleissig.restaurantapp.ui.activities.MenuListActivity;
import com.example.fleissig.restaurantapp.ui.fragments.DishesFragment;
import com.example.fleissig.restaurantapp.ui.models.CieloError;
import com.example.fleissig.restaurantapp.ui.widgets.Toaster;
import com.example.fleissig.restaurantapp.utils.CardUtils;
import com.example.fleissig.restaurantapp.utils.DataUtils;
import com.example.fleissig.restaurantapp.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by phuctran on 6/14/17.
 */

public class ConfirmPagamentoDialog extends DialogFragment {
    private static final String TAG = ConfirmPagamentoDialog.class.getSimpleName();

    private static final String ARGS_CARD = "ARGS_CARD";
    private static final String ARGS_NAME = "ARGS_NAME";
    private static final String ARGS_EXP = "ARGS_EXP";

    private Unbinder unbinder;
    private CieloPaymentService cieloPaymentService;
    @BindView(R.id.etTotalMinhaConta)
    TextView etTotalMinhaConta;
    @BindView(R.id.etTotalPagar)
    EditText etTotalPagar;
    @BindView(R.id.etCVV)
    EditText etCVV;
    @BindView(R.id.etCPF)
    EditText etCPF;
    private String mCard;
    private String mName;
    private String mExp;
    private static DishesFragment dishesFragment;

    public static ConfirmPagamentoDialog newInstance(String card, String name, String exp, DishesFragment dishes) {
        ConfirmPagamentoDialog fragment = new ConfirmPagamentoDialog();
        dishesFragment = dishes;
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_CARD, card);
        bundle.putString(ARGS_NAME, name);
        bundle.putString(ARGS_EXP, exp);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cieloPaymentService = new CieloPaymentService();
        try {
            Volley.newRequestQueue(getActivity(), new HurlStack(null, new Tls12HttpStack()));
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (savedInstanceState == null && getArguments() != null) {
            mCard = getArguments().getString(ARGS_CARD);
            mName = getArguments().getString(ARGS_NAME);
            mExp = getArguments().getString(ARGS_EXP);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.dialog_confirm_pagamento, container,
                false);
        unbinder = ButterKnife.bind(this, v);
        init(v);
        return v;
    }

    private void init(View view) {
        double total = DataUtils.countMinhaConta() * 1.1;
        double minhaContraValue = DataUtils.countLeft();

        etTotalMinhaConta.setText(String.format("%.2f", total));
        etTotalPagar.setText(String.format("%.2f", minhaContraValue));

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LinearLayout root = new LinearLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnConfirmar)
    void onClickConfirmar() {
        try {
            cieloPaymentService.processPayment(getActivity(), mCard, etCVV.getText().toString(), StringUtils.formatDateForPayment(mExp), StringUtils.standarizeCardNameForPayment(mName), CardUtils.getBrandFromCardNumber(mCard), Double.parseDouble(etTotalMinhaConta.getText().toString().replace(",", ".")), new CieloPaymentService.PaymentServiceCompleteListener() {
                @Override
                public void responseReceived(int statusCode, String msg) {
                    //Toaster.showToast(getActivity(), msg);
                    final AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    Log.d(TAG, msg);
                    try {
                        JSONObject responseReader = new JSONObject(msg);
                        JSONObject paymentReader = responseReader.getJSONObject("Payment");
                        String returnCode = paymentReader.getString("ReturnCode");
                        if ("4".equals(returnCode)) {
                            //Success we must think what else we need to store here about the return
                            MyApplication.getInstance().CART.addOrderedPayment(new PayingOrder(Double.parseDouble(etTotalMinhaConta.getText().toString().replace(",", ".")), System.currentTimeMillis()));
                            FragmentManager fm = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            DishesFragment parent = (DishesFragment) fm.findFragmentByTag(MenuListActivity.DISHES_FRAGMENT);
                            parent.notifyPaySuccessfully();
                            parent.notifyLoadDataFromFirebaseChanged();
                            builder.setTitle("OK")
                                    .setMessage("O pagamento foi aprovado")
                                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            getDialog().dismiss();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        } else {
                            builder.setTitle("Error")
                                    .setMessage("O pagamento não foi aprovado")
                                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }catch(Exception e){
                        builder.setTitle("Erro")
                                .setMessage("O cartão não foi processado")
                                .setPositiveButton(R.string.general_error, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        getDialog().dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                        Log.e("CieloResponse", "Error processing payment", e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Fabio", "Error processing payment", e);
        }
    }

    @OnClick(R.id.btnSair)
    void onClickSairButton() {
        dismiss();
    }
}
