package com.a.placekeeper;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.OnMapReadyCallback;


import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
               .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(60.017584, 30.366934);
        map.addMarker(new MarkerOptions().position(sydney).title("Home"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { // если нажали на
            case android.R.id.home: // кнопку меню
                // открываем меню
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                // слева
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}