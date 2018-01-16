package com.magendanz.villanovamec;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing the ride page that requests a ride by generating a text message
 * and exporting it to the messaging app
 */
public class RideFragment extends Fragment {

    private String name;
    private String location;
    private String phoneNumber;
    private List<MecItem> locationsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = "Michelle Mathews";
        location = "NROTC Unit";
        phoneNumber = "9852372393";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_ride, container, false);

        // listener to show ride fields when checkbox marked
        CheckBox box = (CheckBox)view.findViewById(R.id.ride_toggle);
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = ((CheckBox)v).isChecked() ? View.VISIBLE : View.GONE;
                view.findViewById(R.id.ride_field_section).setVisibility(visibility);
            }
        });
        box.setChecked(false);
        view.findViewById(R.id.ride_field_section).setVisibility(View.GONE);

        List<String> displayList = new ArrayList<>();
        for(MecItem p : locationsList) {
            displayList.add(p.getTitle());
        }
        displayList.add("Other (Specify below)");

        // spinner adapters
        ArrayAdapter<String> pickupAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                displayList
        );
        ArrayAdapter<String> dropoffAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                displayList
        );
        ((Spinner)view.findViewById(R.id.pickup_field)).setAdapter(pickupAdapter);
        ((Spinner)view.findViewById(R.id.dropoff_field)).setAdapter(dropoffAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setLocationsList(List<MecItem> oldList) {
        locationsList = oldList;
    }
}
