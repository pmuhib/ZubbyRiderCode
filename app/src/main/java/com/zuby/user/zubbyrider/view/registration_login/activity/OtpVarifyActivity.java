package com.zuby.user.zubbyrider.view.registration_login.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.databinding.ActivityOtpVarifyBinding;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.interfaces.ResultInterface1;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.BaseActivity;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.utils.Utility;
import com.zuby.user.zubbyrider.view.navigationdrawer.activity.MainActivity;
import com.zuby.user.zubbyrider.view.registration_login.model.Level2RegistrationModel;
import com.zuby.user.zubbyrider.view.registration_login.model.LoginModel;
import com.zuby.user.zubbyrider.view.registration_login.model.LoginThroughPassword;
import com.zuby.user.zubbyrider.view.registration_login.model.OtpVarifyModel;
import com.zuby.user.zubbyrider.view.registration_login.model.SendOtpModel;
import com.zuby.user.zubbyrider.view.registration_login.model.SetPasswordModel;
import com.zuby.user.zubbyrider.view.registration_login.presenter.Level2RegistraionPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.LoginUsingPasswordPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.OtpVarifyPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.SendOtpPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.setPasswordPresenter;
import com.zuby.user.zubbyrider.view.tokengenerate.GenerateTokenClass;

import java.util.ArrayList;
import java.util.List;

public class OtpVarifyActivity extends BaseActivity implements View.OnClickListener, ResultInterface1, View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    private ActivityOtpVarifyBinding mActivityOtpVarifyBinding;
    private String mCountryCode, mMobileNumber, mType;
    private Boolean mCheck = false;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    int flag = 0;
    private boolean isOtp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityOtpVarifyBinding = DataBindingUtil.setContentView(this, R.layout.activity_otp_varify);
        setSupportActionBar(mActivityOtpVarifyBinding.toolbar);
        mActivityOtpVarifyBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mActivityOtpVarifyBinding.fab.setOnClickListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.forgotpassword.setOnClickListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.donthaveaccount.setOnClickListener(this);
        mType = getIntent().getStringExtra("type");
        if (mType.equalsIgnoreCase("registration")) {
            mActivityOtpVarifyBinding.contentOtpVarify.enterDetails.setVisibility(View.VISIBLE);
            mActivityOtpVarifyBinding.contentOtpVarify.addPassword.setVisibility(View.GONE);
        } else if (mType.equalsIgnoreCase("password")) {
            mActivityOtpVarifyBinding.contentOtpVarify.enterDetails.setVisibility(View.GONE);
            mActivityOtpVarifyBinding.contentOtpVarify.addPassword.setVisibility(View.VISIBLE);
        } else if (mType.equalsIgnoreCase("registrationback")) {
            mActivityOtpVarifyBinding.contentOtpVarify.enterDetails.setVisibility(View.VISIBLE);
            mActivityOtpVarifyBinding.contentOtpVarify.addPassword.setVisibility(View.GONE);
        } else if (mType.equalsIgnoreCase("updatedata")) {
            mActivityOtpVarifyBinding.contentOtpVarify.enterDetails.setVisibility(View.VISIBLE);
            mActivityOtpVarifyBinding.contentOtpVarify.addPassword.setVisibility(View.GONE);
            mActivityOtpVarifyBinding.contentOtpVarify.enterEmail.setVisibility(View.VISIBLE);
            mActivityOtpVarifyBinding.contentOtpVarify.first.setText(getIntent().getStringExtra("fname"));
            mActivityOtpVarifyBinding.contentOtpVarify.last.setText(getIntent().getStringExtra("lname"));
            mActivityOtpVarifyBinding.contentOtpVarify.email.setText(getIntent().getStringExtra("email"));
        }
        if (mType.equalsIgnoreCase("registrationback")) {
            mCountryCode = PreferenceConnector.readString(context, ApiKeys.COUNTRY_CODE, "");
            mMobileNumber = PreferenceConnector.readString(context, ApiKeys.MOBILE, "");
        } else {
            mCountryCode = getIntent().getStringExtra("countrycode");
            mMobileNumber = getIntent().getStringExtra("mobilenumber");
        }
        mActivityOtpVarifyBinding.contentOtpVarify.forgotpassword.setOnClickListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.GONE);
        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.resend.setOnClickListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.passwordset.reset.setOnClickListener(this);
        setPINListeners();
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.reset:
                setAddPassword();
                break;
            case R.id.resend:
                mActivityOtpVarifyBinding.contentOtpVarify.forgotpassword.performLongClick();
                break;
            case R.id.fab:
                if (isOtp) {
                    varifyOtp();
                    isOtp = false;
                } else {
                    if (mType.equalsIgnoreCase("password")) {
                        mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.VISIBLE);
                        new LoginUsingPasswordPresenter().show(new ResultInterface() {
                                                                   @Override
                                                                   public void onSuccess(Object object) {
                                                                       mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.GONE);
                                                                       LoginThroughPassword loginThroughPassword = (LoginThroughPassword) object;
                                                                       Toast.makeText(context, loginThroughPassword.getType(), Toast.LENGTH_LONG).show();
                                                                       Intent intent = new Intent(context, MainActivity.class);
                                                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                       intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                                       startActivity(intent);
                                                                   }

                                                                   @Override
                                                                   public void onFailed(String string) {
                                                                       mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.GONE);
                                                                       Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                                                                   }
                                                               }, context, mCountryCode, mMobileNumber,
                                mActivityOtpVarifyBinding.contentOtpVarify.enterpass.getText().toString().trim());
                    } else if (mType.equalsIgnoreCase("registration")) {
                        if (Utility.nameMatcher(mActivityOtpVarifyBinding.contentOtpVarify.first.getText().toString().trim())) {
                            Toast.makeText(context, "Enter your first name", Toast.LENGTH_LONG).show();
                        } else if (Utility.nameMatcher(mActivityOtpVarifyBinding.contentOtpVarify.last.getText().toString().trim())) {
                            Toast.makeText(context, "Enter your last name", Toast.LENGTH_LONG).show();
                        } else {
                            level2Registration();
                        }
                    } else if (mType.equalsIgnoreCase("registrationback")) {
                        if (mCheck) {
                            if (Utility.nameMatcher(mActivityOtpVarifyBinding.contentOtpVarify.first.getText().toString().trim())) {
                                Toast.makeText(context, "Please enter first name", Toast.LENGTH_LONG).show();
                            } else if (Utility.nameMatcher(mActivityOtpVarifyBinding.contentOtpVarify.last.getText().toString().trim())) {
                                Toast.makeText(context, "Please enter last name", Toast.LENGTH_LONG).show();
                            } else {
                                String email = "";
                                if (mActivityOtpVarifyBinding.contentOtpVarify.email.getText().toString().trim().equalsIgnoreCase("")) {
                                    email = " ";
                                    addDataRider(email);
                                } else {
                                    if (Utility.checkEmail(mActivityOtpVarifyBinding.contentOtpVarify.email.getText().toString().trim())) {
                                        email = mActivityOtpVarifyBinding.contentOtpVarify.email.getText().toString().trim();
                                        addDataRider(email);
                                    } else {
                                        Toast.makeText(context, "Please enter valid email id", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                        } else {
                            mActivityOtpVarifyBinding.contentOtpVarify.enterEmail.setVisibility(View.VISIBLE);
                            mCheck = true;
                        }
                    } else if (mType.equalsIgnoreCase("updatedata")) {
                        if (!Utility.nameMatcher(mActivityOtpVarifyBinding.contentOtpVarify.first.getText().toString().trim())) {
                            Toast.makeText(context, "Please enter first name", Toast.LENGTH_LONG).show();
                        } else if (!Utility.nameMatcher(mActivityOtpVarifyBinding.contentOtpVarify.last.getText().toString().trim())) {
                            Toast.makeText(context, "Please enter last name", Toast.LENGTH_LONG).show();
                        } else {
                            String email = "";
                            if (mActivityOtpVarifyBinding.contentOtpVarify.email.getText().toString().trim().equalsIgnoreCase("")) {
                                email = " ";
                                addDataRider(email);
                            } else {
                                if (Utility.checkEmail(mActivityOtpVarifyBinding.contentOtpVarify.email.getText().toString().trim())) {
                                    email = mActivityOtpVarifyBinding.contentOtpVarify.email.getText().toString().trim();
                                    addDataRider(email);
                                } else {
                                    Toast.makeText(context, "Please enter valid email id", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }
                break;
            case R.id.forgotpassword:
                mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.VISIBLE);
                new SendOtpPresenter().show(new ResultInterface() {
                                                @Override
                                                public void onSuccess(Object object) {
                                                    isOtp = true;
                                                    mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.GONE);
                                                    SendOtpModel sendOtpModel = (SendOtpModel) object;
                                                    Toast.makeText(context, sendOtpModel.getMessage(), Toast.LENGTH_LONG).show();
                                                    mActivityOtpVarifyBinding.contentOtpVarify.addPassword.setVisibility(View.GONE);
                                                    mActivityOtpVarifyBinding.contentOtpVarify.enterDetails.setVisibility(View.GONE);
                                                    mActivityOtpVarifyBinding.contentOtpVarify.enterotp.addOtp.setVisibility(View.VISIBLE);
                                                    if (checkAndRequestPermissions()) {
                                                        // carry on the normal flow, as the case of  permissions  granted.
                                                    }
                                                    mActivityOtpVarifyBinding.contentOtpVarify.enterotp.addOtp.setVisibility(View.VISIBLE);
                                                    mActivityOtpVarifyBinding.contentOtpVarify.enterotp.timer.setVisibility(View.VISIBLE);
                                                    mActivityOtpVarifyBinding.contentOtpVarify.enterotp.resend.setClickable(false);
                                                    mActivityOtpVarifyBinding.contentOtpVarify.enterotp.resend.setText(getResources().getString(R.string.resendcode));
                                                    new CountDownTimer(30000, 1000) {

                                                        public void onTick(long millisUntilFinished) {
                                                            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.timer.setText("" + millisUntilFinished / 1000);
                                                            //here you can have your logic to set text to edittext
                                                        }

                                                        public void onFinish() {
                                                            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.timer.setVisibility(View.GONE);
                                                            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.resend.setText("Resend Code");
                                                            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.resend.setClickable(true);
                                                        }

                                                    }.start();
                                                }

                                                @Override
                                                public void onFailed(String string) {
                                                    mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.GONE);
                                                    if (string.equalsIgnoreCase("token Expired")) {
                                                        new GenerateTokenClass(context);
                                                        Toast.makeText(context, string + " please try again", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }, context, mApiService,
                        mCountryCode,
                        mMobileNumber);
                // Toast.makeText(context, "Under work will be done by tomorrow mng", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void level2Registration() {
        mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        new Level2RegistraionPresenter().show(this, context, mApiService, mCountryCode, mMobileNumber,
                mActivityOtpVarifyBinding.contentOtpVarify.first.getText().toString().trim(),
                mActivityOtpVarifyBinding.contentOtpVarify.last.getText().toString().trim());
    }

    @Override
    public void onSuccess(Object object) {
        mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (object instanceof Level2RegistrationModel) {
            Level2RegistrationModel le = (Level2RegistrationModel) object;
            Toast.makeText(context, le.getMessage(), Toast.LENGTH_LONG).show();
            PreferenceConnector.writeString(context, ApiKeys.NAME_FIRST, mActivityOtpVarifyBinding.contentOtpVarify.first.getText().toString().trim());
            PreferenceConnector.writeString(context, ApiKeys.NAME_LAST, mActivityOtpVarifyBinding.contentOtpVarify.last.getText().toString().trim());
            PreferenceConnector.writeString(context, ApiKeys.MOBILE, mMobileNumber);
            PreferenceConnector.writeString(context, ApiKeys.COUNTRY_CODE, mCountryCode);
            PreferenceConnector.writeString(context, ApiKeys.STATUS, le.getData().getStatus());
            PreferenceConnector.writeString(context, ApiKeys.UID, le.getData().getUser_id());
            Utility.login(this, context, mCountryCode, mMobileNumber, "");
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        } else if (object instanceof LoginModel) {
            LoginModel loginModel = (LoginModel) object;
            Toast.makeText(context, loginModel.getMessage(), Toast.LENGTH_LONG).show();
            PreferenceConnector.writeString(context, ApiKeys.MOBILE, mMobileNumber);
            PreferenceConnector.writeString(context, ApiKeys.COUNTRY_CODE, mCountryCode);
            PreferenceConnector.writeString(context, ApiKeys.STATUS, "active");
            PreferenceConnector.writeString(context, ApiKeys.UID, loginModel.getData().getUser_id());
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onFailed(Object failobject) {
        mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (failobject instanceof Level2RegistrationModel) {
            Level2RegistrationModel le = (Level2RegistrationModel) failobject;
            if (le.getMessage().equalsIgnoreCase("token Expired")) {
                new GenerateTokenClass(context);
                Toast.makeText(context, le.getMessage() + "! Please try again", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, le.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (failobject instanceof LoginModel) {
            LoginModel loginModel = (LoginModel) failobject;
            if (loginModel.getMessage().equalsIgnoreCase("token Expired")) {
                new GenerateTokenClass(context);
                Toast.makeText(context, loginModel.getMessage() + "! Please try again", Toast.LENGTH_LONG).show();
                Utility.login(this, context, mCountryCode, mMobileNumber, "");
            }
        }
    }

    public void addDataRider(String email) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("firstname", mActivityOtpVarifyBinding.contentOtpVarify.first.getText().toString().trim());
        returnIntent.putExtra("lastname", mActivityOtpVarifyBinding.contentOtpVarify.last.getText().toString().trim());
        returnIntent.putExtra("email", email);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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

    /**
     * Initialize EditText fields.
     */

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext);
                    showSoftKeyboard(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    setFocus(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext);
                    showSoftKeyboard(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    setFocus(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext);
                    showSoftKeyboard(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext);
                    showSoftKeyboard(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext);
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
                        if (mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.getText().length() == 4)
                            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext.setText("");
                        else if (mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.getText().length() == 3)
                            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext.setText("");
                        else if (mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.getText().length() == 2)
                            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinSecondEdittext.setText("");
                        else if (mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.getText().length() == 1)
                            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinFirstEdittext.setText("");

                        if (mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.length() > 0)
                            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.setText
                                    (mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.getText().subSequence
                                            (0, mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.length() - 1));

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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setDefaultPinBackground(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinFirstEdittext);
        setDefaultPinBackground(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinSecondEdittext);
        setDefaultPinBackground(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext);
        setDefaultPinBackground(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext);

        if (s.length() == 0) {
            setFocusedPinBackground(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinFirstEdittext);
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinFirstEdittext.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinSecondEdittext);
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinFirstEdittext.setText(s.charAt(0) + "");
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinSecondEdittext.setText("");
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext.setText("");
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext);
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinSecondEdittext.setText(s.charAt(1) + "");
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext.setText("");
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext);
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext.setText(s.charAt(2) + "");
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext.setText("");
        } else if (s.length() == 4) {
            mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext.setText(s.charAt(3) + "");
            hideSoftKeyboard(mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext);
            Log.e("working", "working");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

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
        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.addTextChangedListener(this);

        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinFirstEdittext.setOnFocusChangeListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinSecondEdittext.setOnFocusChangeListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext.setOnFocusChangeListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext.setOnFocusChangeListener(this);

        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinFirstEdittext.setOnKeyListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinSecondEdittext.setOnKeyListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext.setOnKeyListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext.setOnKeyListener(this);
        mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinHiddenEdittext.setOnKeyListener(this);
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
                mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinFirstEdittext.setText("" + sms[0]);
                mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinSecondEdittext.setText("" + sms[1]);
                mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext.setText("" + sms[2]);
                mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext.setText("" + sms[3]);
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


    public void varifyOtp() {
        String otp = mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinFirstEdittext.getText().toString().trim() +
                mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinSecondEdittext.getText().toString().trim() +
                mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinThirdEdittext.getText().toString().trim() +
                mActivityOtpVarifyBinding.contentOtpVarify.enterotp.pinForthEdittext.getText().toString().trim();
        mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.VISIBLE);
        new OtpVarifyPresenter().show(new ResultInterface() {
                                          @Override
                                          public void onSuccess(Object object) {
                                              OtpVarifyModel otpVarifyModel = (OtpVarifyModel) object;
                                              Toast.makeText(context, otpVarifyModel.getMessage(), Toast.LENGTH_LONG).show();
                                              mActivityOtpVarifyBinding.contentOtpVarify.enterotp.addOtp.setVisibility(View.GONE);
                                              mActivityOtpVarifyBinding.contentOtpVarify.enterDetails.setVisibility(View.GONE);
                                              mActivityOtpVarifyBinding.contentOtpVarify.addPassword.setVisibility(View.GONE);
                                              mActivityOtpVarifyBinding.contentOtpVarify.passwordset.addPass.setVisibility(View.VISIBLE);
                                              mActivityOtpVarifyBinding.fab.setVisibility(View.GONE);
                                          }

                                          @Override
                                          public void onFailed(String string) {
                                              Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                                          }
                                      }, context, mApiService,
                mCountryCode,
                mMobileNumber.trim(), otp);
    }

    public void setAddPassword() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityOtpVarifyBinding.contentOtpVarify.passwordset.newpassward.
                getWindowToken(), 0);
        mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.VISIBLE);
        if (Utility.passwordCheck(mActivityOtpVarifyBinding.contentOtpVarify.passwordset.passward.getText().toString().trim())) {
            if (mActivityOtpVarifyBinding.contentOtpVarify.passwordset.passward.getText().toString().trim().equals(
                    mActivityOtpVarifyBinding.contentOtpVarify.passwordset.newpassward.getText().toString().trim()
            )) {
                mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.VISIBLE);
                new setPasswordPresenter().show(new ResultInterface() {
                                                    @Override
                                                    public void onSuccess(Object object) {
                                                        mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.GONE);
                                                        SetPasswordModel setPasswordModel = (SetPasswordModel) object;
                                                        Toast.makeText(context, setPasswordModel.getMessage(), Toast.LENGTH_LONG).show();
                                                        PreferenceConnector.writeString(context, ApiKeys.HASPASS, "yes");
                                                        Intent intent = new Intent(context, MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }

                                                    @Override
                                                    public void onFailed(String string) {
                                                        mActivityOtpVarifyBinding.contentOtpVarify.progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                                                    }
                                                }, context,
                        mActivityOtpVarifyBinding.contentOtpVarify.passwordset.newpassward.getText().toString().trim());
            } else {
                Toast.makeText(context, "password doesnot match", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Please enter more then 6 character", Toast.LENGTH_LONG).show();
        }
    }
}
