package com.magendanz.villanovamec;

import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float TAB_FADE = .5f;

    private View scheduleButton;
    private View newsButton;
    private View mapButton;
    private View rideButton;

    private ScheduleFragment scheduleFragment;
    private NewsFragment newsFragment;
    private MapFragment mappingFragment;
    private RideFragment rideFragment;
    private SettingsFragment settingsFragment;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleButton = findViewById(R.id.schedule_button);
        newsButton = findViewById(R.id.map_button);
        mapButton = findViewById(R.id.news_button);
        rideButton = findViewById(R.id.ride_button);
        res = getResources();
        scheduleFragment = new ScheduleFragment();
        newsFragment = new NewsFragment();
        mappingFragment = MapFragment.newInstance();
        rideFragment = new RideFragment();
        settingsFragment = new SettingsFragment();
        setTabFormat(findViewById(R.id.schedule_button));
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, scheduleFragment);
        fragmentTransaction.commit();
    }

    public void onMapReady(GoogleMap googleMap){
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(res.getDimension(R.dimen.villanova_lat), res.getDimension(R.dimen.villanova_lng)))
                .title("Marker"));
    }

    public void switchToMap(View view){
        setTabFormat(view);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mappingFragment);
        transaction.commit();
    }

    public void switchToNews(View view){
        setTabFormat(view);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newsFragment);
        transaction.commit();
    }

    public void switchToSchedule(View view){
        setTabFormat(view);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, scheduleFragment);
        transaction.commit();
    }

    public void switchToRide(View view){
        setTabFormat(view);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, rideFragment);
        transaction.commit();
    }

    private void setTabFormat(View view){
        scheduleButton.setAlpha(TAB_FADE);
        newsButton.setAlpha(TAB_FADE);
        mapButton.setAlpha(TAB_FADE);
        rideButton.setAlpha(TAB_FADE);
        view.setAlpha(1);
        scheduleButton.setBackgroundColor(res.getColor(R.color.transparent));
        newsButton.setBackgroundColor(res.getColor(R.color.transparent));
        mapButton.setBackgroundColor(res.getColor(R.color.transparent));
        rideButton.setBackgroundColor(res.getColor(R.color.transparent));
        view.setBackgroundColor(res.getColor(R.color.selected_tab_fade));
    }

    public void switchToSettings(View view){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, settingsFragment);
        transaction.commit();
        scheduleButton.setAlpha(TAB_FADE);
        newsButton.setAlpha(TAB_FADE);
        mapButton.setAlpha(TAB_FADE);
        scheduleButton.setBackgroundColor(res.getColor(R.color.transparent));
        newsButton.setBackgroundColor(res.getColor(R.color.transparent));
        mapButton.setBackgroundColor(res.getColor(R.color.transparent));
    }
}
