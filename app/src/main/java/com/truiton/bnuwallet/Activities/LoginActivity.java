package com.truiton.bnuwallet.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.truiton.bnuwallet.Globals.CONFIG;
import com.truiton.bnuwallet.Globals.PrefManager;
import com.truiton.bnuwallet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private PrefManager prefManager;
    SharedPreferences.Editor editor;
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    public String pin;
    ProgressDialog progressDialog;

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            prefManager.setIsNotLoggedIn(false);
            onLoginSuccess(pin);

        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        prefManager = new PrefManager(this);
        mPinLockView = findViewById(R.id.pin_lock_view);
        mIndicatorDots = findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading........");
    }

    public void onLoginSuccess(String REQ_ID) {
        prefManager.setPin(REQ_ID);
        getWallet();
    }

    void getWallet() {
        RequestQueue queue = Volley.newRequestQueue(this);
        progressDialog.show();
        final JSONObject params = new JSONObject();

        try {
            params.put("method", "createMobileWallet");
        } catch (JSONException e) {
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, CONFIG.LOGIN_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();

                        try {
                            String keys = response.getString("response");
                            JSONObject jsnobject = new JSONObject(keys);
                            String address = jsnobject.getString("address");
                            String spendPublicKey = jsnobject.getString("spendPublicKey");
                            String spendSecretKey = jsnobject.getString("spendSecretKey");

                            editor = getSharedPreferences("MY_KEYS", MODE_PRIVATE).edit();
                            editor.putString("address", address);
                            editor.putString("spendPublicKey", spendPublicKey);
                            editor.putString("spendSecretKey", spendSecretKey);
                            editor.apply();


                            SharedPreferences wallpref = getSharedPreferences("MY_KEYS", MODE_PRIVATE);
                            String prefbalance = wallpref.getString("address", null);

                            prefManager.setIsNotLoggedIn(false);

                            finish();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                onNetworkFailed();
            }
        });
        queue.add(req);
    }

    void onNetworkFailed() {
        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setTitle("Error")
                .setIcon(getResources().getDrawable(R.drawable.ic_failed))
                .setMessage("Failed to create account, check your internet connection and try again")
                .addButton("Retry", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getWallet();
                    }
                })
                .addButton("Cancel", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        prefManager.setIsNotLoggedIn(true);
                    }
                });
        builder.show();
    }
}
