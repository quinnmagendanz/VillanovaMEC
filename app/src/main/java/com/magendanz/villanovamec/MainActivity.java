package com.magendanz.villanovamec;

import android.app.FragmentManager;
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

    private ScheduleFragment scheduleFragment;
    private NewsFragment newsFragment;
    private MapFragment mappingFragment;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = getResources();
        scheduleFragment = new ScheduleFragment();
        newsFragment = new NewsFragment();
        mappingFragment = MapFragment.newInstance();
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
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void switchToNews(View view){
        setTabFormat(view);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void switchToSchedule(View view){
        setTabFormat(view);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, scheduleFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setTabFormat(View view){
        findViewById(R.id.schedule_button).setAlpha(TAB_FADE);
        findViewById(R.id.map_button).setAlpha(TAB_FADE);
        findViewById(R.id.news_button).setAlpha(TAB_FADE);
        view.setAlpha(1);
        findViewById(R.id.schedule_button_frame).setBackgroundColor(res.getColor(R.color.transparent));
        findViewById(R.id.map_button_frame).setBackgroundColor(res.getColor(R.color.transparent));
        findViewById(R.id.news_button_frame).setBackgroundColor(res.getColor(R.color.transparent));
        ((View) view.getParent()).setBackgroundColor(res.getColor(R.color.selected_tab_fade));
    }

}
