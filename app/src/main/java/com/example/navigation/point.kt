package com.example.navigation

import android.content.Context
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.places.panorama.PanoramaService
import com.yandex.mapkit.places.panorama.PanoramaService.SearchListener
import com.yandex.mapkit.places.panorama.PanoramaService.SearchSession
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider

class EeE {
    fun setPoint(mapView: MapView, context: Context, latitude: Double, longitude: Double) {
        val placemark = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(latitude, longitude)
            setIcon(ImageProvider.fromResource(context, R.drawable.ic_pin))
        }

        setCameraPosition(mapView, latitude, longitude)
    }

    fun setCameraPosition(mapView: MapView, latitude: Double, longitude: Double) {
        val geo = Geometry()
        val cameraCallback = object : Map.CameraCallback {
            override fun onMoveFinished(isFinished: Boolean) {
                // Handle camera move finished ...
            }
        }

        mapView.map.move(CameraPosition(
            /* target */ Point(latitude, longitude),
            /* zoom */ 17f,
            /* azimuth */ 0f,
            /* tilt */ 0f,
        ) ,Animation(Animation.Type.LINEAR, 1f),
        cameraCallback
        )
    }

    val searchSessionListener = object : Session.SearchListener{
        override fun onSearchResponse(p0: Response) {

        }

        override fun onSearchError(p0: Error) {

        }

    }
    fun search(mapView: MapView){
        val searchManager =
            SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        val searchOptions = SearchOptions().apply {
            searchTypes = 2
            resultPageSize = 32
        }

        val session = searchManager.submit(
            "where to eat",
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            searchOptions,
            searchSessionListener,
        )

    }
}