package com.zuby.user.zubbyrider.view.ridermanagement.activity;

import android.app.Service;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.databinding.ActivityRiderInformationManagementBinding;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.BaseActivity;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.utils.Utility;
import com.zuby.user.zubbyrider.view.navigationdrawer.activity.GPSTracker;
import com.zuby.user.zubbyrider.view.registration_login.activity.OtpVarifyActivity;
import com.zuby.user.zubbyrider.view.registration_login.model.GetRiderDataModel;
import com.zuby.user.zubbyrider.view.registration_login.model.SetPasswordModel;
import com.zuby.user.zubbyrider.view.registration_login.model.UpdatePasswordModel;
import com.zuby.user.zubbyrider.view.registration_login.model.UpdateRiderDataModel;
import com.zuby.user.zubbyrider.view.registration_login.presenter.AddRiderDataPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.GetRiderDataPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.UpdatePasswordPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.UpdateRiderDataPresenter;
import com.zuby.user.zubbyrider.view.registration_login.presenter.setPasswordPresenter;
import com.zuby.user.zubbyrider.view.tokengenerate.GenerateTokenClass;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RiderInformationManagementActivity extends BaseActivity implements ResultInterface, View.OnClickListener {
    private String mArea, mCity, mLatitude, mLongitude;
    private static final String TAG = RiderInformationManagementActivity.class.getSimpleName();
    private ActivityRiderInformationManagementBinding mActivityRiderInformationManagementBinding;
    private Boolean mIsRiderData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityRiderInformationManagementBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_rider_information_management);
        setSupportActionBar(mActivityRiderInformationManagementBinding.toolbar);
        mActivityRiderInformationManagementBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.getVisibility() == View.VISIBLE) {
                    mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.VISIBLE);
                    mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.GONE);
                    mActivityRiderInformationManagementBinding.fab.setVisibility(View.VISIBLE);
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.setVisibility(View.GONE);
                } else {
                    onBackPressed();
                }
            }
        });
        getDetails();
        getRiderData();
        mActivityRiderInformationManagementBinding.fab.setOnClickListener(this);
        mActivityRiderInformationManagementBinding.riderProfileData.passwordData.setOnClickListener(this);
        mActivityRiderInformationManagementBinding.riderProfileData.setpassword.reset.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                String firstname = data.getStringExtra("firstname");
                String lastname = data.getStringExtra("lastname");
                String email = data.getStringExtra("email");
                Log.e("result", "dhvsjvgahjvhg" + mArea + "\n " + firstname + "\n" + lastname + "\n" + email);
                mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.VISIBLE);
                new AddRiderDataPresenter().show(new ResultInterface() {
                                                     @Override
                                                     public void onSuccess(Object object) {
                                                         Toast.makeText(context, "Rider data added successfully", Toast.LENGTH_LONG).show();
                                                         getRiderData();
                                                     }

                                                     @Override
                                                     public void onFailed(String string) {
                                                         Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                                                     }
                                                 }, context, PreferenceConnector.readString(context, ApiKeys.COUNTRY_CODE, ""),
                        PreferenceConnector.readString(context, ApiKeys.MOBILE, ""),
                        firstname, lastname, "", "English",
                        mCity, mLatitude, mLongitude, email, mArea);
            }
        } else if (requestCode == 1) {
            try {
                String firstname = data.getStringExtra("firstname");
                String lastname = data.getStringExtra("lastname");
                String email = data.getStringExtra("email");
                Log.e("result", "dhvsjvgahjvhg" + "\n " + firstname + "\n" + lastname + "\n" + email);
                mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.VISIBLE);
                new UpdateRiderDataPresenter().show(new ResultInterface() {
                    @Override
                    public void onSuccess(Object object) {
                        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                        UpdateRiderDataModel updateRiderDataModel = (UpdateRiderDataModel) object;
                        Toast.makeText(context, updateRiderDataModel.getMessage(), Toast.LENGTH_LONG).show();
                        getRiderData();
                    }

                    @Override
                    public void onFailed(String string) {
                        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                    }
                }, context, firstname, lastname, email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getDetails() {
        GPSTracker mGps = new GPSTracker(context);
        if (mGps.canGetLocation()) {

            double latitude = mGps.getLatitude();
            double longitude = mGps.getLongitude();
            mLatitude = "" + latitude;
            mLongitude = "" + longitude;
            if (latitude == 0) {

            } else {
                LatLng coordinate = new LatLng(latitude, longitude);
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    mArea = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    mCity = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    Log.e(TAG, mArea + "\n" + mCity + "\n" + mLatitude + "\n" + mLongitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSuccess(Object object) {
        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
        if (object instanceof GetRiderDataModel) {
            mIsRiderData = true;
            GetRiderDataModel getRiderDataModel = (GetRiderDataModel) object;
            Toast.makeText(context, getRiderDataModel.getMessage(), Toast.LENGTH_LONG).show();
            mActivityRiderInformationManagementBinding.riderProfileData.ccp1.setActivated(false);
            mActivityRiderInformationManagementBinding.riderProfileData.ccp1.dispatchSetSelected(false);
            mActivityRiderInformationManagementBinding.riderProfileData.fname.setText(getRiderDataModel.getData().getFirst_name());
            mActivityRiderInformationManagementBinding.riderProfileData.lname.setText(getRiderDataModel.getData().getLast_name());
            mActivityRiderInformationManagementBinding.riderProfileData.pnumber.setText(PreferenceConnector.readString(context, ApiKeys.MOBILE, ""));
            mActivityRiderInformationManagementBinding.riderProfileData.email.setText("" + getRiderDataModel.getData().getEmail_id());
            if (PreferenceConnector.readString(context, ApiKeys.HASPASS, "").equalsIgnoreCase("no")) {
                mActivityRiderInformationManagementBinding.riderProfileData.password.setInputType(InputType.TYPE_CLASS_TEXT);
                mActivityRiderInformationManagementBinding.riderProfileData.password.setText("Set a Password");
            } else {
                mActivityRiderInformationManagementBinding.riderProfileData.password.setText("anup");
            }
            PreferenceConnector.writeString(context, ApiKeys.EMAIL, "" + getRiderDataModel.getData().getEmail_id());
        }
    }

    @Override
    public void onFailed(String string) {
        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
        if (string.equalsIgnoreCase("token Expired")) {
            new GenerateTokenClass(context);
        } else {
            mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.GONE);
            mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Please add Rider Data First", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                if (mIsRiderData) {
                    Intent intent = new Intent(context, OtpVarifyActivity.class);
                    intent.putExtra("type", "updatedata");
                    intent.putExtra("fname", mActivityRiderInformationManagementBinding.riderProfileData.fname.getText().toString().trim());
                    intent.putExtra("lname", mActivityRiderInformationManagementBinding.riderProfileData.lname.getText().toString().trim());
                    intent.putExtra("email", mActivityRiderInformationManagementBinding.riderProfileData.email.getText().toString().trim());
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(context, OtpVarifyActivity.class);
                    intent.putExtra("type", "registrationback");
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.password_data:
                mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.GONE);
                mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.GONE);
                mActivityRiderInformationManagementBinding.fab.setVisibility(View.GONE);
                mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.setVisibility(View.VISIBLE);
                if (PreferenceConnector.readString(context, ApiKeys.HASPASS, "").equals("no")) {
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.setHint("Enter Password");
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.setHint("Reenter Password");
                } else {
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.setHint("Enter Old Password");
                    mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.setHint("Enter New Password");
                }
                break;
            case R.id.reset:
                setAddPassword();
                break;
        }
    }

    public void getRiderData() {
        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.VISIBLE);
        new GetRiderDataPresenter().show(this, context);
    }

    public void setAddPassword() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivityRiderInformationManagementBinding.riderProfileData.setpassword.
                newpassward.getWindowToken(), 0);
        mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.VISIBLE);
        if (PreferenceConnector.readString(context, ApiKeys.HASPASS, "").equalsIgnoreCase("no")) {
            if (Utility.passwordCheck(mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim())) {
                if (mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim().equals(
                        mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.getText().toString().trim()
                )) {
                    new setPasswordPresenter().show(new ResultInterface() {
                        @Override
                        public void onSuccess(Object object) {
                            mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                            SetPasswordModel setPasswordModel = (SetPasswordModel) object;
                            Toast.makeText(context, setPasswordModel.getMessage(), Toast.LENGTH_LONG).show();
                            PreferenceConnector.writeString(context, ApiKeys.HASPASS, "yes");
                            mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.VISIBLE);
                            mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.GONE);
                            mActivityRiderInformationManagementBinding.fab.setVisibility(View.VISIBLE);
                            mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.setVisibility(View.GONE);
                            getRiderData();
                        }

                        @Override
                        public void onFailed(String string) {
                            mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                        }
                    }, context, mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.getText().toString().trim());
                } else {
                    Toast.makeText(context, "password doesnot match", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Please enter more then 6 character", Toast.LENGTH_LONG).show();
            }
        } else {
            if (Utility.passwordCheck(mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim())) {
                if (!mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim().equals("") &&
                        !mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.getText().toString().trim().equals("")
                        ) {
                    new UpdatePasswordPresenter().show(new ResultInterface() {
                                                           @Override
                                                           public void onSuccess(Object object) {
                                                               mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                                                               UpdatePasswordModel setPasswordModel = (UpdatePasswordModel) object;
                                                               Toast.makeText(context, setPasswordModel.getMessage(), Toast.LENGTH_LONG).show();
                                                               PreferenceConnector.writeString(context, ApiKeys.HASPASS, "yes");
                                                               mActivityRiderInformationManagementBinding.riderProfileData.datashow.setVisibility(View.VISIBLE);
                                                               mActivityRiderInformationManagementBinding.riderProfileData.nodata.setVisibility(View.GONE);
                                                               mActivityRiderInformationManagementBinding.fab.setVisibility(View.VISIBLE);
                                                               mActivityRiderInformationManagementBinding.riderProfileData.setpassword.addPass.setVisibility(View.GONE);
                                                               getRiderData();
                                                           }

                                                           @Override
                                                           public void onFailed(String string) {
                                                               mActivityRiderInformationManagementBinding.riderProfileData.progressBar.setVisibility(View.GONE);
                                                               Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                                                           }
                                                       }, context, mActivityRiderInformationManagementBinding.riderProfileData.setpassword.passward.getText().toString().trim(),
                            mActivityRiderInformationManagementBinding.riderProfileData.setpassword.newpassward.getText().toString().trim());
                } else {
                    Toast.makeText(context, "Please enter your old and new password", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Please enter more then 6 character", Toast.LENGTH_LONG).show();
            }
        }
    }
}
