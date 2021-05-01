package com.sutporject.map;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.sutporject.map.Model.Bookmark;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment {
    boolean firstFlag = true;
    public BookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstFlag = !firstFlag;
    }

    private RecyclerView bookmarksRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        bookmarksRecyclerView = view.findViewById(R.id.bookmarks_recyclerview);
        ArrayList<Bookmark> bookmarks = new ArrayList<>();
        bookmarks.add(new Bookmark("Ahmadabad", new LatLng(20,-2)));
        bookmarks.add(new Bookmark("Toorghooz", new LatLng(23,-32)));

        BookmarkCardsAdapter bookmarkCardsAdapter = new BookmarkCardsAdapter(bookmarks);
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));
        bookmarksRecyclerView.setAdapter(bookmarkCardsAdapter);
        if(!firstFlag)
            Toast.makeText(view.getContext(),"Swipe left to delete",Toast.LENGTH_SHORT).show();

        firstFlag = true;
        return view;
    }
}