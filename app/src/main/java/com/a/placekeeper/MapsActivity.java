package com.a.placekeeper;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;

import com.google.android.gms.maps.OnMapReadyCallback;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.a.placekeeper.R.id.map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    GoogleMap _map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        // настраиваем тулбар
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // отображаем ДОМОЙ
        getSupportActionBar().setHomeButtonEnabled(true); // включаем ДОМОЙ
        actionBar.setTitle("");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // находим меню
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0); // создаём штуку, которая будет анимировать иконку (и не только)
        mDrawerLayout.addDrawerListener(mDrawerToggle); // подписываем её на события открытия и закрытия меню (чтобы она знала, когда нужно анимировать кнопку)

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favourites_item:
                        Intent intent = new Intent(MapsActivity.this, FavouriteActivity.class);
                        startActivity(intent);
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.settings_item:
                        Intent intent2 = new Intent(MapsActivity.this, SettingsActivity.class);
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
                    case R.id.mapstylechooser1_item:
                        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.google_map_style_silver));
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;

                    case R.id.mapstylechooser2_item:
                        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.google_map_style_night));
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;

                    case R.id.mapstylechooser3_item:
                        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.google_map_style_aubergine));
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.mapstylechooser4_item:
                        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.google_map_style_retro));
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        _map = map;
        LatLng sydney = new LatLng(60.017584, 30.366934);
        map.addMarker(new MarkerOptions().position(sydney).title("Home"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        _map.getUiSettings().setZoomControlsEnabled(true);
        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.google_map_style_standard));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { // если нажали на
            case android.R.id.home: // кнопку меню
                // открываем меню
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                // слева
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
