package com.a.placekeeper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PlacePickerFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_CODE_PLACE_PICKER = 1;

    private GoogleApiClient mGoogleApiClient; // штука, которая даёт доступ к Google API
    private MapView mMapView; // вьюшка с картой
    private GoogleMap mMap; // сама карта
    private boolean mPickerStarted = false; // запущена ли выбиралка мест

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // создаём штуку для доступа к API (нас интересуют фотки)
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()) // создаём строителя
                .addApi(Places.GEO_DATA_API) // добавляем API, которое нам нужно (в нём нам нужны фотки)
                .build(); // создаём клиент
        mGoogleApiClient.connect(); // подключаемся
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_map, container, false); // заполняем окно по макету
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // ищем вьюшку
        mMapView = (MapView) view.findViewById(R.id.map);

        // нужно обязательо свзяать методы onCreate, onStart, ... вьюшки с картой
        // с соотвествующими методами фрагмента или активности,
        // вот здесь, например, в onViewCreated(...) вызываем onCreate(...)
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this); // запускаем инициализацию карты

        // находим кнопку поиска места и подписываемся на нажатие
        Button pickPlaceButton = (Button) view.findViewById(R.id.pick_place_button);
        pickPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPlace(); // выбираем место
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map; // сохраняем карту в глолобальную переменную

        // включаем геолокацию,
        // при этом используется библиотечка Dexter (см. https://github.com/Karumi/Dexter),
        // её нужно добавить в проект
        Dexter.withActivity(getActivity()) // создаём этот Dexter
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION) // просим попросить у пользователя разрешение на геолокационные данные
                .withListener(new PermissionListener() { // и подписываемся на результат

                    @SuppressWarnings("MissingPermission")
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        // если получили разрешение, то
                        map.setMyLocationEnabled(true); // включаем у карты местоположение
                        map.getUiSettings().setMyLocationButtonEnabled(true); // добавляем кнопочку
                    }

                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        // ну, если пользователь не дал разрешение, не добавляем ничего
                    }

                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        // здесь ничего не делаем
                    }

                }).check(); // запускаем Dexter
    }

    @Override
    public void onStart() {
        mMapView.onStart(); // см. комментарий в onCreateView(...)

        super.onStart();
    }

    @Override
    public void onResume() {
        // см. комментарий в onCreateView(...)
        mMapView.onResume();

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        // см. комментарий в onCreateView(...)
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        mMapView.onStop(); // см. комментарий в onCreateView(...)

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // см. комментарий в onCreateView(...)
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // см. комментарий в onCreateView(...)
        mMapView.onSaveInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        // см. комментарий в onCreateView(...)
        mMapView.onLowMemory();
    }

    /**
     * Метод открывает выбиралку мест.
     */
    private void pickPlace() {
        if (mPickerStarted) { // если выбиралка запущена
            Toast.makeText(getActivity(), "Подождите, сейчас всё будет.", Toast.LENGTH_LONG).show();
        } else {
            try {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder(); // создаём строителя конверта для запуска активности с выбиралкой мест
                intentBuilder.setLatLngBounds(mMap.getProjection().getVisibleRegion().latLngBounds); // в него кладём также выбранный на карте регион
                Intent intent = intentBuilder.build(getActivity()); // создаём конверт
                startActivityForResult(intent, REQUEST_CODE_PLACE_PICKER); // стартуем активность
                mPickerStarted = true; // ставим флаг, что запустили выбиралку
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                // ошибки игнорируем
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // если получили ответ от активности с выбиралкой мест
        // и код результата - OK (то есть пользователь не отменил выбиралку, а выбрал место)
        if (requestCode == REQUEST_CODE_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                // получаем выбранное место
                // Place - класс с данными об этом месте, там есть и координаты, и тип места, и телефон, ...
                Place place = PlacePicker.getPlace(getActivity(), data);
                // добавляем маркер
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(place.getLatLng()) // координаты
                        .title(place.getName().toString()) // заголовок
                        .snippet(place.getAddress().toString()); // адрес в качестве описания
                Marker marker = mMap.addMarker(markerOptions);
                getPhotoAndSetIconForMarker(place, marker); // ставим на загрузку фотку для маркера
            }
            mPickerStarted = false; // сбрасываем флаг
        } else { // если получили от другой активности ответ
            super.onActivityResult(requestCode, resultCode, data); // то вызываем стандартный обработчик
        }
    }

    /**
     * Метод загружает фотографию для конкретного места и устанавливает как иконку для маркера.
     */
    private void getPhotoAndSetIconForMarker(Place place, final Marker marker) {
        // создаём callback, который будет выполняться, когда мы получим данные фотографии
        final ResultCallback<PlacePhotoResult> photoCallback = new ResultCallback<PlacePhotoResult>() {
            @Override
            public void onResult(@NonNull PlacePhotoResult photo) {
                // вытаскиваем саму картинку
                Bitmap bitmap = photo.getBitmap();
                // устанавливаем иконку для маркера
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            }
        };
        // создаём callback, который будет выполняться, когда мы получим данные по ВСЕМ фотографиям
        ResultCallback<PlacePhotoMetadataResult> photosCallback = new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(@NonNull PlacePhotoMetadataResult photos) {
                // проверяем статус операции
                if (photos.getStatus().isSuccess()) { // если успешно всё
                    // получаем данные по фоткам
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    // если фоток больше чем ноль (хотя бы одна)
                    if (photoMetadataBuffer.getCount() > 0) {
                        photoMetadataBuffer.get(0) // получаем первую из фоток
                                .getScaledPhoto(mGoogleApiClient, 128, 128) // хотим 128 пикселей на 128 пикселей
                                .setResultCallback(photoCallback); // устанавливаем callback (он выше объявлен)
                    }
                }
            }
        };

        Places.GeoDataApi // это нужное API
                .getPlacePhotos(mGoogleApiClient, place.getId()) // запрашиваем фотки для места по его id
                .setResultCallback(photosCallback); // устанавливаем callback (он выше объявлен)
    }

}
