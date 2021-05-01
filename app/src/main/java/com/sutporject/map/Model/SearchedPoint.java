package com.sutporject.map.Model;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class SearchedPoint {
    private String place_name;
    private LatLng coordinates;

    public SearchedPoint(String place_name, LatLng coordinates) {
        this.place_name = place_name;
        this.coordinates = coordinates;
    }

    public String getPlace_name() {
        return place_name;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return  place_name;
    }
}
