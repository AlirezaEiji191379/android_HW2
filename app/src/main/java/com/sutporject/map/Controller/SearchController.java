package com.sutporject.map.Controller;

import android.os.Message;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.sutporject.map.Model.SearchedPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class SearchController implements Runnable {
    private final StringBuilder API_URL=new StringBuilder("https://api.mapbox.com/geocoding/v5/mapbox.places/");
    private android.os.Handler handler;
    private OkHttpClient okHttpClient;
    private String searchedString;
    private ArrayList<SearchedPoint> allApiReturned=new ArrayList<>();
    public SearchController(android.os.Handler handler, String searchedString) {
        this.handler = handler;
        this.searchedString = searchedString;
    }

    private ArrayList<SearchedPoint> responseParser(Response response) throws IOException, JSONException {
        ArrayList<SearchedPoint> allPoints=new ArrayList<>();
        String respBody=response.body().string();
        JSONObject jsonResponse=new JSONObject(respBody);
        JSONArray features=jsonResponse.getJSONArray("features");
        if(features.length()==0){
            return allPoints;
        }
        for(int i=0;i<features.length();i++){
            JSONObject model=features.getJSONObject(i);
            String place_name=(String) model.get("place_name");
            JSONObject geometry=model.getJSONObject("geometry");
            JSONArray coordinates=geometry.getJSONArray("coordinates");
            LatLng placeCoordinate=new LatLng(Double.parseDouble((String.valueOf(coordinates.get(1)))),Double.parseDouble((String.valueOf(coordinates.get(0)))));
            allPoints.add(new SearchedPoint(place_name,placeCoordinate));
        }
        return allPoints;
    }

    private void doGetRequestForSuggestion() throws UnsupportedEncodingException {
        HttpUrl.Builder builder=HttpUrl.parse(String.valueOf(API_URL.append(URLEncoder.encode(searchedString, String.valueOf(StandardCharsets.UTF_8)).concat(".json")))).newBuilder();
        builder.addQueryParameter("access_token", "pk.eyJ1IjoiYWxpcmV6YWVpamkxNTEzNzkiLCJhIjoiY2tuc3hxMzdvMDh1dzJ3cGR4bGVyZDdybSJ9.G5GDrZCnKIWMxXiJXe5mtw");
        String url=builder.build().toString();
        Request request=new Request.Builder().url(url).build();
        okHttpClient=new OkHttpClient.Builder().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    Message message=Message.obtain();
                    message.what=0;
                    handler.sendMessage(message);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    allApiReturned=responseParser(response);
                    Message message=Message.obtain();
                    message.obj=allApiReturned;
                    message.what=1;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }



    @Override
    public void run() {
        try {
            doGetRequestForSuggestion();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
