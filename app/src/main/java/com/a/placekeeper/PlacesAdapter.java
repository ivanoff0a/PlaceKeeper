package com.a.placekeeper;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by BananaAdmin on 12.04.2017.
 */

public class PlacesAdapter extends ArrayAdapter<LatLng> {

    public PlacesAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }
}
