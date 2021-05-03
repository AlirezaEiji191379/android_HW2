package com.sutporject.map.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.sutporject.map.Model.Bookmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String BOOKMARK_TABLE = "BOOKMARK_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_BOOKMARK_NAME = "BOOKMARK_NAME";
    public static final String COLUMN_BOOKMARK_LONG = "BOOKMARK_LONG";
    public static final String COLUMN_BOOKMARK_LAT = "BOOKMARK_LAT";
    private ExecutorService executorService;

    public DataBaseHelper(@Nullable Context context, ExecutorService executorService) {
        super(context, "bookmark.db", null, 1);
        this.executorService = executorService;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + BOOKMARK_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BOOKMARK_NAME + " TEXT, " + COLUMN_BOOKMARK_LONG + " REAL, " + COLUMN_BOOKMARK_LAT + " REAL)";

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                sqLiteDatabase.execSQL(createTableStatement);
            }
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public boolean addBookmark(Bookmark bookmark){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_BOOKMARK_NAME, bookmark.getName());
        cv.put(COLUMN_BOOKMARK_LONG, bookmark.getLatLong());
        cv.put(COLUMN_BOOKMARK_LAT, bookmark.getLatLat());
        final long[] insert = new long[1];

        executorService.execute(new Runnable() {
            @Override
            public void run() {
               insert[0] = db.insert(BOOKMARK_TABLE,null,cv);
               db.close();
            }
        });

        if (insert[0] == -1)
            return false;
        return true;
    }

    public void getBookmarks(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + BOOKMARK_TABLE;
        executorService.execute(new GettingBookmarksRunnable(db,query));
    }

    public void deleteAllBookmarks(){
        SQLiteDatabase db = this.getWritableDatabase();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                db.delete(BOOKMARK_TABLE,null,null);
                db.close();
            }
        });
    }

    static private class GettingBookmarksRunnable implements Runnable{
        SQLiteDatabase db;
        String query;

        private GettingBookmarksRunnable(SQLiteDatabase db, String query){
            this.db = db;
            this.query = query;
        }

        @Override
        public void run() {
            List<Bookmark> returnedList = new ArrayList<>();
            Cursor cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){
                do{
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    double latLong = cursor.getDouble(2);
                    double latLat = cursor.getDouble(3);
                    Bookmark bookmark = new Bookmark(name,latLong,latLat);
                    bookmark.setID(id);
                    returnedList.add(bookmark);
                }while (cursor.moveToNext());
                Bookmark.addAllBookmarks(returnedList);
            }
            cursor.close();
            db.close();
        }

    }

}
