package com.magendanz.villanovamec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Data adapter for use in all lists of Mecitems
 */

public class MecListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<MecItem> mDataSource;

    public MecListAdapter(Context context, ArrayList<MecItem> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_mec_item_layout, parent, false);
        MecItem item = (MecItem) getItem(position);
        ((TextView) rowView.findViewById(R.id.title_text_box)).setText(item.getTitle());
        ((TextView) rowView.findViewById(R.id.tag_text_box)).setText(item.getLocation());
        ((TextView) rowView.findViewById(R.id.details_text_box)).setText(item.getDetails());
        return rowView;
    }

    /**
     * @param newItem the new MecItem to add to the adapter data
     */
    public void add(MecItem newItem){
        mDataSource.add(newItem);
    }

    /**
     * remove all elements in the data adapter
     */
    public void removeAll(){
        mDataSource = new ArrayList<>();
    }
}
