package com.zuby.user.zubbyrider.view.registration_login.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hbb20.CountryCodePicker;
import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.databinding.ActivityFirstBinding;
import com.zuby.user.zubbyrider.utils.BaseActivity;


public class FirstActivity extends BaseActivity implements View.OnClickListener {
    private String countryCodeAndroid = "91";
    private ActivityFirstBinding mActivityFirstBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityFirstBinding = DataBindingUtil.setContentView(this, R.layout.activity_first);
        setSupportActionBar(mActivityFirstBinding.toolbar);
        getSupportActionBar().setTitle("");
        mActivityFirstBinding.toolbar.setVisibility(View.GONE);
        mActivityFirstBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mActivityFirstBinding.contentFirst.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCodeAndroid = mActivityFirstBinding.contentFirst.ccp.getSelectedCountryCode();
                Log.d("Country Code", countryCodeAndroid);
            }
        });
        mActivityFirstBinding.contentFirst.addnumber.setOnClickListener(this);
        mActivityFirstBinding.contentFirst.connectsocial.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.addnumber:
                Intent intent = new Intent(FirstActivity.this, AddMobileNumber.class);
                startActivity(intent);
                break;
            case R.id.connectsocial:

                break;

        }
    }
}
