package com.magendanz.villanovamec;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

/**
 * A fragment representing the settings page used to upload MecItems to the server
 */
public class EditEntryFragment extends Fragment {

    View view;
    List<MecItem> spinnerList;
    List<MecItem> locationsList;
    String header;
    AdapterView.OnItemSelectedListener listener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_entry, container, false);
        ((TextView)view.findViewById(R.id.settings_title_field)).setText(header);
        ((Spinner)view.findViewById(R.id.existing_entry_field)).setOnItemSelectedListener(listener);
        ((RadioGroup)view.findViewById(R.id.radio_group)).clearCheck();
        view.findViewById(R.id.select_section).setVisibility(View.GONE);
        view.findViewById(R.id.entry_fields_section).setVisibility(View.GONE);
        view.findViewById(R.id.location_fields_section).setVisibility(View.GONE);
        view.findViewById(R.id.add_entry_button).setVisibility(View.GONE);
        view.findViewById(R.id.edit_entry_button).setVisibility(View.GONE);
        view.findViewById(R.id.delete_entry_button).setVisibility(View.GONE);
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

    public void setListener(AdapterView.OnItemSelectedListener newListener) {
        listener = newListener;
    }

    public void setStyle(String header, List<MecItem> mecItems, List<MecItem> locations) {
        spinnerList = mecItems;
        locationsList = locations;
        this.header = header;
    }

    public void initializeSpinner() {
        ArrayAdapter<MecItem> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                spinnerList
        );
        ArrayAdapter<MecItem> locAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                locationsList
        );
        ((Spinner)view.findViewById(R.id.existing_entry_field)).setAdapter(adapter);
        ((Spinner)view.findViewById(R.id.location_field)).setAdapter(locAdapter);
    }
}
