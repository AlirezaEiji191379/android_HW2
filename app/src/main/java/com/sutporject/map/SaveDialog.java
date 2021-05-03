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
import com.sutporject.map.Controller.DataBaseHelper;
import com.sutporject.map.Model.Bookmark;

import java.util.concurrent.ExecutorService;

public class SaveDialog  extends AppCompatDialogFragment {
    private LatLng latLng;
    private MapFragment fragment;
    private ExecutorService executorService;

    public SaveDialog(LatLng latLng, MapFragment fragment, ExecutorService executorService) {
        this.latLng = latLng;
        this.fragment = fragment;
        this.executorService = executorService;
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
                Bookmark bookmark = new Bookmark(locationName,latLng.getLongitude(),latLng.getLatitude());
                Bookmark.addBookmark(bookmark);

                DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(),executorService);

                boolean success = dataBaseHelper.addBookmark(bookmark);

                Toast.makeText(getContext(),success?"Location " + locationName + " has been added successfully.":"Error adding location!"
                        ,Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }
}
