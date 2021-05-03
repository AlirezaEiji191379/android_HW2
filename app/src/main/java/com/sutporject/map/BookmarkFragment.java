package com.sutporject.map;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.sutporject.map.Controller.SwipeController;
import com.sutporject.map.Controller.SwipeControllerActions;
import com.sutporject.map.Model.Bookmark;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment {
    private boolean firstFlag = true;
    private RecyclerView bookmarksRecyclerView;
    private ItemTouchHelper itemTouchHelper;
    private FloatingSearchView floatingSearchView;
    private BookmarkCardsAdapter bookmarkCardsAdapter;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstFlag = !firstFlag;
        SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Toast.makeText(getContext(),"clicked",Toast.LENGTH_SHORT).show();
                super.onRightClicked(position);
            }
        },getContext());
        itemTouchHelper = new ItemTouchHelper(swipeController);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        floatingSearchView = view.findViewById(R.id.search_bar_bookmarks);
        if(Bookmark.getBookmarks().size() != 0){
            TextView label = view.findViewById(R.id.no_bookmark_label);
            label.setVisibility(View.INVISIBLE);
            label.setEnabled(false);

            bookmarksRecyclerView = view.findViewById(R.id.bookmarks_recyclerview);
            itemTouchHelper.attachToRecyclerView(bookmarksRecyclerView);
            bookmarkCardsAdapter = new BookmarkCardsAdapter(Bookmark.getBookmarks());
            bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                    LinearLayoutManager.VERTICAL, false));
            bookmarksRecyclerView.setAdapter(bookmarkCardsAdapter);

            if(!firstFlag)
                Toast.makeText(view.getContext(),"Swipe left to delete",Toast.LENGTH_SHORT).show();

            firstFlag = true;

            floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
                @Override
                public void onSearchTextChanged(String oldQuery, String newQuery) {
                    bookmarkCardsAdapter.getFilter().filter(newQuery);
                }
            });

        }else{
            floatingSearchView.setVisibility(View.INVISIBLE);
            floatingSearchView.setEnabled(false);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}