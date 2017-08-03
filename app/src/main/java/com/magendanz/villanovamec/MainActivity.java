package com.magendanz.villanovamec;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class MainActivity extends FragmentActivity {

    private static final float TAB_FADE = .8f;

    private ScheduleFragment scheduleFragment;
    private NewsFragment newsFragment;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleFragment = new ScheduleFragment();
        newsFragment = new NewsFragment();
        mapFragment = new MapFragment();
        findViewById(R.id.schedule_button).setAlpha(1);
        findViewById(R.id.news_button).setAlpha(TAB_FADE);
        findViewById(R.id.map_button).setAlpha(TAB_FADE);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, scheduleFragment).commit();
    }

    public void switchToMap(View view){
        findViewById(R.id.map_button).setAlpha(1);
        findViewById(R.id.news_button).setAlpha(TAB_FADE);
        findViewById(R.id.schedule_button).setAlpha(TAB_FADE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mapFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void switchToNews(View view){
        findViewById(R.id.news_button).setAlpha(1);
        findViewById(R.id.map_button).setAlpha(TAB_FADE);
        findViewById(R.id.schedule_button).setAlpha(TAB_FADE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void switchToSchedule(View view){
        findViewById(R.id.schedule_button).setAlpha(1);
        findViewById(R.id.map_button).setAlpha(TAB_FADE);
        findViewById(R.id.news_button).setAlpha(TAB_FADE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, scheduleFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
