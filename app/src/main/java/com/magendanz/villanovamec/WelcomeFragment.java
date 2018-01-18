package com.magendanz.villanovamec;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing the settings page used to upload MecItems to the server
 */
public class WelcomeFragment extends Fragment {

    private SwipeRefreshLayout refreshView;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        refreshView = rootView.findViewById(R.id.refresh_container);
        refreshView.setOnRefreshListener(refreshListener);
        refreshView.post(new Runnable() {
            @Override
            public void run() {
                refreshView.setRefreshing(true);
            }
        });
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
     * initialize the data elements and UI of the fragment
     *
     * @param refreshListener the listener used to define response to a refresh swipe
     */
    public void setUp(SwipeRefreshLayout.OnRefreshListener refreshListener) {
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
                    refreshView.setRefreshing(false);
                }
            });
        }
    }
}
