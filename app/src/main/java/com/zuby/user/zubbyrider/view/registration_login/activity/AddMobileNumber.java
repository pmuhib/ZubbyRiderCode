package com.zuby.user.zubbyrider.view.registration_login.activity;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.databinding.ActivityAddMobileNumberBinding;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.interfaces.ResultInterface1;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.BaseActivity;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.utils.Utility;
import com.zuby.user.zubbyrider.view.navigationdrawer.activity.MainActivity;
import com.zuby.user.zubbyrider.view.registration_login.model.HasPasswordModel;
import com.zuby.user.zubbyrider.view.registration_login.model.MobileNumberRegistrationModel;
import com.zuby.user.zubbyrider.view.registration_login.model.OtpVarifyModel;
import com.zuby.user.zubbyrider.view.registration_login.model.SendOtpModel;
import com.zuby.user.zubbyrider.view.registration_login.presenter.HasPasswordPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.MobileNumberRegistrationPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.OtpVarifyPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.SendOtpPresenter;
import com.zuby.user.zubbyrider.view.tokengenerate.GenerateTokenClass;

import java.util.ArrayList;
import java.util.List;

public class AddMobileNumber extends BaseActivity implements ResultInterface, View.OnFocusChangeListener, View.OnKeyListener, TextWatcher, View.OnClickListener {
    private ActivityAddMobileNumberBinding mActivityAddMobileNumberBinding;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    int flag = 0;
    private boolean isOtp = false;
    private String mCountryCode, mMobileNumber, mType;
    private Boolean mCheck = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAddMobileNumberBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_mobile_number);
        setSupportActionBar(mActivityAddMobileNumberBinding.toolbar);
        mActivityAddMobileNumberBinding.fab.setOnClickListener(this);
        mActivityAddMobileNumberBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setPINListeners();
        mActivityAddMobileNumberBinding.contentaddmobilenumber.resend.setOnClickListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.trouble.setVisibility(View.GONE);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.continue1.setOnClickListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.nextstep.setOnClickListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.socialconnect.setOnClickListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    hideSoftKeyboard(mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onSuccess(Object object) {
        mActivityAddMobileNumberBinding.contentaddmobilenumber.progressBar.setVisibility(View.GONE);
        if (object instanceof HasPasswordModel) {
            HasPasswordModel mobileNumberRegistrationModel = (HasPasswordModel) object;
            if (mobileNumberRegistrationModel.getMessage().equalsIgnoreCase("yes")) {
                PreferenceConnector.writeString(context, ApiKeys.HASPASS, "yes");
                Intent intent = new Intent(context, OtpVarifyActivity.class);
                intent.putExtra("type", "password");
                intent.putExtra("countrycode", mActivityAddMobileNumberBinding.contentaddmobilenumber.ccp1.getSelectedCountryCode());
                intent.putExtra("mobilenumber", mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString());
                startActivity(intent);
            }
        } else if (object instanceof SendOtpModel) {
            isOtp = true;
            // mActivityAddMobileNumberBinding.fab.setVisibility(View.VISIBLE);
            SendOtpModel sendOtpModel = (SendOtpModel) object;
            Toast.makeText(context, sendOtpModel.getMessage(), Toast.LENGTH_LONG).show();
            if (checkAndRequestPermissions()) {
                // carry on the normal flow, as the case of  permissions  granted.
            }
            mActivityAddMobileNumberBinding.contentaddmobilenumber.addnumber.setVisibility(View.GONE);
            mActivityAddMobileNumberBinding.contentaddmobilenumber.addOtp.setVisibility(View.VISIBLE);
            mActivityAddMobileNumberBinding.contentaddmobilenumber.timer.setVisibility(View.VISIBLE);
            mActivityAddMobileNumberBinding.contentaddmobilenumber.resend.setClickable(false);
            mActivityAddMobileNumberBinding.contentaddmobilenumber.resend.setText(getResources().getString(R.string.resendcode));
            new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    mActivityAddMobileNumberBinding.contentaddmobilenumber.timer.setText("" + millisUntilFinished / 1000);
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    mActivityAddMobileNumberBinding.contentaddmobilenumber.timer.setVisibility(View.GONE);
                    mActivityAddMobileNumberBinding.contentaddmobilenumber.resend.setText("Resend Code");
                    mActivityAddMobileNumberBinding.contentaddmobilenumber.resend.setClickable(true);
                }

            }.start();
        } else if (object instanceof OtpVarifyModel) {
            OtpVarifyModel otpVarifyModel = (OtpVarifyModel) object;
            Toast.makeText(context, otpVarifyModel.getMessage(), Toast.LENGTH_LONG).show();
            level1Registration();
        }
    }

    @Override
    public void onFailed(String string) {
        mActivityAddMobileNumberBinding.contentaddmobilenumber.progressBar.setVisibility(View.GONE);
        if (string.equalsIgnoreCase("token Expired")) {
            new GenerateTokenClass(context);
            Toast.makeText(context, string + " please try again", Toast.LENGTH_LONG).show();
        } else if (string.equalsIgnoreCase("no")) {
            PreferenceConnector.writeString(context, ApiKeys.HASPASS, "no");
            sendOtp();
        } else if (string.equalsIgnoreCase("authentication failed")) {
            new GenerateTokenClass(context);
            Toast.makeText(context, string + " please try again", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show();
        }
    }

    public void addData() {
        mActivityAddMobileNumberBinding.contentaddmobilenumber.progressBar.setVisibility(View.VISIBLE);
        new HasPasswordPresenter().show(this, context, mApiService,
                mActivityAddMobileNumberBinding.contentaddmobilenumber.ccp1.getSelectedCountryCode().toString().trim(),
                mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString().trim());
    }

    public void sendOtp() {
        mActivityAddMobileNumberBinding.contentaddmobilenumber.progressBar.setVisibility(View.VISIBLE);
        new SendOtpPresenter().show(this, context, mApiService,
                mActivityAddMobileNumberBinding.contentaddmobilenumber.ccp1.getSelectedCountryCode().toString().trim(),
                mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString().trim());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * Hides soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext);
                    showSoftKeyboard(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    setFocus(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext);
                    showSoftKeyboard(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    setFocus(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext);
                    showSoftKeyboard(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext);
                    showSoftKeyboard(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.getText().length() == 4)
                            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext.setText("");
                        else if (mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.getText().length() == 3)
                            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext.setText("");
                        else if (mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.getText().length() == 2)
                            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinSecondEdittext.setText("");
                        else if (mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.getText().length() == 1)
                            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinFirstEdittext.setText("");

                        if (mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.length() > 0)
                            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.setText
                                    (mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.getText().subSequence
                                            (0, mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setDefaultPinBackground(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinFirstEdittext);
        setDefaultPinBackground(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinSecondEdittext);
        setDefaultPinBackground(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext);
        setDefaultPinBackground(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext);

        if (s.length() == 0) {
            setFocusedPinBackground(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinFirstEdittext);
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinFirstEdittext.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinSecondEdittext);
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinFirstEdittext.setText(s.charAt(0) + "");
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinSecondEdittext.setText("");
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext.setText("");
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext);
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinSecondEdittext.setText(s.charAt(1) + "");
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext.setText("");
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext);
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext.setText(s.charAt(2) + "");
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext.setText("");
        } else if (s.length() == 4) {
            mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext.setText(s.charAt(3) + "");
            hideSoftKeyboard(mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext);
            Log.e("working", "working");
        }
    }

    /**
     * Sets default PIN background.
     *
     * @param editText edit text to change
     */
    private void setDefaultPinBackground(EditText editText) {
        // setViewBackground(editText, getResources().getDrawable(R.drawable.com_facebook_button_background));
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets focused PIN field background.
     *
     * @param editText edit text to change
     */
    private void setFocusedPinBackground(EditText editText) {
        //setViewBackground(editText, getResources().getDrawable(R.drawable.com_facebook_button_background));
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.addTextChangedListener(this);

        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinFirstEdittext.setOnFocusChangeListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinSecondEdittext.setOnFocusChangeListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext.setOnFocusChangeListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext.setOnFocusChangeListener(this);

        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinFirstEdittext.setOnKeyListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinSecondEdittext.setOnKeyListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext.setOnKeyListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext.setOnKeyListener(this);
        mActivityAddMobileNumberBinding.contentaddmobilenumber.pinHiddenEdittext.setOnKeyListener(this);
    }

    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                Log.e("msg", message);
                char[] sms = message.toCharArray();
                mActivityAddMobileNumberBinding.contentaddmobilenumber.pinFirstEdittext.setText("" + sms[0]);
                mActivityAddMobileNumberBinding.contentaddmobilenumber.pinSecondEdittext.setText("" + sms[1]);
                mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext.setText("" + sms[2]);
                mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext.setText("" + sms[3]);
            }
        }
    };

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.continue1:
                mActivityAddMobileNumberBinding.fab.performClick();
                break;
            case R.id.resend:
                sendOtp();
                break;
            case R.id.nextstep:
                if (isOtp) {
                    varifyOtp();
                } else {

                }
                break;
            case R.id.socialconnect:
                Intent intent1 = new Intent(AddMobileNumber.this, ConnectSocial.class);
                startActivity(intent1);
                break;
            case R.id.fab:
                if (mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString().trim().equals("")
                        || !Utility.checkMobile(mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().
                        toString().trim())) {
                    mActivityAddMobileNumberBinding.contentaddmobilenumber.errorcode.setVisibility(View.VISIBLE);
                    mActivityAddMobileNumberBinding.contentaddmobilenumber.errorcode.setText(getResources().getString(R.string.warning_add_phone));
                } else {
                    mActivityAddMobileNumberBinding.contentaddmobilenumber.errorcode.setVisibility(View.GONE);
                    addData();
                }
                break;
        }
    }

    public void varifyOtp() {
        String otp = mActivityAddMobileNumberBinding.contentaddmobilenumber.pinFirstEdittext.getText().toString().trim() +
                mActivityAddMobileNumberBinding.contentaddmobilenumber.pinSecondEdittext.getText().toString().trim() +
                mActivityAddMobileNumberBinding.contentaddmobilenumber.pinThirdEdittext.getText().toString().trim() +
                mActivityAddMobileNumberBinding.contentaddmobilenumber.pinForthEdittext.getText().toString().trim();
        mActivityAddMobileNumberBinding.contentaddmobilenumber.progressBar.setVisibility(View.VISIBLE);
        new OtpVarifyPresenter().show(this, context, mApiService,
                mActivityAddMobileNumberBinding.contentaddmobilenumber.ccp1.getSelectedCountryCode().toString().trim(),
                mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString().trim(), otp);
    }

    public void level1Registration() {
        mActivityAddMobileNumberBinding.contentaddmobilenumber.progressBar.setVisibility(View.VISIBLE);
        new MobileNumberRegistrationPresenter().show(new ResultInterface1() {
                                                         @Override
                                                         public void onSuccess(Object object) {
                                                             mActivityAddMobileNumberBinding.contentaddmobilenumber.progressBar.setVisibility(View.GONE);
                                                             if (object instanceof MobileNumberRegistrationModel) {
                                                                 MobileNumberRegistrationModel le = (MobileNumberRegistrationModel) object;
                                                                 if (le.getData().getStatus().equalsIgnoreCase("inactive")) {
                                                                     Intent intent = new Intent(context, OtpVarifyActivity.class);
                                                                     intent.putExtra("type", "registration");
                                                                     intent.putExtra("countrycode", mActivityAddMobileNumberBinding.contentaddmobilenumber.ccp1.getSelectedCountryCode());
                                                                     intent.putExtra("mobilenumber", mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString());
                                                                     startActivity(intent);
                                                                 } else {
                                                                     Toast.makeText(context, "You logged in successfully", Toast.LENGTH_LONG).show();
                                                                     Utility.login(this, context, mActivityAddMobileNumberBinding.contentaddmobilenumber.ccp1.getSelectedCountryCode(),
                                                                             mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString(), "");
                                                                     PreferenceConnector.writeString(context, ApiKeys.MOBILE, le.getData().getMobile_no());
                                                                     PreferenceConnector.writeString(context, ApiKeys.COUNTRY_CODE, le.getData().getCountry_code());
                                                                     PreferenceConnector.writeString(context, ApiKeys.STATUS, le.getData().getStatus());
                                                                     PreferenceConnector.writeString(context, ApiKeys.UID, le.getData().getUser_id());
                                                                     Intent intent = new Intent(context, MainActivity.class);
                                                                     startActivity(intent);
//                                                                     PreferenceConnector.writeString(context, ApiKeys.NAME_FIRST,le.getData().getFirst_name());
//                                                                     PreferenceConnector.writeString(context, ApiKeys.NAME_LAST,le.getData().getLast_name());

                                                                 }
                                                             }
                                                         }

                                                         @Override
                                                         public void onFailed(Object failObject) {
                                                             mActivityAddMobileNumberBinding.contentaddmobilenumber.progressBar.setVisibility(View.GONE);
                                                             if (failObject instanceof MobileNumberRegistrationModel) {
                                                                 MobileNumberRegistrationModel mobileNumberRegistrationModel = (MobileNumberRegistrationModel) failObject;
                                                                 if (mobileNumberRegistrationModel.getMessage().equalsIgnoreCase("Mobile no. already exists")) {
                                                                     if (mobileNumberRegistrationModel.getData().getStatus().equalsIgnoreCase("inactive")) {
                                                                         Intent intent = new Intent(context, OtpVarifyActivity.class);
                                                                         intent.putExtra("type", "registration");
                                                                         intent.putExtra("countrycode", mActivityAddMobileNumberBinding.contentaddmobilenumber.ccp1.getSelectedCountryCode());
                                                                         intent.putExtra("mobilenumber", mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString());
                                                                         startActivity(intent);
                                                                     } else {
                                                                         Toast.makeText(context, "You logged in successfully", Toast.LENGTH_LONG).show();
                                                                         Utility.login(this, context, mActivityAddMobileNumberBinding.contentaddmobilenumber.ccp1.getSelectedCountryCode(),
                                                                                 mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString(), "");
                                                                         PreferenceConnector.writeString(context, ApiKeys.MOBILE, mobileNumberRegistrationModel.getData().getMobile_no());
                                                                         PreferenceConnector.writeString(context, ApiKeys.COUNTRY_CODE, mobileNumberRegistrationModel.getData().getCountry_code());
                                                                         PreferenceConnector.writeString(context, ApiKeys.STATUS, mobileNumberRegistrationModel.getData().getStatus());
                                                                         PreferenceConnector.writeString(context, ApiKeys.UID, mobileNumberRegistrationModel.getData().getUser_id());
                                                                         Intent intent = new Intent(context, MainActivity.class);
                                                                         startActivity(intent);
                                                                     }
                                                                 } else if (mobileNumberRegistrationModel.getMessage().equalsIgnoreCase("token Expired")) {
                                                                     new GenerateTokenClass(context);
                                                                     Toast.makeText(context, mobileNumberRegistrationModel.getMessage() + " please try again", Toast.LENGTH_LONG).show();
                                                                 }
                                                             }
                                                         }
                                                     }, context, mApiService,
                mActivityAddMobileNumberBinding.contentaddmobilenumber.ccp1.getSelectedCountryCode(),
                mActivityAddMobileNumberBinding.contentaddmobilenumber.enterphone.getText().toString().trim());
    }
}
