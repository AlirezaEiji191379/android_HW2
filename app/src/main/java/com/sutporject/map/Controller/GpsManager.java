package com.sutporject.map.Controller;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.sutporject.map.MapFragment;
import com.sutporject.map.R;

public class GpsManager extends BroadcastReceiver {
    private MapFragment fragment;

    public GpsManager(MapFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==true){
            fragment.getMapboxMap().getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    fragment.showCurrentLocation(style);
                    fragment.getMapboxMap().addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public boolean onMapClick(@NonNull LatLng point) {
                            Toast.makeText(context, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();
                            fragment.getMapboxMap().clear();
                            PointF pixel =fragment.getMapboxMap().getProjection().toScreenLocation(point);
                            //Toast.makeText(getContext(), String.format("User clicked at: %s", pixel), Toast.LENGTH_LONG).show();
                            IconFactory iconFactory = IconFactory.getInstance(context);
                            Icon icon = iconFactory.fromResource(R.drawable.choose_location_icon);
                            fragment.getMapboxMap().addMarker(new MarkerOptions().position(point).icon(icon));
                            return true;
                        }
                    });
                }
            });
        }
    }
}
