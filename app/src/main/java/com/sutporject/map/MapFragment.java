package com.sutporject.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.sutporject.map.Controller.GpsManager;
import com.sutporject.map.Controller.SearchController;
import com.sutporject.map.Model.SearchedPoint;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapFragment extends Fragment implements OnMapReadyCallback,LocationListener {

    private TextView speed;
    private MapView mapView;
    private SpeechRecognizer speechRecognizer;
    private MapboxMap mapboxMap;
    private GpsManager gpsManager;
    private SearchController searchController;
    private ExecutorService executorService;
    ArrayList<SearchedPoint> allApiReturned=new ArrayList<>();
    ArrayAdapter<SearchedPoint> adapter;
    AutoCompleteTextView search_location;
    private  Intent speechIntent;
    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                Toast.makeText(getActivity(),R.string.internet_connection,Toast.LENGTH_LONG).show();
            }
            else{
                allApiReturned= (ArrayList<SearchedPoint>) msg.obj;
                if(allApiReturned.size()==0){
                    return;
                }
                else{
                    adapter=new ArrayAdapter<SearchedPoint>(getActivity(), android.R.layout.simple_list_item_1,allApiReturned);
                    search_location.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

        }
    };
    private LocationManager locationManager;
    public MapboxMap getMapboxMap() {
        return mapboxMap;
    }

    public MapFragment() {
    }

    private MapFragment getMapFragment(){return this;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.executorService = Executors.newCachedThreadPool();
        //connectionManager=new ConnectionManager(this);
        gpsManager = new GpsManager(this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        ViewGroup root=(ViewGroup)inflater.inflate(R.layout.fragment_map, container, false);
        speed=root.findViewById(R.id.speed);
        speed.setText("0 m/s");
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                Toast.makeText(getActivity(),R.string.internet_connection,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> strings=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                ((AutoCompleteTextView)root.findViewById(R.id.search_location)).setText(strings.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        search_location=(AutoCompleteTextView) root.findViewById(R.id.search_location);
        search_location.setThreshold(1);
        adapter=new ArrayAdapter<SearchedPoint>(getActivity(), android.R.layout.simple_list_item_2,allApiReturned);
        adapter.setNotifyOnChange(true);
        search_location.setAdapter(adapter);
        search_location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchController=new SearchController(handler,search_location.getText().toString());
                executorService.execute(searchController);
            }
        });

        search_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LatLng point=((SearchedPoint)adapterView.getItemAtPosition(position)).getCoordinates();
//                mapboxMap.clear();
//                IconFactory iconFactory = IconFactory.getInstance(getActivity());
//                Icon icon = iconFactory.fromResource(R.drawable.user_location_icon);
//                mapboxMap.addMarker(new MarkerOptions().position(point).icon(icon));
//                CameraPosition cameraPosition=new CameraPosition.Builder().target(point).zoom(10).build();
//                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000);
                zoomOnLocation(point);
            }
        });

        search_location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled=false;
                if(i== EditorInfo.IME_ACTION_SEARCH){
                    handled=true;
                    if(allApiReturned.size()==0){
                        Toast.makeText(getActivity(),R.string.not_Found,Toast.LENGTH_LONG).show();
                    }
                    else{
                        String text=capitailizeWord(textView.getText().toString());
                        String [] tokens=text.split(" ");
                        for(SearchedPoint searchedPoint:allApiReturned){
                            for(String string:tokens){
                                if(searchedPoint.getPlace_name().contains(string)){
//                                    mapboxMap.clear();
//                                    IconFactory iconFactory = IconFactory.getInstance(getActivity());
//                                    Icon icon = iconFactory.fromResource(R.drawable.user_location_icon);
//                                    mapboxMap.addMarker(new MarkerOptions().position(searchedPoint.getCoordinates()).icon(icon));
//                                    CameraPosition cameraPosition=new CameraPosition.Builder().target(searchedPoint.getCoordinates()).zoom(10).build();
//                                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000);
                                    zoomOnLocation(searchedPoint.getCoordinates());
                                    return true;
                                }
                            }
                        }
                    }
                }
                return handled;
            }
        });

        ((FloatingActionButton)root.findViewById(R.id.get_voice)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        speechRecognizer.stopListening();
                    }

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        speechRecognizer.startListening(speechIntent);
                    }
                }else{
                    requestPermissions(new String []{Manifest.permission.RECORD_AUDIO},101);
                }

                return false;
            }
        });
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return root;
    }


    public void zoomOnLocation(LatLng point){
        mapboxMap.clear();
        IconFactory iconFactory = IconFactory.getInstance(getActivity());
        Icon icon = iconFactory.fromResource(R.drawable.user_location_icon);
        mapboxMap.addMarker(new MarkerOptions().position(point).icon(icon));
        CameraPosition cameraPosition=new CameraPosition.Builder().target(point).zoom(14).build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000);
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
//                                    Toast.makeText(getContext(), String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();
                                    mapboxMap.clear();
                                    IconFactory iconFactory = IconFactory.getInstance(getActivity());
                                    Icon icon = iconFactory.fromResource(R.drawable.user_location_icon);
                                    mapboxMap.addMarker(new MarkerOptions().position(point).icon(icon));
                                    SaveDialog saveDialog=new SaveDialog(point,getMapFragment(),executorService);
                                    saveDialog.show(getActivity().getSupportFragmentManager(),"save dialog");
                                    return false;
                                }
                            });
                    }
                });
    }

    public void showCurrentLocation(Style mapStyle){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getActivity(), mapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            getActivity().findViewById(R.id.get_location).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    locationComponent.setCameraMode(CameraMode.TRACKING);
                    //locationComponent.zoomWhileTracking(10f);
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
                Toast.makeText(getActivity(),R.string.failed_location_permission,Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode==101 && grantResults.length>0){
            if(grantResults[0]==0){
                speechRecognizer.startListening(speechIntent);
            }else{
                Toast.makeText(getActivity(),R.string.failed_audio_permission,Toast.LENGTH_LONG).show();
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

    private static String capitailizeWord(String str) {
        StringBuffer s = new StringBuffer();
        char ch = ' ';
        for (int i = 0; i < str.length(); i++) {

            if (ch == ' ' && str.charAt(i) != ' ')
                s.append(Character.toUpperCase(str.charAt(i)));
            else
                s.append(str.charAt(i));
            ch = str.charAt(i);
        }
        return s.toString().trim();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(getActivity(),String.valueOf(location.getSpeed()),Toast.LENGTH_LONG).show();
        speed.setText(String.valueOf(Math.round(location.getSpeed()*100)/100)+" m/s");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(getActivity(),R.string.disabled_location,Toast.LENGTH_LONG).show();
        speed.setText("0 m/s");
    }
}