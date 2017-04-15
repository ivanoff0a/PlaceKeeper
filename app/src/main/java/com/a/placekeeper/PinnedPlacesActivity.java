package com.a.placekeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.kuelye.banana.examples.tinyfavourites.TinyFavourites;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.security.AccessController.getContext;

/**
 * Created by С новым годом!!! on 20.03.2017.
 */

public class PinnedPlacesActivity extends AppCompatActivity {
TinyFavourites mTinyFavourites;
ListView listView;
GoogleMap _map;
DrawerLayout mDrawerLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinnedplaces);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Избранное");
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(R.id.favourites_item).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings_item:
                        Intent intent2 = new Intent(PinnedPlacesActivity.this, SettingsActivity.class);
                        startActivity(intent2);
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;

                    case R.id.mapschooser1_item:
                        _map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;

                    case R.id.mapschooser2_item:
                        _map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;

                    case R.id.mapschooser3_item:
                        _map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
            }
                return true;
            }
        });

        mTinyFavourites = TinyFavourites.getInstance();

        listView = (ListView) findViewById(R.id.listView); // находим список
        mTinyFavourites.getPlaces(new TinyFavourites.GetPlacesCallback() {
            @Override
            public void onResult(@NonNull List<Place> places) {
                PlacesAdapter adapter = new PlacesAdapter(PinnedPlacesActivity.this, android.R.layout.simple_list_item_1, places);
                listView.setAdapter(adapter); // присоединяем адаптер к списку

            }
        });
    }
}
