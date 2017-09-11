package com.magendanz.villanovamec;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing the ride page that requests a ride by generating a text message
 * and exporting it to the messaging app
 */
public class RideFragment extends Fragment {

    private String name;
    private String location;
    private String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = "Michelle Mathews";
        location = "NROTC Unit";
        phoneNumber = "9852372393";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     *
     * @param name the name of the person to route all questions and rides through
     * @param location the location that the POC is located
     * @param phoneNumber the phone number of the POC. Should be a string of 10 integers
     */
    public void setContactInfo(String name, String location, String phoneNumber){

    }
}
