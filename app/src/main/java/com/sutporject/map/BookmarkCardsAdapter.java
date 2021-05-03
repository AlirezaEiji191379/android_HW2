package com.sutporject.map;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sutporject.map.Model.Bookmark;

import java.util.ArrayList;
import java.util.List;

public class BookmarkCardsAdapter extends RecyclerView.Adapter<BookmarkCardsAdapter.BookmarkViewHolder> implements Filterable{
    private static final String TAG = "Filter";
    private List<Bookmark> bookmarks;
    private List<Bookmark> bookmarksFull;

    public BookmarkCardsAdapter(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
        bookmarksFull = new ArrayList<>(bookmarks);
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

    @Override
    public Filter getFilter() {
        return bookmarkFilter;
    }

    private Filter bookmarkFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Bookmark> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0)
                filteredList.addAll(bookmarksFull);
            else{
                String pattern = charSequence.toString().trim().toLowerCase();
                for (Bookmark bookmark : bookmarksFull) {
                    if(bookmark.getName().trim().toLowerCase().contains(pattern))
                        filteredList.add(bookmark);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            Log.i(TAG, "perform: \n" + filteredList.toString());

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            bookmarks.clear();
            bookmarks.addAll((List) filterResults.values);

            Log.i(TAG, "publis: \n" + bookmarks.toString());

            notifyDataSetChanged();
        }
    };

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
