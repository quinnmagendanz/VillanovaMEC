package com.magendanz.villanovamec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Data adapter for use in all lists of Mecitems
 */

public class MecListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MecItem> mDataSource;

    public MecListAdapter(Context context, List<MecItem> items) {
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
        int day = Integer.parseInt(item.getStartDay());
        int backColor = day%2 == 0
                ? mContext.getResources().getColor(R.color.novaLightGrey)
                : mContext.getResources().getColor(R.color.novaOffWhite);
        rowView.setBackgroundColor(backColor);
        rowView.findViewById(R.id.day_box).setBackgroundColor(backColor - 0x101010);
        ((TextView) rowView.findViewById(R.id.title_text_box)).setText(item.getMainField());
        ((TextView) rowView.findViewById(R.id.description_text_box)).setText(item.getSecondaryField());
        ((TextView) rowView.findViewById(R.id.time_text_box)).setText(item.getStartTime() + " - " + item.getEndTime());
        ((TextView) rowView.findViewById(R.id.day_box)).setText(item.getStartDay() + " " + item.getStartMonth());
        ((TextView) rowView.findViewById(R.id.location_text_box)).setText(item.getLocation());
        return rowView;
    }
}
