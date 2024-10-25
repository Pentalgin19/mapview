package com.example.navigation;

import static android.provider.Settings.System.getString;
import static com.example.navigation.MainActivityKt.mapView;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

public class SearchJava implements Session.SearchListener, CameraListener{
    private Context context;
    void setContext(Context context1){
        context = context1;
    }
    private EditText searchEdit;
    void setSearchEdit(EditText searchEdit1){
        searchEdit = searchEdit1;
    }
    private MapView mapView;
    void setMapView(MapView mapView1){
        mapView = mapView1;
    }
    Session searchSession;
    SearchManager searchManager;
    Session.SearchListener searchListener;
    void setSearchSession(Session searchSession1){
        searchSession = searchSession1;
    }
    void setSearchManager(SearchManager searchManager1){
        searchManager = searchManager1;
    }
    void setSearchListener(Session.SearchListener searchListener1){
        searchListener = searchListener1;
    }

    void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMapWindow().getMap().getVisibleRegion()),
                new SearchOptions(),
                searchListener
        );
    }

    @Override
    public void onSearchResponse(Response response) {
        MapObjectCollection mapObjects = mapView.getMapWindow().getMap().getMapObjects();
        mapObjects.clear();
        final ImageProvider searchResultImageProvider = ImageProvider.fromResource(context, R.drawable.ic_pin);
        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            final Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                mapObjects.addPlacemark(placemark -> {
                    placemark.setGeometry(resultLocation);
                    placemark.setIcon(searchResultImageProvider);
                });
            }
        }
    }

    @Override
    public void onSearchError(Error error) {
        String errorMessage = "Unknown error";
        if (error instanceof RemoteError) {
            errorMessage = "Remote server error";
        } else if (error instanceof NetworkError) {
            errorMessage = "Network error";
        }

        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPositionChanged(Map map, CameraPosition cameraPosition, CameraUpdateReason cameraUpdateReason,
                                        boolean finished) {
        {
            if (finished) {
                submitQuery(searchEdit.getText().toString());
            }
        }
    }
}
