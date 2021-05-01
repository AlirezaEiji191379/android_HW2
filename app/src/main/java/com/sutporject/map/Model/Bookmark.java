package com.sutporject.map.Model;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Bookmark {
    private String name;
    private LatLng latLng;

    public Bookmark(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
