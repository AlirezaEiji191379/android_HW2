package com.sutporject.map;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sutporject.map.Model.Bookmark;

import java.util.List;

public class BookmarkCardsAdapter extends RecyclerView.Adapter<BookmarkCardsAdapter.BookmarkViewHolder> {
    private List<Bookmark> bookmarks;

    public BookmarkCardsAdapter(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_bookmark,parent,false);
        return new BookmarkViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);
        holder.locationName.setText(bookmark.getName());
        holder.locationLong.setText(String.format("%.3f",bookmark.getLatLng().getLongitude()));
        holder.locationLat.setText(String.format("%.3f",bookmark.getLatLng().getLatitude()));
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    class BookmarkViewHolder extends RecyclerView.ViewHolder{

        private TextView locationName, locationLat, locationLong;
        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.bookmark_name);
            locationLat = itemView.findViewById(R.id.bookmark_lat);
            locationLong = itemView.findViewById(R.id.bookmark_long);
        }
    }
}
