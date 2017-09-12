package com.magendanz.villanovamec;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MecListFragment extends Fragment {

    private MecListAdapter listAdapter;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new MecListAdapter(getActivity(), new ArrayList<MecItem>());
        name = "Mec List";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mec_list, container, false);
        ((TextView) rootView.findViewById(R.id.list_title_box)).setText(name);
        ((ListView) rootView.findViewById(R.id.schedule_list)).setAdapter(listAdapter);
        return rootView;
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
     * @param newItem a new MecItem to add into the listview
     */
    public void addElement(MecItem newItem){
        listAdapter.add(newItem);
    }

    /**
     * remove all rows in the list
     */
    public void clearRows(){
        listAdapter.removeAll();
    }

    /**
     * @param name the title to display at the top of the list
     */
    public void setName(String name){
        this.name = name;
    }
}
