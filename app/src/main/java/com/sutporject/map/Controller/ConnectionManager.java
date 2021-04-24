package com.sutporject.map.Controller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class ConnectionManager extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected= cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        Log.i("reza","hi");
        if(isConnected==false) Toast.makeText(context,"کاربر گرامی لطفا اتصال اینترنت را برقرار نمایید",Toast.LENGTH_LONG).show();
    }
}
