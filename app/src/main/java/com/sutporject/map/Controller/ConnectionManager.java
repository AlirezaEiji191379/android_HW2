package com.sutporject.map.Controller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.maps.Style;
import com.sutporject.map.MapFragment;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class ConnectionManager extends BroadcastReceiver{
    private MapFragment fragment;

    public ConnectionManager(MapFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected= cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        if(isConnected==false) Toast.makeText(context,"کاربر گرامی لطفا اتصال اینترنت را برقرار نمایید",Toast.LENGTH_LONG).show();
    }
}
