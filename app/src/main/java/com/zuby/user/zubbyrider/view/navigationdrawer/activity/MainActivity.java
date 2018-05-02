package com.zuby.user.zubbyrider.view.navigationdrawer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.http.RequestQueue;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.databinding.ActivityMainBinding;
import com.zuby.user.zubbyrider.globaldata.PlaceAutocompleteAdapter;
import com.zuby.user.zubbyrider.globaldata.Utils;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.BaseActivity;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.utils.Utility;
import com.zuby.user.zubbyrider.view.navigationdrawer.NearbyCabPresenter;
import com.zuby.user.zubbyrider.view.navigationdrawer.fragment.FragmentDrawer;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.directions.route.RoutingListener;
import com.zuby.user.zubbyrider.view.navigationdrawer.model.NearbyDriverModel;

public class MainActivity extends BaseActivity implements OnMapReadyCallback,
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
        FragmentDrawer.FragmentDrawerListener, RoutingListener {

    private GoogleMap mMap;
    private List<Polyline> polylines;
    boolean checkstatuspermission;
    private View mMapView;
    public static int GpsRequesst = 101;
    public static int PERMISSION = 102;
    GoogleApiClient client;
    LatLng currentlatLng, Destlatlong, mSourceLatLng, mDestinationLatLng;
    private FusedLocationProviderClient mFusedLocationClient;
    AutoCompleteTextView enterPlace;
    TextView Search, Go;
    private PolylineOptions polylineOptions, blackPolylineOptions;

    Polyline greypolyline, blackpolyline;
    MarkerOptions currentMarker, destnationMarker;
    List<Address> currentAddresses, destinationAddress;
    double radius = 0.50;
    PlaceAutocompleteAdapter placeAutocompleteAdapter;
    String mCityName, mPostalCode, destlocation, distance;
    private DrawerLayout drawerLayout;
    private FragmentDrawer drawerFragment;
    private ActivityMainBinding mActivityMainBinding;
    private SupportMapFragment mapFragment;
    private Boolean isSource = true;
    Handler handler = new Handler();
    int i = 0;
    // Define the code block to be executed
//    private Runnable runnableCode = new Runnable() {
//        @Override
//        public void run() {
//            i++;
//            Log.e("Handlers", "Called on main thread" + i+mSourceLatLng);
//            if (mSourceLatLng != null) {
//                Log.e("Handlers12", "Called on main thread" + i+mSourceLatLng);
////                getNearbyCab(mSourceLatLng.latitude, mSourceLatLng.longitude,
////                        mCityName, mPostalCode);
//            }
//            handler.postDelayed(this, 20000);
//        }
//    };

    // Start the initial runnable task by posting through the handler
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        polylines = new ArrayList<>();
        enterPlace = findViewById(R.id.input_search);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityMainBinding.toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mActivityMainBinding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mActivityMainBinding.toolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);
        client = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this, this).build();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapView = mapFragment.getView();
        if (Utils.isNetworkavailable(this)) {
            checkgpsstatus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermissions();
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            } else {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                init();
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Check Network Connection", Snackbar.LENGTH_LONG).show();
        }
        mActivityMainBinding.continue2.setOnClickListener(this);
    }


    private void checkgpsstatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsenabled) {
            gotoGpsSettings();
        } else {
        }
    }

    private void checkPermissions() {
        if (Utility.checkLocation(this)) {
            checkstatuspermission = true;
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // Utils.Message(MapsActivity.this, "Allowed");
        } else {
            checkstatuspermission = false;
            // Utils.Message(MapsActivity.this, "Need Permissions to run this App");
            gotoPermissionSettings();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentlatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mSourceLatLng = currentlatLng;
                    currentAddresses = codeaddress(location);
                    if (!currentAddresses.isEmpty() && currentAddresses.size() > 0) {
                        currentMarker = new MarkerOptions();
                        currentMarker.position(currentlatLng).title("My Location").snippet(currentAddresses.get(0).getAddressLine(0))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mActivityMainBinding.inputSearch.setHint(currentAddresses.get(0).getAddressLine(0));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatLng, 17));
                        setupAutocomplete();

                        String cityName = currentAddresses.get(0).getAddressLine(0);
                        String arr[] = cityName.split(",");
                        String city = "" + arr[arr.length - 3];
                        Log.e("postalcode", currentAddresses.get(0).getPostalCode());
                        Log.e("postalcodexxx", arr[arr.length - 3]);
                        Log.e("latitude", "" + mSourceLatLng.latitude);
                        Log.e("longitude", "" + mSourceLatLng.longitude);
                        Log.e("cityname", currentAddresses.get(0).getAddressLine(0));
                        mPostalCode = currentAddresses.get(0).getPostalCode();
                        mCityName = city;
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                getNearbyCab(mSourceLatLng.latitude, mSourceLatLng.longitude,
//                                        mCityName, mPostalCode);
//                            }
//                        }, 5000);
                        //handler.post(runnableCode);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.Message(context, "Error trying to get last GPS location");
                }
            });
        }

        if (mMapView != null &&
                mMapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).
                    findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 60);
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // init();
                    mActivityMainBinding.inputSearch.setHint("getting location");
                    final Location location = mMap.getMyLocation();
                    new Handler().postDelayed(new Runnable() {


                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            // Start your app main activity
                            mSourceLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            currentAddresses = codeaddress(location);
                            if (!currentAddresses.isEmpty() && currentAddresses.size() > 0) {
                                currentMarker = new MarkerOptions();
                                currentMarker.position(currentlatLng).title("My Location").snippet(currentAddresses.get(0).getAddressLine(0))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mActivityMainBinding.inputSearch.setHint(currentAddresses.get(0).getAddressLine(0));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatLng, 17));
                                String cityName = currentAddresses.get(0).getAddressLine(0);
                                String arr[] = cityName.split(",");
                                String city = "" + arr[arr.length - 3];
                                mPostalCode = currentAddresses.get(0).getPostalCode();
                                mCityName = city;
//                                getNearbyCab(mSourceLatLng.latitude, mSourceLatLng.longitude,
//                                        mCityName, mPostalCode);
                            }
                        }
                    }, 1000);
                }
            });
        }
    }

    public void init() {
        mapFragment.getMapAsync(this);
    }

    private void setupAutocomplete() {
        LatLng latLng1 = new LatLng(currentlatLng.latitude + radius, currentlatLng.longitude + radius);
        LatLng latLng2 = new LatLng(currentlatLng.latitude - radius, currentlatLng.longitude - radius);
        LatLngBounds bounds = LatLngBounds.builder().include(latLng2).include(latLng1).build();
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, client, bounds, null);
        mActivityMainBinding.inputSearch.setAdapter(placeAutocompleteAdapter);
        mActivityMainBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("test", s.toString());
                isSource = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                geolocate(mActivityMainBinding.inputSearch.getText().toString().trim());
            }
        });
        mActivityMainBinding.inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    hideSoftKeyboard(mActivityMainBinding.inputSearch);
                }
                return false;
            }
        });
        mActivityMainBinding.inputSearch1.setAdapter(placeAutocompleteAdapter);
        mActivityMainBinding.inputSearch1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isSource = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("test", s.toString());
                geolocate(mActivityMainBinding.inputSearch1.getText().toString().trim());
            }
        });
        mActivityMainBinding.inputSearch1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    hideSoftKeyboard(mActivityMainBinding.inputSearch1);
                }
                return false;
            }
        });
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    private void gotoPermissionSettings() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PERMISSION);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();

    }

    private void gotoGpsSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings").setMessage("GPS is not enabled. Do you want to go to settings menu?");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GpsRequesst);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GpsRequesst) {
            checkgpsstatus();
        }
        if (requestCode == PERMISSION) {
            if (checkstatuspermission == false) {
                checkPermissions();
            }
        }

    }

    private List<Address> codeaddress(Location currentlatLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(currentlatLng.getLatitude(), currentlatLng.getLongitude(), 1);
            if (addressList.isEmpty()) {
                Utils.Message(this, "Waiting");
            } else {
                if (addressList.size() > 0) {
                    return addressList;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue2:
                erasePolylines();
                getRouteToMarker(mDestinationLatLng);
                break;
        }
    }

    private void geolocate(String location) {
        if (!location.isEmpty()) {
            // mMap.clear();
            Address address = null;
            destnationMarker = new MarkerOptions();
            Geocoder geocoder = new Geocoder(this);
            try {
                destinationAddress = geocoder.getFromLocationName(location, 5);
                for (int i = 0; i < destinationAddress.size(); i++) {
                    address = destinationAddress.get(0);
                    Destlatlong = new LatLng(address.getLatitude(), address.getLongitude());
                    if (isSource) {
                        mSourceLatLng = Destlatlong;
                    } else {
                        mDestinationLatLng = Destlatlong;
                        mActivityMainBinding.continue2.setVisibility(View.VISIBLE);
                    }

                    destnationMarker.position(Destlatlong).title("Result Location=" + destinationAddress.get(0).getFeatureName() + "," + destinationAddress.get(0).getSubLocality() + "," + destinationAddress.get(0).getSubAdminArea()).snippet(destinationAddress.get(0).getLocality() + "," + destinationAddress.get(0).getAdminArea() + "," + destinationAddress.get(0).getCountryName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(destnationMarker);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Destlatlong));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Destlatlong, 16));
                    //  setupline();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    private void getRouteToMarker(LatLng pickupLatLng) {
        if (mSourceLatLng != null && mDestinationLatLng != null) {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(mSourceLatLng, mDestinationLatLng)
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void hospitals(View view) {
        // nearbyHospitals();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
//        Fragment fragment = null;
//        String title = getString(R.string.app_name);
//        switch (position) {
//            case 0:
//                fragment = new HomeFragment();
//                title = getString(R.string.title_home);
//                break;
//            case 1:
//                fragment = new FriendsFragment();
//                title = getString(R.string.title_friends);
//                break;
//            case 2:
//                fragment = new MessagesFragment();
//                title = getString(R.string.title_messages);
//                break;
//            default:
//                break;
//        }
//
//        if (fragment != null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.container_body, fragment);
//            fragmentTransaction.commit();
//
//            // set the toolbar title
//            getSupportActionBar().setTitle(title);
//        }
    }

    //    private void nearbyHospitals()
//    {
//        int PROXIMITY_RADIUS;
//        if (currentlatLng!=null) {
//            final MarkerOptions markerOptions = new MarkerOptions();
//            PROXIMITY_RADIUS = 500;
//
//         /*   if(distance!=null )
//            {
//                PROXIMITY_RADIUS= Integer.parseInt(distance)*1000;
//            }
//            else {
//                PROXIMITY_RADIUS = 500;
//
//            }*/
//            //Defines the distance (in meters)
//
//            String key="AIzaSyDCDw0K3aui_cCWMvFQVYQfkxw5l9DHJaw";
//
//            StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//            googlePlaceUrl.append("location="+currentlatLng.latitude+","+currentlatLng.longitude);
//            googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
//            googlePlaceUrl.append("&type="+"hospital");
//            googlePlaceUrl.append("&key="+key);
//            String url=googlePlaceUrl.toString();
//            RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
//            StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Gson gson=new Gson();
//                    GetNearByPlaces nearByPlaces=null;
//
//                    try {
//                        nearByPlaces=new GetNearByPlaces();
//                        nearByPlaces=gson.fromJson(response,GetNearByPlaces.class);
//                        String status=nearByPlaces.getStatus();
//                        List<GetNearByPlaces.ResultsBean> list=new ArrayList<>();
//                        list=nearByPlaces.getResults();
//                        for (int i=0;i<list.size();i++)
//                        {
//                            double lat=list.get(i).getGeometry().getLocation().getLat();
//                            double longt=list.get(i).getGeometry().getLocation().getLng();
//                            String name=list.get(i).getName();
//                            String vicnity=list.get(i).getVicinity();
//                            LatLng latLng=new LatLng(lat,longt);
//                            markerOptions.title(name).position(latLng).snippet(vicnity).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//                            mMap.addMarker(markerOptions);
//                            Log.d("hos",response);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d("Error",""+error);
//
//                }
//            });
//            requestQueue.add(stringRequest);
//        } else {
//        }
//
//    }
    public void getNearbyCab(double latitude, double longitude, String cityName, String postalCode) {
        new NearbyCabPresenter().show(new ResultInterface() {
            @Override
            public void onSuccess(Object object) {
                NearbyDriverModel nearbyDriverModel = (NearbyDriverModel) object;
                //Toast.makeText(context, nearbyDriverModel.getMessage(), Toast.LENGTH_LONG).show();
                mList.clear();
                for (int i = 0; i < nearbyDriverModel.getData().size(); i++) {
                    NearbyDriverModel.DataBean dataBean = nearbyDriverModel.getData().get(i);
                    mList.add(dataBean);
                }
                addMarker(mList);
            }

            @Override
            public void onFailed(String string) {
                Toast.makeText(context, string, Toast.LENGTH_LONG).show();
            }
        }, context, "" + latitude, "" + longitude, cityName, postalCode);
    }

    List<NearbyDriverModel.DataBean> mList = new ArrayList<>();

    public void addMarker(List<NearbyDriverModel.DataBean> list) {
        for (int i = 0; i < mList.size(); i++) {
            NearbyDriverModel.DataBean dataBean = mList.get(i);
            mList.add(dataBean);
            MarkerOptions destnationMarker1 = new MarkerOptions();
            try {
                destnationMarker1.position(new LatLng(Double.parseDouble(dataBean.getDriver_lat()),
                        Double.parseDouble(dataBean.getDriver_long()))).title("").
                        snippet("hello").
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                mMap.addMarker(destnationMarker1);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(dataBean.getDriver_lat()),
                        Double.parseDouble(dataBean.getDriver_long()))));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(dataBean.getDriver_lat()),
                        Double.parseDouble(dataBean.getDriver_long())), 16));
                //  setupline();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
