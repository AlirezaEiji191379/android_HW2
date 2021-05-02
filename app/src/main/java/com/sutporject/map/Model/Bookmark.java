package com.sutporject.map.Model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class Bookmark {
    private static ArrayList<Bookmark> bookmarks = new ArrayList<>();
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

    public static void addBookmark(Bookmark bookmark){
        bookmarks.add(bookmark);
    }

    public static ArrayList<Bookmark> getBookmarks() {
        return bookmarks;
    }

}
