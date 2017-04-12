package com.a.placekeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Set;

import static java.security.AccessController.getContext;

/**
 * Created by С новым годом!!! on 20.03.2017.
 */

public class PinnedPlacesActivity extends AppCompatActivity{



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinnedplaces);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Избранное");

//        ListView listView = (ListView) view.findViewById(R.id.listView); // находим список
//        PlacesAdapter adapter = new PlacesAdapter(getActivity()); // создаём злодейских адаптер
//        listView.setAdapter(adapter); // присоединяем адаптер к списку
    }
//    public VillainsAdapter(@NonNull Context context) {
//        super(context, R.layout.layout_favourite_item); // вызываем родительский конструктор (так надо)
//
//        private static final String KEY_FAVOURITE_VILLAIN_PLACES = "Избранное";
//
//        mPlaceId = poi.placeId;
//
//        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext()); // ищем штуку для сохранений
//        Set<String> favouriteVillainsDefault = new HashSet<>(); // создаём пустое множество любимых злодеев - оно будем использовано по умолчанию
//        // загружаем сохранённое множество любимых злодеев (или пустое оставляем, если ничего не сохранено))
//        mFavouriteVillainNames = new HashSet<>(mPreferences.getStringSet(KEY_FAVOURITE_VILLAIN_PLACES, favouriteVillainsDefault));
//
//        if (mFavouriteVillainNames.contains(villainName)) { // если злодей был любимым`
//            mFavouriteVillainNames.remove(villainName); // то теперь его нужно убрать из любимых`
//            } else { // иначе
//                mFavouriteVillainNames.add(villainName); // добавляем его в любимые`
//            }
//
//        SharedPreferences.Editor editor = mPreferences.edit(); // открываем сохранялку на запись`
//        editor.putStringSet(KEY_FAVOURITE_VILLAIN_PLACES, mFavouriteVillainNames); // записываем туда любимых злодеев`
//        editor.commit(); // сохраняем изменения`
//    }

}

