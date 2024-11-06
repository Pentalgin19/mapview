package com.example.navigation;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.collection.MutableObjectList;
import androidx.core.app.ActivityCompat;

import com.example.navigation.databinding.ActivityMain2Binding;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;

import java.util.List;

public class MainActivity2 extends Activity {
    public Double lat = 0.00000;
    public Double lon = 0.00000;

    private ImageButton btnShowTrafficJams;

    private LocationManager locationManager;
    private ActivityMain2Binding binding;
    private MapView mapView;
    private String search = null;
    private SearchManager searchManager;
    public Mapkit mapkit;
    private Boolean showWhereIAM = true;
    private boolean enabled;
    private PointObj pointObj;
    private ClearPoint clearPoint;
    private Route route;
    private IndoorNavigation indoorNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("eed2a724-fc95-4e5d-935a-8e0c346df956");
        MapKitFactory.initialize(this);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        btnShowTrafficJams = binding.btnShowTrafficJams;
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        mapView = binding.mapView;
        mapkit = new Mapkit(mapView, this, this, binding.edSearch);
        indoorNavigation = new IndoorNavigation(mapView, this);

        pointObj = new PointObj(this, mapView);
        pointObj.cardView = binding.ll;
        pointObj.tvLatitude = binding.latitude;
        pointObj.tvLongutude = binding.longitude;
        pointObj.someInformation = binding.someInformation;
        pointObj.addTapAndInputListener();
        pointObj.info = binding.info;
        pointObj.cardViewFilter = binding.cardViewFilter;


        route = new Route(mapView, this);
        Route.setTvTime(binding.tvTime);
        Route.setTvLenght(binding.tvLength);

        clearPoint = new ClearPoint(this, mapView);

        binding.btnShowResults.setOnClickListener(v -> {
            if (binding.cardViewFilter.getVisibility() == View.VISIBLE){
                binding.cardViewFilter.setVisibility(View.GONE);
            }else{
                binding.cardViewFilter.setVisibility(View.VISIBLE);
            }
            if (search != null && !search.isEmpty()) {
                if (search.equals("ОКЭИ 105 кабинет ")){
                    Toast.makeText(this, "ooo", Toast.LENGTH_SHORT).show();
                    mapkit.setE(true);
                    mapkit.subQuery(search);
                }
                mapkit.setE(true);
                mapkit.subQuery(search);
            }
        });

        binding.edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if( event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    search = v.getText().toString();
                    mapkit.subQuery(search);
                    return true;
                }
                return false;

            }
        });
        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                search = editable.toString();
            }
        });

        //if your gps off
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Toast.makeText(this, "turn on your gps", Toast.LENGTH_LONG).show();
        }
        mapkit.requestLocationPermission();

        binding.btnShowMyPosition.setOnClickListener(v -> {
            pointObj.setCameraPosition(lat, lon);
        });

        binding.btnShowWalkingRoute.setOnClickListener(v -> {
            binding.timeLength.setVisibility(View.VISIBLE);
            if (Route.getCarRoute()){
                pointObj.deleteCarRoute();
            }
            pointObj.deleteWalkingRoute();
            mapkit.setWalkingRoute();
        });

        binding.btnShowCarRoute.setOnClickListener(v -> {
            binding.timeLength.setVisibility(View.VISIBLE);
            if (Route.getWalkRoute()){
                pointObj.deleteWalkingRoute();
            }
            pointObj.deleteCarRoute();
            mapkit.setCarRoute();
        });

        btnShowTrafficJams.setOnClickListener(v -> {
            PointObj.setZoom(PointObj.getZoom() + 1f);
            pointObj.setCameraPosition(lat, lon);
        });

        binding.btnMinusMash.setOnClickListener(v -> {
            PointObj.setZoom(PointObj.getZoom() - 1f);
            pointObj.setCameraPosition(lat, lon);
        });

        binding.btnCafe.setOnClickListener(v -> {
            search = "cafe";
            binding.edSearch.setText("Кафе");
            binding.cardViewFilter.setVisibility(View.GONE);
            mapkit.setE(true);
            mapkit.subQuery(search);
        });
        binding.btnGasoline.setOnClickListener(v -> {
            search = "заправка";
            binding.edSearch.setText("Заправка");
            binding.cardViewFilter.setVisibility(View.GONE);
            mapkit.setE(true);
            mapkit.subQuery(search);
        });
        binding.btnPark.setOnClickListener(v -> {
            search = "парк";
            binding.edSearch.setText("Парк");
            binding.cardViewFilter.setVisibility(View.GONE);
            mapkit.setE(true);
            mapkit.subQuery(search);
        });
        binding.btnHospital.setOnClickListener(v -> {
            search = "hospital";
            binding.edSearch.setText("Больница");
            binding.cardViewFilter.setVisibility(View.GONE);
            mapkit.setE(true);
            mapkit.subQuery(search);
        });
        binding.btnHotel.setOnClickListener(v -> {
            search = "отель";
            binding.edSearch.setText("Отель");
            binding.cardViewFilter.setVisibility(View.GONE);
            mapkit.setE(true);
            mapkit.subQuery(search);
        });
        binding.btnStar.setOnClickListener(v -> {
            search = "Что посмотреть";
            binding.edSearch.setText("Что посмотреть");
            binding.cardViewFilter.setVisibility(View.GONE);
            mapkit.setE(true);
            mapkit.subQuery(search);
        });
        binding.btnCross.setOnClickListener(v -> {
            if (binding.info.getVisibility() == View.GONE){
                binding.info.setVisibility(View.VISIBLE);
            }else{
                binding.info.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 500, 10,
                locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                500, 10, locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (enabled) {
                Mapkit.setLatitude(location.getLatitude());
                Mapkit.setLongitude(location.getLongitude());
                PointObj.setMyLatitude(location.getLatitude());
                PointObj.setMyLongitude(location.getLongitude());

                mapkit.showUserPin();

                indoorNavigation.checkIfUserInBuilding(new Point(location.getLatitude(), location.getLongitude()));

                if (IndoorNavigation.getRedactPositionState()){
                    indoorNavigation.collegeAuditoriums();
                    indoorNavigation.checkIndoorPoints(new Point(location.getLatitude(), location.getLongitude()));
                }else {
                    PolylineMapObject point = indoorNavigation.getItemFromList();

                }

                if (Route.getCarRoute()){
                    mapView.getMapWindow().getMap().getMapObjects().clear();
                    mapkit.showUserPin();
                    pointObj.setSelectedPoint(PointObj.getSelectedPointLatitude(), PointObj.getSelectedPointLongitude());
                    route.setCarRoute();
                }

                if (Route.getWalkRoute()){
                    pointObj.deleteAllRoute();
                    mapkit.showUserPin();
                    pointObj.setSelectedPoint(PointObj.getSelectedPointLatitude(), PointObj.getSelectedPointLongitude());
                    route.setWalkingRoute();
                }
                lat = location.getLatitude();
                lon = location.getLongitude();
                if (showWhereIAM) {
                    pointObj.setCameraPosition(lat, lon);
                    showWhereIAM = false;
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(
                    MainActivity2.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity2.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        MapKitFactory.getInstance().onStop();
        mapView.onStop();
        super.onStop();
    }
}
