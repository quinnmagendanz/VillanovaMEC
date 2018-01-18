package com.magendanz.villanovamec;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MecListFragment extends Fragment {

    private List<MecItem> itemList;
    private SwipeRefreshLayout refreshView;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    private MecListAdapter listAdapter;
    private String name = "Mec List";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new MecListAdapter(getActivity(), itemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mec_list, container, false);
        ((TextView) rootView.findViewById(R.id.list_title_box)).setText(name);
        ((ListView) rootView.findViewById(R.id.schedule_list)).setAdapter(listAdapter);
        refreshView = rootView.findViewById(R.id.refresh_container);
        refreshView.setOnRefreshListener(refreshListener);
        refreshView.setColorSchemeResources(R.color.novaBlue, R.color.novaGrey, R.color.novaDark, R.color.novaOffWhite);
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
    public void addElements(List<MecItem> newItem){
        itemList = newItem;
    }

    /**
     * initialize the data elements and UI of the fragment
     *
     * @param name the title to display at the top of the list
     * @param refreshListener the listener used to define response to a refresh swipe
     */
    public void setUp(String name, SwipeRefreshLayout.OnRefreshListener refreshListener){
        this.name = name;
        itemList = new ArrayList<>();
        this.refreshListener = refreshListener;
    }

    /**
     * notify UI elements when refresh has been completed
     */
    public void refreshDone() {
        if (refreshView != null) {
            refreshView.post(new Runnable() {
                @Override
                public void run() {
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    refreshView.setRefreshing(false);
                }
            });
        }
    }
}
