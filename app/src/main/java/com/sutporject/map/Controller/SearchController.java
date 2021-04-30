package com.sutporject.map.Controller;

import android.util.Log;

import com.sutporject.map.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Handler;

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

    public SearchController(android.os.Handler handler, String searchedString) {
        this.handler = handler;
        this.searchedString = searchedString;
    }

    private void doGetRequestForSuggestion() throws UnsupportedEncodingException {
        HttpUrl.Builder builder=HttpUrl.parse(String.valueOf(API_URL.append(URLEncoder.encode(searchedString, String.valueOf(StandardCharsets.UTF_8)).concat(".json")))).newBuilder();
        builder.addQueryParameter("access_token", "pk.eyJ1IjoiYWxpcmV6YWVpamkxNTEzNzkiLCJhIjoiY2tuc3hxMzdvMDh1dzJ3cGR4bGVyZDdybSJ9.G5GDrZCnKIWMxXiJXe5mtw");
        String url=builder.build().toString();
        Log.i("responsex",url);
        Request request=new Request.Builder().url(url).build();
        okHttpClient=new OkHttpClient.Builder().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("responsex",response.body().string());
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
