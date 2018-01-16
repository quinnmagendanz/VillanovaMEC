package com.magendanz.villanovamec;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

/**
 * A fragment representing the settings page used to upload MecItems to the server
 */
public class POCFragment extends Fragment {

    private List<MecItem> locationList;
    private MecItem pointOfContact;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_poc, container, false);
        ArrayAdapter<MecItem> pocLocAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                locationList
        );
        ((Spinner)view.findViewById(R.id.poc_location_field)).setAdapter(pocLocAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int i;
        for (i = locationList.size(); i > 0; i--) {
            if (locationList.get(i-1).getTitle() == pointOfContact.getLocation()) {
                i--;
                break;
            }
        }
        ((Spinner)view.findViewById(R.id.poc_location_field)).setSelection(i);
        ((EditText)view.findViewById(R.id.poc_name_field)).setText(pointOfContact.getMainField());
        ((EditText)view.findViewById(R.id.poc_number_field)).setText(pointOfContact.getSecondaryField());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setLocationsPOC(List<MecItem> oldList) {
        locationList = oldList;
    }

    public void addPOC(MecItem poc) {
        pointOfContact = poc;
    }
}
