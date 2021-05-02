package com.sutporject.map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.sutporject.map.Model.Bookmark;

public class SaveDialog  extends AppCompatDialogFragment {
    private LatLng latLng;
    private MapFragment fragment;

    public SaveDialog(LatLng latLng, MapFragment fragment) {
        this.latLng = latLng;
        this.fragment = fragment;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.save_dialog,null);
        ((TextView)view.findViewById(R.id.location_lat)).setText(String.format("%.3f",latLng.getLatitude()));
        ((TextView)view.findViewById(R.id.location_long)).setText(String.format("%.3f",latLng.getLongitude()));
        builder.setView(view).setTitle(R.string.save_dialog).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fragment.getMapboxMap().clear();
            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String locationName=((EditText)view.findViewById(R.id.location_name)).getText().toString();
                Bookmark.addBookmark(new Bookmark(locationName,latLng));
                Toast.makeText(getContext(),"Location " + locationName + " has been added successfully.",Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }
}
