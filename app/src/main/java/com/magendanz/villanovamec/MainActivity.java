package com.magendanz.villanovamec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final float TAB_FADE = .5f;
    private static final float SETTINGS_FADE = .3f;

    private View scheduleButton;
    private View newsButton;
    private View mapButton;
    private View rideButton;
    private View settingsButton;

    private ScheduleFragment scheduleFragment;
    private NewsFragment newsFragment;
    private MapFragment mappingFragment;
    private RideFragment rideFragment;
    private SettingsFragment settingsFragment;
    private Resources res;

    GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleButton = findViewById(R.id.schedule_button);
        newsButton = findViewById(R.id.map_button);
        mapButton = findViewById(R.id.news_button);
        rideButton = findViewById(R.id.ride_button);
        settingsButton = findViewById(R.id.settings_button);
        res = getResources();
        scheduleFragment = new ScheduleFragment();
        newsFragment = new NewsFragment();
        rideFragment = new RideFragment();
        settingsFragment = new SettingsFragment();

        TypedValue out = new TypedValue();
        res.getValue(R.raw.villanova_lat, out, true);
        final float initLat = out.getFloat();
        res.getValue(R.raw.villanova_lng, out, true);
        final float initLong = out.getFloat();
        res.getValue(R.raw.map_init_zoom, out, true);
        final float zoom = out.getFloat();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(initLat, initLong))
                .zoom(zoom)
                .build();
        mappingFragment = MapFragment.newInstance(new GoogleMapOptions().camera(cameraPosition));
        mappingFragment.getMapAsync(this);

        switchTabs(scheduleButton);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void onMapReady(GoogleMap googleMap){
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setBuildingsEnabled(true);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            googleMap.setMyLocationEnabled(true);
        }

        TypedValue out = new TypedValue();
        res.getValue(R.raw.unit_lat, out, true);
        final float unitLat = out.getFloat();
        res.getValue(R.raw.unit_lng, out, true);
        final float unitLong = out.getFloat();
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(unitLat, unitLong)).title(res.getString(R.string.unit_name)));

        res.getValue(R.raw.field_lat, out, true);
        final float fieldLat = out.getFloat();
        res.getValue(R.raw.field_lng, out, true);
        final float fieldLong = out.getFloat();
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(fieldLat, fieldLong)).title(res.getString(R.string.field_name)));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(res.getString(R.string.permission_request_title))
                        .setMessage(res.getString(R.string.permission_request_message))
                        .setPositiveButton(res.getString(R.string.permission_request_button),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void switchTabs(View view){
        scheduleButton.setAlpha(TAB_FADE);
        newsButton.setAlpha(TAB_FADE);
        mapButton.setAlpha(TAB_FADE);
        rideButton.setAlpha(TAB_FADE);
        settingsButton.setAlpha(SETTINGS_FADE);
        view.setAlpha(1);
        scheduleButton.setBackgroundColor(res.getColor(R.color.transparent));
        newsButton.setBackgroundColor(res.getColor(R.color.transparent));
        mapButton.setBackgroundColor(res.getColor(R.color.transparent));
        rideButton.setBackgroundColor(res.getColor(R.color.transparent));
        settingsButton.setBackgroundColor(res.getColor(R.color.transparent));
        view.setBackgroundColor(res.getColor(R.color.selected_tab_fade));
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch(view.getId()){
            case R.id.schedule_button: transaction.replace(R.id.fragment_container, scheduleFragment);
            case R.id.news_button: transaction.replace(R.id.fragment_container, newsFragment);
            case R.id.map_button: transaction.replace(R.id.fragment_container, mappingFragment);
            case R.id.ride_button: transaction.replace(R.id.fragment_container, rideFragment);
            case R.id.settings_button: transaction.replace(R.id.fragment_container, settingsFragment);
        }
        transaction.commit();
    }
}
