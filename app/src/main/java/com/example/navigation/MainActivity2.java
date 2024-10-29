package com.example.navigation;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.navigation.databinding.ActivityMain2Binding;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

public class MainActivity2 extends Activity {

    private Button btnShowTrafficJams;

    private LocationManager locationManager;
    private ActivityMain2Binding binding;
    private MapView mapView;
    private String search;
    private SearchManager searchManager;
    private Session searchSession;
    private Mapkit mapkit;
    private Boolean showWhereIAM = true;
    private boolean enabled;
    private Double lat = 0.00000;
    private Double lon = 0.00000;
    private EeE eee;

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

        eee = new EeE(this, mapView);
        eee.cardView = binding.cvShowRoute;
        eee.addTapAndInputListener();

        binding.btnShowResults.setOnClickListener(v -> {
            if (search != null) {
                mapkit.setE(true);
                mapkit.loc();
            }
        });

        btnShowTrafficJams.setOnClickListener(v -> {
            mapkit.showTrafficJams();
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

            }
        });

        //if your gps off
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Toast.makeText(this, "turn on your gps", Toast.LENGTH_LONG).show();
        }

        binding.btnShowMyPosition.setOnClickListener(v -> {
            eee.setCameraPosition(lat, lon);
            eee.showHide(true);
        });

        binding.btnShowWalkingRoute.setOnClickListener(v -> {
            mapkit.setWalkingRoute();
        });
        
        binding.btnShowCarRoute.setOnClickListener(v -> {
            mapkit.setCarRoute();
        });

        mapkit.requestLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        checkEnabled();
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
                lat = location.getLatitude();
                lon = location.getLongitude();
                Mapkit.setLatitude(location.getLatitude());
                Mapkit.setLongitude(location.getLongitude());
                if (showWhereIAM) {
                    EeE eee = new EeE(MainActivity2.this, mapView);
                    eee.setCameraPosition(lat, lon);
                    eee.showHide(true);
                    showWhereIAM = false;
                }
                eee.setPoint(location.getLatitude(), location.getLongitude());
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
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

    private void checkEnabled() {

    }

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
