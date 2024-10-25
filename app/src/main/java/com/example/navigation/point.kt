package com.example.navigation

import android.app.SearchManager
import android.content.Context
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.places.panorama.PanoramaService
import com.yandex.mapkit.places.panorama.PanoramaService.SearchListener
import com.yandex.mapkit.places.panorama.PanoramaService.SearchSession
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider

class EeE(context: Context, mapView: MapView) {

    fun setPoint(mapView: MapView, context: Context, latitude: Double, longitude: Double) {
        val circle = Circle(
            Point(latitude, longitude),
            20f
        )
        val placemark = mapView.map.mapObjects.addCircle(circle).apply {
            strokeWidth = 2f
            strokeColor = ContextCompat.getColor(context, R.color.red)
            fillColor = ContextCompat.getColor(context, R.color.red_alpfa)
        }
        setCameraPosition(mapView, latitude, longitude)
        mapView.map.addInputListener(inputListener)
    }

    fun setCameraPosition(mapView: MapView, latitude: Double, longitude: Double) {
        val geo = Geometry()
        val cameraCallback = object : Map.CameraCallback {
            override fun onMoveFinished(isFinished: Boolean) {
                // Handle camera move finished ...
            }
        }

        mapView.map.move(
            CameraPosition(
                /* target */ Point(latitude, longitude),
                /* zoom */ 17f,
                /* azimuth */ 0f,
                /* tilt */ 0f,
            ),
            Animation(Animation.Type.LINEAR, 1f),
            cameraCallback
        )
    }

    val searchSessionListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val city = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }
                ?.name
        }

        override fun onSearchError(p0: Error) {
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
        }

    }

    fun search(mapView: MapView, request: String, context: Context, latitude: Double, longitude: Double) {
        val searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.COMBINED
        )
        val point = Geometry.fromPoint(Point(latitude, longitude))
        val searchSession = searchManager!!.submit(request, point, SearchOptions(), searchSessionListener)

        setCameraPosition(mapView, latitude, longitude)
    }

    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val street = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.STREET) }
                ?.name ?: "Информация об улице не найдена"

            val polyline = Polyline()
            Toast.makeText(context, street, Toast.LENGTH_SHORT).show()
        }

        override fun onSearchError(p0: Error) {
        }
    }

    lateinit var searchSession1: Session

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            val searchManager =
                SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
            searchSession1 = searchManager.submit(point, 20, SearchOptions(), searchListener)


            mapView.map.addTapListener(tapListener)
        }

        override fun onMapLongTap(map: Map, point: Point) {}
    }

    private val tapListener = object : GeoObjectTapListener {
        override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
            val selectionMetadata: GeoObjectSelectionMetadata = geoObjectTapEvent
                .geoObject
                .metadataContainer
                .getItem(GeoObjectSelectionMetadata::class.java)
            mapView.map.selectGeoObject(selectionMetadata)
            return false
        }
    }
}