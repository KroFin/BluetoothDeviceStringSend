package com.example.bluetoothdevicestringsend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter {

    private ArrayList<BluetoothDeviceWhichBonded> mData;
    private Context mContext;

    public MyAdapter (ArrayList<BluetoothDeviceWhichBonded> mData , Context mContext){
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RecyclerView.ViewHolder holder;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview,parent,false);
        TextView txt_DeviceName = (TextView) convertView.findViewById(R.id.DeviceName);
        TextView txt_DeviceAddress = (TextView) convertView.findViewById(R.id.DeviceAddress);
        txt_DeviceName.setText(mData.get(position).getDeviceName());
        txt_DeviceAddress.setText(mData.get(position).getAddress());
        return convertView;
    }
}
