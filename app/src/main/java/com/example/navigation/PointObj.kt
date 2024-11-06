package com.example.navigation

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider


class PointObj(context: Context, mapView: MapView) {

    lateinit var cardView: LinearLayout
    lateinit var info: LinearLayout
    lateinit var cardViewFilter: CardView
    private val mapView = mapView
    lateinit var tvLatitude: TextView
    lateinit var tvLongutude: TextView
    lateinit var someInformation: TextView
    private val context = context

    companion object {
        @JvmStatic var myLatitude = 0.0
        @JvmStatic var myLongitude = 0.0
        @JvmStatic var latitude = 0.0
        @JvmStatic var longitude = 0.0
        @JvmStatic var selectedPointLatitude = 0.0
        @JvmStatic var selectedPointLongitude = 0.0
        @JvmStatic var zoom: Float = 0f
    }

    private var placemark = mapView.mapWindow.map.mapObjects.addPlacemark().apply {
        geometry = Point(latitude, longitude)
        setIcon(ImageProvider.fromResource(context, R.drawable.selected_location))
    }

    fun deleteCurrentPointPosition() {
        if (placemark.isValid) {
            mapView.mapWindow.map.mapObjects.remove(placemark as MapObject)
        }
    }
    fun deleteWalkingRoute(){
        if (Route.carPolylineMapObject != null){
            if (Route.carPolylineMapObject!!.isValid){
                mapView.mapWindow.map.mapObjects.remove(Route.carPolylineMapObject!!)
            }
        }
    }
    fun deleteCarRoute(){
        if (Route.walkPolylineMapObject != null){
            if (Route.walkPolylineMapObject!!.isValid){
                mapView.mapWindow.map.mapObjects.remove(Route.walkPolylineMapObject!!)
            }
        }
    }
    fun deleteWalkingRoute1(){
        if (Route.carPolylineMapObject1 != null){
            if (Route.carPolylineMapObject1!!.isValid){
                mapView.mapWindow.map.mapObjects.remove(Route.carPolylineMapObject1 as MapObject)
            }
        }
    }
    fun deleteCarRoute1(){
        if (Route.walkPolylineMapObject1 != null){
            if (Route.walkPolylineMapObject1!!.isValid){
                mapView.mapWindow.map.mapObjects.remove(Route.walkPolylineMapObject1 as MapObject)
            }
        }
    }
    fun deleteAllRoute(){
        deleteWalkingRoute()
        deleteCarRoute()
    }

    fun setPoint(latitude: Double, longitude: Double) {
        placemark = mapView.mapWindow.map.mapObjects.addPlacemark().apply {
            geometry = Point(latitude, longitude)
            setIcon(ImageProvider.fromResource(context, R.drawable.ic_me))
        }
    }

    fun setCameraPosition(latitude: Double, longitude: Double) {
        val cameraCallback = object : Map.CameraCallback {
            override fun onMoveFinished(isFinished: Boolean) {
                // Handle camera move finished ...
            }
        }

        mapView.mapWindow.map.move(
            CameraPosition(
                /* target */ Point(latitude, longitude),
                /* zoom */ 17f + zoom,
                /* azimuth */ 0f,
                /* tilt */ 0f,
            ),
            Animation(Animation.Type.LINEAR, 1f),
            cameraCallback
        )
    }

    fun addTapAndInputListener() {
        mapView.mapWindow.map.addInputListener(inputListener)
        mapView.mapWindow.map.addTapListener(tapListener)
    }

    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val place = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.AREA) }
                ?.name
                ?: response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.REGION) }
                    ?.name
                ?: response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }
                    ?.name
                ?: response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.OTHER) }
                    ?.name
                ?: response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.AIRPORT) }
                    ?.name
                ?: response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.VEGETATION) }
                    ?.name
                ?: response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.METRO_STATION) }
                    ?.name
                ?: response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.PROVINCE) }
                    ?.name
                ?: response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.RAILWAY_STATION) }
                    ?.name
                ?: ""
            val country = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.COUNTRY) }
                ?.name
                ?: ""
            val hydro = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.HYDRO) }
                ?.name
                ?: ""
            val house = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.HOUSE) }
                ?.name
                ?: ""
            val street = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.STREET) }
                ?.name
                ?: ""
            val city = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }
                ?.name
                ?: ""

            if (house != "" && street != "") {
                someInformation.text = "$street, $house"
            } else if (house != "") {
                someInformation.text = house
            } else if (street != "") {
                someInformation.text = street
            } else if (city != "" && country != "") {
                tvLatitude.text = latitude.toString() + ","
                tvLongutude.text = longitude.toString()
                someInformation.text = "$city, $country"
            } else if (city != "") {
                tvLatitude.text = latitude.toString() + ","
                tvLongutude.text = longitude.toString()
                someInformation.text = city
            } else if (place != "" && country != "") {
                tvLatitude.text = latitude.toString() + ","
                tvLongutude.text = longitude.toString()
                someInformation.text = "$place, $country"
            } else if (place != "") {
                tvLatitude.text = latitude.toString() + ","
                tvLongutude.text = longitude.toString()
                someInformation.text = place
            } else if (country != "") {
                tvLatitude.text = latitude.toString() + ","
                tvLongutude.text = longitude.toString()
                someInformation.text = country
            } else if (hydro != "") {
                tvLatitude.text = latitude.toString() + ","
                tvLongutude.text = longitude.toString()
                someInformation.text = hydro
            }
            val polyline = Polyline()
        }

        override fun onSearchError(p0: Error) {
        }
    }

    private val tapListener = object : GeoObjectTapListener {
        override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
            val selectionMetadata: GeoObjectSelectionMetadata = geoObjectTapEvent
                .geoObject
                .metadataContainer
                .getItem(GeoObjectSelectionMetadata::class.java)
            mapView.mapWindow.map.selectGeoObject(selectionMetadata)

            selectedPointLatitude = geoObjectTapEvent.geoObject.geometry[0].point!!.latitude
            selectedPointLongitude = geoObjectTapEvent.geoObject.geometry[0].point!!.longitude
            cardView.visibility = View.VISIBLE
            return false
        }
    }

    lateinit var searchSession1: Session

    val route = Route(mapView, context)
    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            cardViewFilter.visibility = View.GONE
            info.visibility = View.VISIBLE;
            selectedPointLatitude = point.latitude
            selectedPointLongitude = point.longitude
            isRouteHave()
            setSelectedPoint(point.latitude, point.longitude)
            val searchManager =
                SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
            searchSession1 = searchManager.submit(point, 20, SearchOptions(), searchListener)

            latitude = point.latitude
            longitude = point.longitude
            tvLatitude.text = latitude.toString()
            tvLongutude.text = longitude.toString()
        }

        override fun onMapLongTap(map: Map, point: Point) {
            cardViewFilter.visibility = View.GONE
            info.visibility = View.VISIBLE;
            selectedPointLatitude = point.latitude
            selectedPointLongitude = point.longitude
            isRouteHave()
            setSelectedPoint(point.latitude, point.longitude)

            val searchManager =
                SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
            searchSession1 = searchManager.submit(point, 20, SearchOptions(), searchListener)

            latitude = point.latitude
            longitude = point.longitude
            cardView.visibility = View.VISIBLE
            tvLatitude.text = latitude.toString()
            tvLongutude.text = longitude.toString()
        }
    }

    private fun isRouteHave() {
        deleteSelectedPoint()
    }

    private fun deleteSelectedPoint() {
        if (selectedPoint != null){
            if (selectedPoint.isValid){
                mapView.mapWindow.map.mapObjects.remove(selectedPoint)
            }
        }
    }

    var selectedPoint: PlacemarkMapObject = mapView.mapWindow.map.mapObjects.addPlacemark()

    fun setSelectedPoint(latitude: Double, longitude: Double) {
        if (latitude != 0.0 && longitude != 0.0) {
            selectedPoint = mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                geometry = Point(latitude, longitude)
                setIcon(ImageProvider.fromResource(context, R.drawable.selected_location))
            }
        }
    }
}