package com.ranjutech.codat.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ranjutech.codat.R;

import java.util.HashMap;

public class ListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    HashMap<String,String> mData;
    private String[] mKeys;

    public ListAdapter(Context context,HashMap<String,String> data){
        this.mContext=context;
        this.mData=data;
        mKeys = mData.keySet().toArray(new String[mData.size()]);
        mInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView=mInflater.inflate(R.layout.layout,null );

        TextView regionNameTextView=convertView.findViewById(R.id.regionNameTextView);
        TextView regionDataTextView=convertView.findViewById(R.id.regionDataTextView);

        String name = mKeys[position];
        String Value = getItem(position).toString();

        regionNameTextView.setText(name);
        regionDataTextView.setText(Value);

        return convertView;
    }
}
