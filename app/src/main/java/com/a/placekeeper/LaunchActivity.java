package com.a.placekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by BananaAdmin on 19.04.2017.
 */

public class LaunchActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Intent intenttoMap = new Intent(LaunchActivity.this, MapsActivity.class);
        startActivity(intenttoMap);
        finish();
    }
}
