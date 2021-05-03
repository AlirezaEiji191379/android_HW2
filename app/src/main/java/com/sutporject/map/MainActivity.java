package com.sutporject.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.sutporject.map.Controller.DataBaseHelper;
import com.sutporject.map.Model.Bookmark;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "In Main";
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        loadBookmarksDataBase();
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MapFragment mapFragment = new MapFragment();
        BookmarkFragment bookmarkFragment = new BookmarkFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        final Fragment[] shownFragment = {mapFragment};

        fragmentTransaction.add(R.id.mapContainer, mapFragment);
        fragmentTransaction.commit();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.maps_page);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == bottomNavigation.getSelectedItemId()){
                    Log.d("no", "already selected: ");
                    return false;
                }

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                if(shownFragment[0].equals(bookmarkFragment) || shownFragment[0].equals(settingsFragment)){
                    FragmentManager fmRemove = getSupportFragmentManager();
                    FragmentTransaction ftRemove = fmRemove.beginTransaction();
                    if(shownFragment[0].equals(bookmarkFragment))
                        ftRemove.remove(bookmarkFragment);
                    else
                        ftRemove.remove(settingsFragment);
                    ftRemove.commit();
                }
                if(item.getItemId() == R.id.bookmarks_page) {
                    ft.add(R.id.mapContainer,bookmarkFragment,null);
                    ft.addToBackStack(null);
                    ft.commit();
                    Log.d("yes", bottomNavigation.getSelectedItemId() + " ");
                     shownFragment[0] = bookmarkFragment;
                    return true;
                }else if(item.getItemId() == R.id.settings_page){
                    ft.add(R.id.mapContainer,settingsFragment,null);
                    ft.addToBackStack(null);
                    ft.commit();
                    shownFragment[0] = settingsFragment;
                    return true;
                }else if(item.getItemId() == R.id.maps_page){
                    shownFragment[0] = mapFragment;
                    return true;
                }
                return false;
            }
        });

//        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                bottomNavigation.getMenu().getItem(1).setChecked(true);
//            }
//        });
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.i(TAG, "onPause: ");
//        clearDatabase();
//        rewriteDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //
    private void rewriteDatabase() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this,executorService);
        for (Bookmark bookmark : Bookmark.getBookmarks()) {
            dataBaseHelper.addBookmark(bookmark);
        }
    }

    private void clearDatabase() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this,executorService);
        dataBaseHelper.deleteAllBookmarks();
    }
    //

    private void loadBookmarksDataBase() {
        Bookmark.deleteBookmarks();
        ExecutorService executorService = Executors.newCachedThreadPool();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this,executorService);
        dataBaseHelper.getBookmarks();
        String log = "";
        for (Bookmark bookmark : Bookmark.getBookmarks()) {
            log += bookmark.getName() +"  " + bookmark.getID() + "\n";
        }
        Log.i(TAG, log);
    }

    public void checkPermission(){
        String [] allPermissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.RECORD_AUDIO
        };
            ActivityCompat.requestPermissions(this,allPermissions,100);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}