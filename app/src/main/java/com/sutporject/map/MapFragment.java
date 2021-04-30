package com.sutporject.map;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.sutporject.map.Controller.ConnectionManager;
import com.sutporject.map.Controller.GpsManager;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private GpsManager gpsManager;
    //private ConnectionManager connectionManager;
    public MapboxMap getMapboxMap() {
        return mapboxMap;
    }

    public MapFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //connectionManager=new ConnectionManager(this);
        gpsManager=new GpsManager(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        ViewGroup root=(ViewGroup)inflater.inflate(R.layout.fragment_map, container, false);
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return root;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        MapFragment.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(//new Style.Builder().fromUri("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7")
                Style.MAPBOX_STREETS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        showCurrentLocation(style);
                            mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                                @Override
                                public boolean onMapLongClick(@NonNull LatLng point) {
                                    Toast.makeText(getContext(), String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();
                                    mapboxMap.clear();
                                    IconFactory iconFactory = IconFactory.getInstance(getActivity());
                                    Icon icon = iconFactory.fromResource(R.drawable.user_location_icon);
                                    mapboxMap.addMarker(new MarkerOptions().position(point).icon(icon));
                                    return false;
                                }
                            });
                    }
                });
    }

    public void showCurrentLocation(Style mapStyle){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {

//            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(getActivity())
//                    .elevation(5)
//                    .accuracyAlpha(.6f)
//                    .accuracyColor(Color.TRANSPARENT)
//                    .foregroundDrawable(R.drawable.user_location_icon)
//                    .build();
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
//            LocationComponentActivationOptions locationComponentActivationOptions =
//                    LocationComponentActivationOptions.builder(getActivity(), mapStyle)
//                            .locationComponentOptions(customLocationComponentOptions)
//                            .build();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getActivity(), mapStyle).build());
            //locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            getActivity().findViewById(R.id.get_location).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    locationComponent.setCameraMode(CameraMode.TRACKING);
                    locationComponent.zoomWhileTracking(40f);
                }
            });

        } else {
            requestPermissions(new String []{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100 && grantResults.length>0){
            if(grantResults[0]==0){
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        showCurrentLocation(style);
                    }
                });
            }else{
                Toast.makeText(getActivity(),"برای گرفتن مکان دستگاه نیاز به دسترسی به Location دارد.",Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        //IntentFilter intentFilter=new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        //getActivity().registerReceiver(connectionManager,intentFilter);
        IntentFilter intentFilter1=new IntentFilter("android.location.PROVIDERS_CHANGED");
        getActivity().registerReceiver(gpsManager,intentFilter1);
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        //getActivity().unregisterReceiver(connectionManager);
        getActivity().unregisterReceiver(gpsManager);
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }




}