package com.a.placekeeper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kuelye.banana.examples.tinyfavourites.TinyFavourites;

import static com.a.placekeeper.R.id.map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {

    private static final int REQUEST_CODE_PLACE_PICKER = 1;

    // === ПЕРЕМЕННЫЕ

    DrawerLayout mDrawerLayout; // менюшка
    ActionBarDrawerToggle mDrawerToggle; // штука, которая управляет анимация кнопки ДОМОЙ
    GoogleMap _map; // карта
    EditText editText; // ???
    Boolean mPickerStarted = false; // флаг, = true, если уже запущена выбиралка мест, и = false - в противном случае
    MapView mMapView; // вьюшка, отрисовывающая карту
    private StreetViewPanoramaView mPanoramaView; // вьюшка с панорамой
    private StreetViewPanorama mPanorama; // cама панорама
    private LatLng mLastPoiLatLng; // координаты последнего POI, на который кликнули
    Boolean bigPanoramaIsOpened = false; // открыта ли большая панорама
    boolean locationIsChanged = false; // false - вначале, когда приходят координаты мп меняется на true
    boolean recordingroute = false; // = true, когда включена запись маршрута
    FloatingActionButton pinnedplacebutton; // фаб
    TinyFavourites mTinyFavourites; // штука с избранным
    String placeId; // идентификатор последнего POI, на который кликнул
    LatLng mPreviousLatLng; // ???

    // === МЕТОДЫ

    // === Жизненный цикл активности ===

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        mTinyFavourites = TinyFavourites.getInstance(); // получаем TinyFavourites
        mTinyFavourites.initialize(MapsActivity.this); // инициализируем TinyFavourites
        // лучше читать письмо (интент) сразу, в начале метода onCreate(...)
        // но иногда бываем необходимым ещё чего-нибудь тут сделать до чтения

        if (getIntent().hasExtra("МЕСТО")) { // если письмо (интент) содержит
            final String placeId = getIntent().getExtras().getString("МЕСТО"); // читаем
            mTinyFavourites.getFavouritePlace(placeId, new TinyFavourites.GetPlaceCallback() {
                @Override
                public void onResult(@Nullable Place place) {
                    _map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16.0f));
                }
            });
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        // ищем вьюшки
        mPanoramaView = (StreetViewPanoramaView) findViewById(R.id.street_view_panorama_view);
        // нужно обязательо свзяать методы onCreate, onStart, ... вьюшек с картой и панорамой
        // с соотвествующими методами фрагмента или активности,
        // вот здесь, например, в onViewCreated(...) вызываем onCreate(...)
        mPanoramaView.onCreate(null);

        mPanoramaView.getStreetViewPanoramaAsync(this);// запускаем инициализацию панорамы
        mPanoramaView.setTranslationY(600);




        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9229888776029148~6309098316");//добавляем идентификатор ПРИЛОЖЕНИЯЕЙ
        AdView mAdView = (AdView) findViewById(R.id.adView);//создаем вьюшку
        AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("EA41746F2D8B08BA5A5AB943BC932E74")  // An example device ID
//                .build();
        mAdView.loadAd(adRequest);

        // настраиваем тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // находим меню
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0); // создаём штуку, которая будет анимировать иконку (и не только)
        mDrawerLayout.addDrawerListener(mDrawerToggle); // подписываем её на события открытия и закрытия меню (чтобы она знала, когда нужно анимировать кнопку)


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(R.id.map_item).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favourites_item:
                        Intent intent1 = new Intent(MapsActivity.this, PinnedPlacesActivity.class);
                        startActivity(intent1);
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
//                    case R.id.mapstylechooser1_item:
//                        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.google_map_style_silver));
//                        mDrawerLayout.closeDrawer(Gravity.LEFT);
//                        break;
//
//                    case R.id.mapstylechooser2_item:
//                        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.google_map_style_night));
//                        mDrawerLayout.closeDrawer(Gravity.LEFT);
//                        break;
//
//                    case R.id.mapstylechooser3_item:
//                        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.google_map_style_aubergine));
//                        mDrawerLayout.closeDrawer(Gravity.LEFT);
//                        break;
//                    case R.id.mapstylechooser4_item:
//                        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.google_map_style_retro));
//                        mDrawerLayout.closeDrawer(Gravity.LEFT);
//                        break;
                }
                return true;
            }
        });
        FloatingActionButton searchbutton = (FloatingActionButton) findViewById(R.id.button2);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPlace();

            }
        });

        final FloatingActionButton startmovingb = (FloatingActionButton) findViewById(R.id.recordroutebutton);
        startmovingb.setOnClickListener
                (new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                     if (recordingroute == true){
                     startmovingb.setImageResource(R.drawable.recordingimage);
                     recordingroute = false;
                     }else{
                     startmovingb.setImageResource(R.drawable.ic_transfer_within_a_station_black_36dp);
                     recordingroute = true;
                     }
                     }
                 });




        pinnedplacebutton = (FloatingActionButton) findViewById(R.id.pinnedplacebutton);
        pinnedplacebutton.setTranslationY(600);
        pinnedplacebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTinyFavourites.containFavouritePlace(placeId)) { // если место уже в любимых
                    mTinyFavourites.removeFavouritePlace(placeId); // удаляем его из любимых
                    Toast.makeText(MapsActivity.this, "Ваше место удалено из Избранного", Toast.LENGTH_SHORT).show();
                    checkPinnedPlaceButton();
                } else { // иначе
                    mTinyFavourites.addFavouritePlace(placeId); // наоборот, добавляем его
                    Toast.makeText(MapsActivity.this, "Ваше место добавленно в Избранное", Toast.LENGTH_SHORT).show();
                    checkPinnedPlaceButton();
                }
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    public void onResume(){
        super.onResume();
        bigPanoramaIsOpened = false;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // если получили ответ от активности с выбиралкой мест
        // и код результата - OK (то есть пользователь не отменил выбиралку, а выбрал место)
        if (requestCode == REQUEST_CODE_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                // получаем выбранное место
                // Place - класс с данными об этом месте, там есть и координаты, и тип места, и телефон, ...
                Place place = PlacePicker.getPlace(MapsActivity.this, data);
                // добавляем маркер
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(place.getLatLng()) // координаты
                        .title(place.getName().toString()) // заголовок
                        .snippet(place.getAddress().toString()); // адрес в качестве описания
                Marker marker = _map.addMarker(markerOptions);
            }
            mPickerStarted = false; // сбрасываем флаг
        } else { // если получили от другой активности ответ
            super.onActivityResult(requestCode, resultCode, data); // то вызываем стандартный обработчик
        }
    }

    // === Карта и панорама ===

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {
        mPanorama = panorama; // сохраняем панорамку в глобальную переменную
        //setPanoramaTo(mLastPoiLatLng); // устанавливаем её в текущую позицию

        mPanorama.setOnStreetViewPanoramaCameraChangeListener(new StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener() {
            @Override
            public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera) {
                if (bigPanoramaIsOpened == false) {
                    Intent intent = new Intent(MapsActivity.this, HugePanoramaActivity.class);
                    intent.putExtra("LATLNG", mLastPoiLatLng);
                    startActivity(intent);
                    bigPanoramaIsOpened = true;
                }

            }
        });

        // подписываемся на событие изменения координат в панораме
        mPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                if (streetViewPanoramaLocation != null && streetViewPanoramaLocation.links != null) {
                    // панорама есть
                } else {
                    // панорамы нет
                    mPanoramaView.setTranslationY(400);

                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        _map = map;
        LatLng sydney = new LatLng(60.017584, 30.366934);
        setMyLocationEnabled();
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        _map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.google_map_style_standard));
        _map.setPadding(770,1030, 5, 30);


        _map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                mPanoramaView.animate().translationY(0);
                mPanorama.setPosition(poi.latLng);
                mLastPoiLatLng = poi.latLng;
                placeId = poi.placeId;
                pinnedplacebutton.animate().translationY(0);
                checkPinnedPlaceButton();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(poi.latLng) // координаты
                        //.snippet() // описание
                        .alpha(0.9f) // прозрачность
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)) // иконка
                        .flat(true); // делаем плоской иконку
                _map.addMarker(markerOptions);
            }
        });
        _map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mPanoramaView.animate().translationY(600);
                pinnedplacebutton.animate().translationY(600);



            }
        });
    }

    // === Разное

    /**
     * Двигает компас и другие иконки.
     */
    public void setPadding(){
        _map.setPadding(500,1000,5, 30);
    }

    /**
     * Рисует маршрут от предыдущей точки к текущей.
     * @param currentL Координаты текущего местоположения.
     */
    public void drawRoute(LatLng currentL) {
        Log.v("ASADSAD", "ASDASDSA");
        boolean previousLatLngNotNull = mPreviousLatLng != null;
        boolean distanceChanged = true; // TODO
        if (previousLatLngNotNull && distanceChanged) {
            // создаём полилинию
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(mPreviousLatLng)
                    .add(currentL)
                    .color(Color.BLUE);
            // добавляем полилинию на карту
            _map.addPolyline(polylineOptions);
        }

        mPreviousLatLng = currentL;
    }

    /**
     * Метод открывает выбиралку мест.
     */
    private void pickPlace() {
        if (mPickerStarted) { // если выбиралка запущена
            Toast.makeText(MapsActivity.this, "Подождите, сейчас всё будет.", Toast.LENGTH_LONG).show();
        } else {
            try {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder(); // создаём строителя конверта для запуска активности с выбиралкой мест
                intentBuilder.setLatLngBounds(_map.getProjection().getVisibleRegion().latLngBounds); // в него кладём также выбранный на карте регион
                Intent intent = intentBuilder.build(MapsActivity.this); // создаём конверт
                startActivityForResult(intent, REQUEST_CODE_PLACE_PICKER); // стартуем активность
                mPickerStarted = true; // ставим флаг, что запустили выбиралку
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                // ошибки игнорируем
            }
        }
    }

    /**
     * Включает возможность определения местоположения.
     */
    public void setMyLocationEnabled(){
        Dexter.withActivity(MapsActivity.this) // создаём этот Dexter
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION) // просим попросить у пользователя разрешение на геолокационные данные
                .withListener(new PermissionListener() { // и подписываемся на результат

                    @SuppressWarnings("MissingPermission")
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        // если получили разрешение, то
                        _map.setMyLocationEnabled(true); // включаем у карты местоположение
                        _map.getUiSettings().setMyLocationButtonEnabled(true); // добавляем кнопочку

                        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(Location location) {
                                double latitude = location.getLatitude();
                                double longtitude = location.getAltitude();
                                LatLng currentL = new LatLng(latitude, longtitude);
                                drawRoute(currentL);
                                if (locationIsChanged == false) {
                                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                                    if (_map != null) {
                                        _map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                                    }
                                    locationIsChanged = true;
                                }
                            }
                        };
                        _map.setOnMyLocationChangeListener(myLocationChangeListener);
                    }

                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        // ну, если пользователь не дал разрешение, не добавляем ничего
                    }

                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        // здесь ничего не делаем
                    }

                }).check(); // запускаем Dexter
    }

    /**
     * Меняет иконку звёздочки.
     */
    private void checkPinnedPlaceButton() {
        if (mTinyFavourites.containFavouritePlace(placeId)) { // если место уже в любимых
            pinnedplacebutton.setImageResource(R.drawable.ic_star_black_36dp);//заштрихованная
        } else { // иначе
            pinnedplacebutton.setImageResource(R.drawable.ic_star_outline_black_36dp);
        }
    }

    // === Архив

    //   private void save(String text){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("СТРОЧКА", text);
//        editor.commit();
//    }z
//
//    private String load(){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String text = preferences.getString("СТРОЧКА", "");
//
//        return text;
//    }


}
