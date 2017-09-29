package com.example.fleissig.restaurantapp.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
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
import android.widget.LinearLayout;

import com.example.fleissig.mylibrary.PayingOrder;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ui.activities.MenuListActivity;
import com.example.fleissig.restaurantapp.ui.fragments.DishesFragment;
import com.example.fleissig.restaurantapp.ui.widgets.Toaster;
import com.example.fleissig.restaurantapp.utils.DataUtils;
import com.example.fleissig.restaurantapp.utils.uniMag.ProfileDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.UniMagTools.uniMagReaderToolsMsg;
import IDTech.MSR.uniMag.UniMagTools.uniMagSDKTools;
import IDTech.MSR.uniMag.uniMagReader;
import IDTech.MSR.uniMag.uniMagReaderMsg;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by phuctran on 6/14/17.
 */

public class PaymentGuideDialog extends DialogFragment implements uniMagReaderMsg, uniMagReaderToolsMsg {
    private Unbinder unbinder;
    private uniMagReader myUniMagReader = null;
    private Handler handler = new Handler();
    private uniMagSDKTools firmwareUpdateTool = null;
    private StructConfigParameters profile = null;
    private ProfileDatabase profileDatabase = null;

    @BindView(R.id.ivCardSwipe)
    View ivCardSwipe;
    @BindView(R.id.rl_progress)
    View rlProgress;

    private Runnable doRunAutoConfig = new Runnable() {
        @Override
        public void run() {
            String fileNameWithPath = getConfigurationFileFromRaw();
            if (!isFileExist(fileNameWithPath)) {
                fileNameWithPath = null;
            }
            boolean startAcRet = myUniMagReader.startAutoConfig(fileNameWithPath, true);
            Log.d("PaymentDialog", "startAutoConfig " + startAcRet);
//            ((MenuListActivity) getActivity()).showConfirmPagamento("Master", "Carlos", "2018/07/07");

        }
    };
    private Runnable doConnectUsingProfile = new Runnable() {
        @Override
        public void run() {
            if (myUniMagReader != null) {
                myUniMagReader.connectWithProfile(profile);
            }
        }
    };

    public static PaymentGuideDialog newInstance() {
        PaymentGuideDialog fragment = new PaymentGuideDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        profileDatabase = new ProfileDatabase(getActivity());
        profileDatabase.initializeDB();

        initializeReader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.dialog_payment_guide, container,
                false);

        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    private void initializeReader() {
        if (myUniMagReader != null) {
            myUniMagReader.unregisterListen();
            myUniMagReader.release();
            myUniMagReader = null;
        }
        myUniMagReader = new uniMagReader(PaymentGuideDialog.this, getActivity(), uniMagReader.ReaderType.SHUTTLE);

        myUniMagReader.setVerboseLoggingEnable(true);
        myUniMagReader.registerListen();

        if (profileDatabase.updateProfileFromDB()) {
            this.profile = profileDatabase.getProfile();
            handler.postDelayed(doConnectUsingProfile, 1000);
        } else {
            handler.postDelayed(doRunAutoConfig, 1000);
        }

        firmwareUpdateTool = new uniMagSDKTools(PaymentGuideDialog.this, getActivity());
        firmwareUpdateTool.setUniMagReader(myUniMagReader);
        myUniMagReader.setSDKToolProxy(firmwareUpdateTool.getSDKToolProxy());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        if (myUniMagReader != null) {
            myUniMagReader.stopSwipeCard();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {

        if (myUniMagReader != null) {
            myUniMagReader.release();
        }
        profileDatabase.closeDB();
        super.onDestroy();
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

    @OnClick(R.id.btnSair)
    void onClickSairButton() {
        dismiss();
    }

    final Handler swipeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String text = (String) msg.obj;
            Toaster.showToast(getActivity(), text);
        }
    };

    @Override
    public void onReceiveMsgCommandResult(int arg0, byte[] arg1) {
        Log.d("UniMag", "onReceiveMsgCommandResult");
    }

    @Override
    public void onReceiveMsgConnected() {
        Log.d("UniMag", "onReceiveMsgConnected");
        ivCardSwipe.setVisibility(View.VISIBLE);
        rlProgress.setVisibility(View.INVISIBLE);
        if (!myUniMagReader.isSwipeCardRunning()) {
            myUniMagReader.startSwipeCard();

        }
    }

    @Override
    public void onReceiveMsgDisconnected() {
        Log.d("UniMag", "onReceiveMsgDisconnected");
        if (myUniMagReader.isSwipeCardRunning()) {
            myUniMagReader.stopSwipeCard();
        }
        myUniMagReader.release();

    }

    @Override
    public boolean getUserGrant(int type, String strMessage) {
        Log.d("UniMag", "getUserGrant -- " + strMessage);
        boolean getUserGranted = false;
        switch (type) {
            case uniMagReaderMsg.typeToPowerupUniMag:
                //pop up dialog to get the user grant
                getUserGranted = true;
                break;
            case uniMagReaderMsg.typeToUpdateXML:
                //pop up dialog to get the user grant
                getUserGranted = true;
                break;
            case uniMagReaderMsg.typeToOverwriteXML:
                //pop up dialog to get the user grant
                getUserGranted = true;
                break;
            case uniMagReaderMsg.typeToReportToIdtech:
                //pop up dialog to get the user grant
                getUserGranted = true;
                break;
            default:
                getUserGranted = false;
                break;
        }
        return getUserGranted;
    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int arg0) {
        // TODO Auto-generated method stub
        Log.d("UniMag", "onReceiveMsgAutoConfigProgress");
    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int i, double v, String s) {
        Log.d("UniMag", "onReceiveMsgAutoConfigProgress");
    }

    @Override
    public void onReceiveMsgCardData(byte arg0, byte[] arg1) {
        Log.d("UniMag", "onReceiveMsgCardData");
        Log.d("UniMag", "Successful swipe!");

        String strData = new String(arg1);
        Log.d("UniMag", "SWIPE - " + strData);
        if (myUniMagReader.isSwipeCardRunning()) {
            myUniMagReader.stopSwipeCard();
        }

        // Match the data we want.
        String pattern = "%B(\\d+)\\^([^\\^]+)\\^(\\d{4})";
        Log.d("UniMag", pattern);
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(strData);
        String card = "";
        String name = "";
        String exp = "";
        String data = "";
        if (m.find()) {
            for (int a = 0; a < m.groupCount(); ++a) {
                Log.d("UniMag", a + " - " + m.group(a));
            }
            card = m.group(1);
            name = m.group(2);
            exp = m.group(3);
            data = "Data: " + name + " -- " + card + " -- " + exp;
            Log.d("UniMag", data);

            ((MenuListActivity) getActivity()).showConfirmPagamento(card, name, exp);
            this.dismiss();
//            Message msg = new Message();
//            msg.obj = data;
//            swipeHandler.sendMessage(msg);
        }

    }

    @Override
    public void onReceiveMsgProcessingCardData() {
        Log.d("UniMag", "onReceiveMsgProcessingCardData");
    }

    @Override
    public void onReceiveMsgToCalibrateReader() {
        Log.d("UniMag", "onReceiveMsgToCalibrateReader");
    }

    @Override
    public void onReceiveMsgFailureInfo(int arg0, String arg1) {
        Log.d("UniMag", "onReceiveMsgFailureInfo -- " + arg1);
        showAlert("Error", arg1, new Runnable() {
            @Override
            public void run() {
                initializeReader();
            }
        });
    }

    @Override
    public void onReceiveMsgSDCardDFailed(String arg0) {
        Log.d("UniMag", "onReceiveMsgSDCardDFailed -- " + arg0);
    }

    @Override
    public void onReceiveMsgTimeout(String arg0) {
        Log.d("UniMag", "onReceiveMsgTimeout -- " + arg0);
        showAlert("Error", arg0, new Runnable() {
            @Override
            public void run() {
                initializeReader();
            }
        });
    }

    @Override
    public void onReceiveMsgToConnect() {
        Log.d("UniMag", "Swiper Powered Up");
    }

    @Override
    public void onReceiveMsgToSwipeCard() {
        Log.d("UniMag", "onReceiveMsgToSwipeCard");
        Toaster.showToast(getActivity(), "Pronto para passar o cartão");
    }

    @Override
    public void onReceiveMsgAutoConfigCompleted(StructConfigParameters profile) {
        Log.d("UniMag", "onReceiveMsgAutoConfigCompleted");
        this.profile = profile;
        profileDatabase.setProfile(profile);
        profileDatabase.insertResultIntoDB();
        handler.postDelayed(doConnectUsingProfile, 1000);
    }

    private String getConfigurationFileFromRaw() {
        return getXMLFileFromRaw("idt_unimagcfg_default.xml");
    }

    private String getXMLFileFromRaw(String fileName) {
        //the target filename in the application path
        String fileNameWithPath = null;
        fileNameWithPath = fileName;

        try {
            InputStream in = getResources().openRawResource(R.raw.idt_unimagcfg_default);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            in.close();
            getActivity().deleteFile(fileNameWithPath);
            FileOutputStream fout = getActivity().openFileOutput(fileNameWithPath, MODE_PRIVATE);
            fout.write(buffer);
            fout.close();

            // to refer to the application path
            File fileDir = getActivity().getFilesDir();
            fileNameWithPath = fileDir.getParent() + java.io.File.separator + fileDir.getName();
            fileNameWithPath += java.io.File.separator + "idt_unimagcfg_default.xml";

        } catch (Exception e) {
            e.printStackTrace();
            fileNameWithPath = null;
        }
        return fileNameWithPath;
    }

    private boolean isFileExist(String path) {
        if (path == null)
            return false;
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    @Override
    public void onReceiveMsgUpdateFirmwareProgress(int progressValue) {
        Log.d("UNIMAG", "onReceiveMsgUpdateFirmwareProgress");
    }

    @Override
    public void onReceiveMsgUpdateFirmwareResult(int progressValue) {
        Log.d("UNIMAG", "onReceiveMsgUpdateFirmwareResult");
    }

    @Override
    public void onReceiveMsgChallengeResult(int i, byte[] bytes) {
        Log.d("UNIMAG", "onReceiveMsgChallengeResult");
    }

    private void showAlert(String title, String message, final Runnable runnable) {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle(title)
                .setMessage("Tempo Expirado. Tente novamente.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(runnable!=null){
                            runnable.run();
                        }
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
