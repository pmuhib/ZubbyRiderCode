package com.zuby.user.zubbyrider.view.registration_login.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.databinding.ActivityConnectSocialBinding;
import com.zuby.user.zubbyrider.utils.BaseActivity;
import com.zuby.user.zubbyrider.utils.MyApp;

import org.json.JSONObject;
import java.util.Collections;

public class ConnectSocial extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private ActivityConnectSocialBinding mActivityConnectSocialBinding;
    private static final int RC_GET_TOKEN = 9002;
    private final String mTag = getClass().getSimpleName();
    private Activity mActivity;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private boolean isFbLogin;

    CallbackManager callbackManager;
    JSONObject response;
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_EMAIL = "email";
    private static String KEY_PROFILE_PIC = "profile_pic";
    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;
    public static final String PROFILE_USER_ID = "USER_ID";
    public static final String PROFILE_DISPLAY_NAME = "PROFILE_DISPLAY_NAME";
    public static final String PROFILE_USER_EMAIL = "USER_PROFILE_EMAIL";
    public static final String PROFILE_IMAGE_URL = "PROFILE_IMAGE_URL";
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mActivityConnectSocialBinding = DataBindingUtil.setContentView(this, R.layout.activity_connect_social);
        MyApp.app().getDraggerComponenet().inject(this);
        mActivity = this;
        setSupportActionBar(mActivityConnectSocialBinding.toolbar);

        mActivityConnectSocialBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mCallbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mActivityConnectSocialBinding.facebook.setOnClickListener(this);
        mActivityConnectSocialBinding.google.setOnClickListener(this);
        AppEventsLogger.activateApp(this);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");


        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public AppCompatActivity activity;

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("Login Results", loginResult.toString());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                Log.e("response: ", response + "");
                                try {
                                    KEY_UID = object.getString("id");
                                    KEY_EMAIL = object.getString("email");
                                    KEY_FIRSTNAME = object.getString("first_name");
                                    KEY_LASTNAME = object.getString("last_name");

                                    Log.e("Em", "::::::KEY_EMAIL::::::" + " " + KEY_EMAIL);
//                                    KEY_PROFILE_PIC = "https://graph.facebook.com/" + KEY_UID + "/picture?type=large";

//                                    userName.setText(KEY_FIRSTNAME + " " + KEY_LASTNAME);
//                                    userEmail.setText(KEY_EMAIL);

//                                    Glide.with(getApplicationContext())
//                                            .load(KEY_PROFILE_PIC)
//                                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                                            .centerCrop()
//                                            .transform(new CircleTransform(getApplicationContext()))
//                                            .into(userPic);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }

        });
        //Log.e("apiservice",""+apiService.hashCode());
    }
    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        Log.e("Em","response"+" "+response.toString());

                    }

                });
//        Bundle permission_param = new Bundle();
//        permission_param.putString("fields", "id,name,email, picture.width(120).height(120)");
//                data_request.setParameters(permission_param);
//        data_request.executeAsync();



    }

    @Override
    protected void onResume()
    {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.facebook:
                mActivityConnectSocialBinding.login.performClick();
//                isFbLogin = true;
//                openFB();
                break;
            case R.id.google:
                isFbLogin = false;
                signInWithGplus();
                break;
        }
    }

    private void openFB() {
        LoginManager.getInstance().logInWithReadPermissions(mActivity,
                Collections.singletonList("email"));
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse user) {
                                        if (object != null) {
                                            try {
                                                Login(object);
                                            } catch (Exception e) {
                                                Log.e("PG : JSONException " + mTag + " ", e.toString());
                                            }
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,cover,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            signIn();
        }
    }

    protected void onStart() {
        super.onStart();
        // mProgressBar.setVisibility(View.GONE);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("GetGivenName", "" + acct.getGivenName());
            Log.d("Email", "" + acct.getEmail());
            Log.d("GetFamilyName", "" + acct.getFamilyName());
            Log.d("GetId", "" + acct.getId());
            Log.d("GetDisplayName", "" + acct.getDisplayName());
            Log.d("getIdToken", "" + acct.getIdToken());
            Log.d("GetPhotoUrl", "" + acct.getPhotoUrl());
            Log.d("GetServerAuthCode", "" + acct.getServerAuthCode());
            signOut();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    private void signOut() {
        if (mGoogleApiClient != null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d(mTag, "" + status.getStatus());
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
//        mProgressBar.setVisibility(View.VISIBLE);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
        if (isFbLogin) {
            mCallbackManager.onActivityResult(requestCode, responseCode, intent);
        } else if (responseCode == Activity.RESULT_OK) {
            try {
                if (requestCode == RC_GET_TOKEN) {
                    if (intent.equals(null)) {
                        Toast.makeText(ConnectSocial.this, "Try to login again", Toast.LENGTH_LONG).show();
                    } else {
                        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
                        handleSignInResult(result);
                    }
                }
            } catch (NullPointerException ex) {
                Log.d(mTag, ex.toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        isFbLogin = false;
        mGoogleApiClient.disconnect();
        super.onBackPressed();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(mTag, "onConnectionFailed:" + connectionResult);
    }

    private void Login(JSONObject object) {
        String email = "";
        Log.e("object", object.toString());
        try {
            if (object.has("email")) {
                email = object.getString("email");
                Log.e("tag", email);
            }
            LoginManager.getInstance().logOut();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
//            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (mTelephony.getDeviceId() != null) {
//                        android_id = "" + mTelephony.getDeviceId();
//                    } else {
//                        android_id = "" + Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//                    }
//                } else {
//                    //code for deny
//                }
//                break;
        }
    }
}
