package com.magendanz.villanovamec;

import android.Manifest;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.SmsManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int MY_PERMISSIONS_REQUEST_SMS = 49;
    private static final float TAB_FADE = .5f;
    private static final float SETTINGS_FADE = .1f;

    private View scheduleButton;
    private View newsButton;
    private View mapButton;
    private View rideButton;
    private View settingsButton;
    private MecListFragment scheduleFragment;
    private MecListFragment newsFragment;
    private MapFragment mappingFragment;
    private RideFragment rideFragment;
    private WelcomeFragment welcomeFragment;
    private SettingsFragment settingsFragment;
    private EditEntryFragment editEntryFragment;
    private POCFragment pocFragment;
    private Resources res;
    private long settingsPresses = 0;
    private long settingsPressTime = 0;
    private int settingsSection;
    public boolean loading = false;

    private MecItem pointOfContact;
    private List<MecItem> calendarItems;
    private List<MecItem> noticeItems;
    private List<MecItem> locationItems;

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
        scheduleFragment = new MecListFragment();
        newsFragment = new MecListFragment();
        welcomeFragment = new WelcomeFragment();
        rideFragment = new RideFragment();
        settingsFragment = new SettingsFragment();
        pocFragment = new POCFragment();

        // TODO(magendanz) move settings edit behavior into fragment
        editEntryFragment = new EditEntryFragment();
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showFields(view);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
        editEntryFragment.setListener(listener);

        pointOfContact = new MecItem();
        calendarItems = new ArrayList<>();
        noticeItems = new ArrayList<>();
        locationItems = new ArrayList<>();
        pocFragment.addPOC(pointOfContact);
        pocFragment.setLocationsPOC(locationItems);
        rideFragment.setLocationsList(locationItems);

        // Setup refresh listener which triggers new data loading
        SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                loading = true;
                loadContent();
            }
        };
        scheduleFragment.setUp(res.getString(R.string.schedule_title), refreshListener);
        newsFragment.setUp(res.getString(R.string.news_title), refreshListener);
        welcomeFragment.setUp(refreshListener);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, welcomeFragment);
        transaction.commit();

        loading = true;
        loadContent();

        // initialize map frame
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
    }

    //------------------------------- Load Map -------------------------------------

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
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

        addMarkers();
    }

    public void addMarkers() {
        if (mGoogleMap != null) {
            for (int i = 0; i < locationItems.size(); i++) {
                try {
                    String name = locationItems.get(i).getTitle();
                    float lat = Float.parseFloat(locationItems.get(i).getMainField());
                    float lon = Float.parseFloat(locationItems.get(i).getSecondaryField());
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon)).title(name));
                } catch (NumberFormatException e) {
                    // TODO(magendanz) handle this error instead of just dropping it
                    System.err.println("Incorrectly formatted location coordinates");
                }
            }
        }
    }

    /**
     * build the API client once permissions have been granted
     */
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
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (IllegalStateException e) {
                System.err.println(e);
            }
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

    /**
     * Check that we have permission to access the location
     */
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

    /**
     * Check that we have permission to access the sms
     */
    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(res.getString(R.string.permission_request_title_sms))
                        .setMessage(res.getString(R.string.permission_request_message_sms))
                        .setPositiveButton(res.getString(R.string.permission_request_button_sms),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Prompt the user once explanation has been shown
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.SEND_SMS},
                                                MY_PERMISSIONS_REQUEST_SMS );
                                    }
                                })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SMS );
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
            case MY_PERMISSIONS_REQUEST_SMS: {

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //---------------------------- Refresh and Database -------------------------------------

    /**
     * Load the server responses for DynamoDB table
     */
    private void loadContent(){
        // load items from AWS table
        Runnable runnable = new Runnable() {
            public void run() {
                // Initialize the Amazon Cognito credentials provider
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-east-1:971621f7-8ea0-4211-809c-040ed268b0be", // Identity pool ID
                        Regions.US_EAST_1 // Region
                );
                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                PaginatedScanList<MecItem> result = mapper.scan(MecItem.class, scanExpression);
                addResponses(result);

                // notify fragment UIs that refresh is done and to reload
                scheduleFragment.refreshDone();
                newsFragment.refreshDone();
                welcomeFragment.refreshDone();
                loading = false;
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }


    /**
     * @param responses the MecItems to add into the different Mec lists
     */
    private void addResponses(List<MecItem> responses){
        noticeItems.clear();
        calendarItems.clear();
        locationItems.clear();
        for(MecItem response : responses){
            switch (response.getMecItemType()) {
                case 0:
                    pointOfContact = response;
                    pocFragment.addPOC(pointOfContact);
                    break;
                case 1:
                    locationItems.add(response);
                    break;
                case 2:
                    calendarItems.add(response);
                    break;
                case 3:
                    noticeItems.add(response);
                    break;
                default:
            }
        }
        Collections.sort(noticeItems);
        Collections.sort(calendarItems);
        newsFragment.addElements(noticeItems);
        scheduleFragment.addElements(calendarItems);
    }

    // ---------------------------- OnClick Buttons -----------------------------------

    /**
     * Switch to the new fragment specified by the calling button
     *
     * @param view the button that activated the switch
     */
    public void switchTabs(View view){
        if (loading) {
            return;
        }
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
            case R.id.schedule_button:
                transaction.replace(R.id.fragment_container, scheduleFragment);
                break;
            case R.id.news_button:
                transaction.replace(R.id.fragment_container, newsFragment);
                break;
            case R.id.map_button:
                if (mGoogleMap != null) {
                    mGoogleMap.clear();
                    addMarkers();
                }
                transaction.replace(R.id.fragment_container, mappingFragment);
                mappingFragment.getMapAsync(this);
                break;
            case R.id.ride_button:
                transaction.replace(R.id.fragment_container, rideFragment);
                break;
            case R.id.settings_button:
                transaction.replace(R.id.fragment_container, settingsFragment);
                break;
            default:
                System.err.println("View not identified");
        }
        transaction.commit();
    }

    /**
     * Switch to the settings tab only when the settings button has been clicked 7 times in rapid
     * succession
     *
     * @param view
     */
    public void switchTabsSettings(View view) {
        long currentTime = android.os.SystemClock.elapsedRealtime();
        if (settingsPresses >= 7) {
            settingsPresses = 0;
            switchTabs(view);
        } else if (settingsPresses == 0 || currentTime - settingsPressTime < 2000) {
            settingsPresses++;
            settingsPressTime = currentTime;
        } else {
            settingsPresses = 0;
        }
    }

    /**
     * Switch between the different settings operational pages
     *
     * @param view
     */
    public void switchSettings(View view) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch(view.getId()){
            case R.id.change_calendar_button:
                settingsSection = MecItem.CalendarItem;
                editEntryFragment.setStyle(getResources().getString(R.string.change_calendar_label), calendarItems, locationItems);
                transaction.replace(R.id.fragment_container, editEntryFragment);
                break;
            case R.id.change_notice_button:
                settingsSection = MecItem.NoticeItem;
                editEntryFragment.setStyle(getResources().getString(R.string.change_notice_label), noticeItems, locationItems);
                transaction.replace(R.id.fragment_container, editEntryFragment);
                break;
            case R.id.change_location_button:
                settingsSection = MecItem.LocationItem;
                editEntryFragment.setStyle(getResources().getString(R.string.change_location_label), locationItems, locationItems);
                transaction.replace(R.id.fragment_container, editEntryFragment);
                break;
            default:
                transaction.replace(R.id.fragment_container, pocFragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // --------------------------------- Ride Controls ---------------------------------------

    /**
     * Send an SMS message to the POC with given message fields
     *
     * @param view
     */
    public void submitMessage(View view) {
        // get SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            checkSMSPermission();
            // don't send sms if permissions not given
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        String message = "MEC Message\nPoint Of Contact: ";
        message += pointOfContact.getMainField() + "\n";
        message += "From: " + ((EditText)findViewById(R.id.question_name_field)).getText() + "\n\n";
        if (((CheckBox)findViewById(R.id.ride_toggle)).isChecked()) {
            message += "Ride Request\n";
            message += "From: " + ((Spinner)findViewById(R.id.pickup_field)).getSelectedItem() + "\n";
            message += "To: " + ((Spinner)findViewById(R.id.dropoff_field)).getSelectedItem() + "\n\n";
        }
        message += ((EditText)findViewById(R.id.message_field)).getText();

        // send message
        SmsManager smsManager = SmsManager.getDefault();
        // if message length is too long messages are divided
        List<String> messages = smsManager.divideMessage(message);
        for (String msg : messages) {
            smsManager.sendTextMessage(pointOfContact.getSecondaryField(), null, message, null, null);
        }
        Toast.makeText(getApplicationContext(), "Message Sent",
                Toast.LENGTH_LONG).show();
    }

    // ---------------------------- Settings Item Controls -----------------------------------

    /**
     * Send new MecItem to the DynamoDB table and switch back to the main settings page
     *
     * @param view
     */
    public void updateEntry(View view) {
        final MecItem newItem;
        if (view.getId() == R.id.edit_entry_button) {
            newItem = (MecItem)((Spinner)findViewById(R.id.existing_entry_field)).getSelectedItem();
            deleteMecEntry(newItem);
        } else {
            newItem = new MecItem();
        }
        if (settingsSection == MecItem.LocationItem) {
            newItem.setTitle(((EditText)findViewById(R.id.name_field)).getText().toString());
            newItem.setMainField(((EditText)findViewById(R.id.lat_field)).getText().toString());
            newItem.setSecondaryField(((EditText)findViewById(R.id.long_field)).getText().toString());
        } else {
            newItem.setMainField(((EditText)findViewById(R.id.name_field)).getText().toString());
            newItem.setSecondaryField(((EditText)findViewById(R.id.description_field)).getText().toString());
            newItem.setLocation(((Spinner)findViewById(R.id.location_field)).getSelectedItem().toString());
            newItem.setMecItemType(settingsSection);
            newItem.setTime(
                    ((EditText)findViewById(R.id.from_month_entry)).getText().toString(),
                    ((EditText)findViewById(R.id.from_day_entry)).getText().toString(),
                    ((EditText)findViewById(R.id.from_time_entry)).getText().toString(),
                    ((EditText)findViewById(R.id.to_month_entry)).getText().toString(),
                    ((EditText)findViewById(R.id.to_day_entry)).getText().toString(),
                    ((EditText)findViewById(R.id.to_time_entry)).getText().toString()
            );
            newItem.setTitle(newItem.getMainField() + " (" + newItem.getTime() + ")");
        }

        saveMecEntry(newItem);
        endSettingsChange();
    }

    /**
     * Update the point of contact and upload to DynamoDB table
     *
     * @param view
     */
    public void updatePOC(View view) {
        String number = ((EditText)findViewById(R.id.poc_number_field)).getText().toString();
        try {
            long temp = Long.parseLong(number);
            if (number.length() != 10) {
                throw new NumberFormatException();
            }
            pointOfContact.setMainField(
                    ((EditText)findViewById(R.id.poc_name_field)).getText().toString());
            pointOfContact.setSecondaryField(
                    ((EditText)findViewById(R.id.poc_number_field)).getText().toString());
            pointOfContact.setLocation(
                    ((Spinner)findViewById(R.id.poc_location_field)).getSelectedItem().toString());
        } catch (NumberFormatException e) {
            System.err.println("Invalid phone number for point of contact.");
        }
        saveMecEntry(pointOfContact);
        endSettingsChange();
    }

    /**
     * Delete the selected entry in the settings spinner
     *
     * @param view
     */
    public void deleteEntry(View view) {
        final MecItem item = (MecItem)((Spinner)findViewById(R.id.existing_entry_field)).getSelectedItem();
        deleteMecEntry(item);
        switch (settingsSection) {
            case MecItem.CalendarItem:
                calendarItems.remove(item);
                break;
            case MecItem.LocationItem:
                locationItems.remove(item);
                break;
            case MecItem.NoticeItem:
                noticeItems.remove(item);
                break;
        }
        endSettingsChange();
    }

    /**
     * switch back to settings main screen
     */
    private void endSettingsChange() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, settingsFragment);
        transaction.commit();
    }

    /**
     * @param entry the MecItem to tell the DynamoDB table to save/edit
     */
    private void saveMecEntry(final MecItem entry) {
        new Thread(new Runnable() {
            public void run() {
                // Initialize the Amazon Cognito credentials provider
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-east-1:971621f7-8ea0-4211-809c-040ed268b0be", // Identity pool ID
                        Regions.US_EAST_1 // Region
                );
                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
                mapper.save(entry);
            }
        }).start();
    }

    /**
     * @param entry the MecItem to tell the DynamoDB table to delete
     */
    private void deleteMecEntry(final MecItem entry) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Initialize the Amazon Cognito credentials provider
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-east-1:971621f7-8ea0-4211-809c-040ed268b0be", // Identity pool ID
                        Regions.US_EAST_1 // Region
                );
                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
                mapper.delete(entry);
            }
        }).start();
    }

    /**
     * Show the correct field type for editing a MecItem. Called after add selected or spinner item selected
     *
     * @param view
     */
    public void showFields(View view) {
        // delete only needs spinner
        if (((RadioButton)findViewById(R.id.delete_radio)).isChecked()) {
            findViewById(R.id.delete_entry_button).setVisibility(View.VISIBLE);
        } else if (((RadioButton)findViewById(R.id.edit_radio)).isChecked()) {
            // populate fields for editing
            MecItem currentItem  = (MecItem)((Spinner)findViewById(R.id.existing_entry_field)).getSelectedItem();
            // populate location or generic entry
            if (settingsSection == MecItem.LocationItem) {
                ((EditText)findViewById(R.id.loc_title_field)).setText(currentItem.getTitle());
                ((EditText)findViewById(R.id.lat_field)).setText(currentItem.getMainField());
                ((EditText)findViewById(R.id.long_field)).setText(currentItem.getSecondaryField());
                findViewById(R.id.location_fields_section).setVisibility(View.VISIBLE);
            } else {
                ((EditText)findViewById(R.id.name_field)).setText(currentItem.getMainField());
                ((EditText)findViewById(R.id.description_field)).setText(currentItem.getSecondaryField());
                ((EditText)findViewById(R.id.from_month_entry)).setText(currentItem.getStartMonth());
                ((EditText)findViewById(R.id.from_day_entry)).setText(currentItem.getStartDay());
                ((EditText)findViewById(R.id.from_time_entry)).setText(currentItem.getStartTime());
                ((EditText)findViewById(R.id.to_month_entry)).setText(currentItem.getEndMonth());
                ((EditText)findViewById(R.id.to_day_entry)).setText(currentItem.getEndDay());
                ((EditText)findViewById(R.id.to_time_entry)).setText(currentItem.getEndTime());
                ((Spinner)findViewById(R.id.location_field)).setSelection(locationItems.indexOf(currentItem));
                findViewById(R.id.entry_fields_section).setVisibility(View.VISIBLE);
            }
            findViewById(R.id.edit_entry_button).setVisibility(View.VISIBLE);
        } else {
            // show empty sections for adding new MecItem
            if (settingsSection == MecItem.LocationItem) {
                findViewById(R.id.location_fields_section).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.entry_fields_section).setVisibility(View.VISIBLE);
            }
            findViewById(R.id.add_entry_button).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show the spinner for selecting existing MecItems
     *
     * @param view
     */
    public void showEntrySelect(View view) {
        findViewById(R.id.select_section).setVisibility(View.GONE);
        findViewById(R.id.entry_fields_section).setVisibility(View.GONE);
        findViewById(R.id.location_fields_section).setVisibility(View.GONE);
        findViewById(R.id.add_entry_button).setVisibility(View.GONE);
        findViewById(R.id.edit_entry_button).setVisibility(View.GONE);
        findViewById(R.id.delete_entry_button).setVisibility(View.GONE);
        editEntryFragment.initializeSpinner();
        if (view.getId() == R.id.add_radio) {
            showFields(view);
        } else {
            view.getRootView().findViewById(R.id.select_section).setVisibility(View.VISIBLE);
        }
    }
}
