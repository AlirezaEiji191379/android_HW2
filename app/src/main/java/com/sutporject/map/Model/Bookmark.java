package com.sutporject.map.Model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Bookmark {
    private static ArrayList<Bookmark> bookmarks = new ArrayList<>();
    private String name;
    private double latLong;
    private double latLat;
    private int ID;

    public Bookmark(String name, double latLong, double latLat) {
        this.name = name;
        this.latLat = latLat;
        this.latLong = latLong;
    }

    public String getName() {
        return name;
    }

    public double getLatLong() {
        return latLong;
    }

    public double getLatLat() {
        return latLat;
    }

    public int getID() {
        return ID;
    }

    public static void deleteBookmarks(){
        bookmarks.clear();
    }

    public static void addBookmark(Bookmark bookmark){
        bookmarks.add(bookmark);
    }

    public static void addAllBookmarks(List<Bookmark> bookmarkList){
        bookmarks.addAll(bookmarkList);
    }

    public static ArrayList<Bookmark> getBookmarks() {
        return bookmarks;
    }


    public static void setBookmarks(ArrayList<Bookmark> bookmarks) {
        Bookmark.bookmarks = bookmarks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatLong(double latLong) {
        this.latLong = latLong;
    }

    public void setLatLat(double latLat) {
        this.latLat = latLat;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
