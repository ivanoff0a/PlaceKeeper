package com.a.placekeeper;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;

/**
 * Created by BananaAdmin on 12.04.2017.
 */

public class PlacesAdapter extends ArrayAdapter<Place> {
    public PlacesAdapter(@NonNull Context context, @LayoutRes int resource, List <Place> places) {
        super(context, resource, places);
    }
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        Place places = (Place) getItem(position);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(places.getName() + "\n" + places.getPhoneNumber());
        return view;
    }

}

