package com.example.navigation

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.Showtime
import com.yandex.mapkit.search.Snippet
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError


class EeE(context: Context, mapView: MapView) {

    lateinit var cardView: LinearLayout
    private val mapView = mapView
    lateinit var tvLatitude: TextView
    lateinit var tvLongutude: TextView
    var message = ""
    lateinit var someInformation: TextView
    val finalTarget = Point(51.768996, 55.100944)
    private val context = context

    companion object {
        @JvmStatic
        var latitude = 0.000

        @JvmStatic
        var longitude = 0.000

        @JvmStatic
        var selectedPointLatitude = 0.0000

        @JvmStatic
        var selectedPointLongitude: Double = 0.00000
    }

    private var placemark = mapView.map.mapObjects.addPlacemark().apply {
        geometry = Point(latitude, longitude)
        setIcon(ImageProvider.fromResource(context, R.drawable.selected_location))
    }

    fun setPoint(latitude: Double, longitude: Double) {
        mapView.mapWindow.map.mapObjects.clear()
        placemark = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(latitude, longitude)
            setIcon(ImageProvider.fromResource(context, R.drawable.ic_me))
        }
    }

    fun addTapAndInputListener(){
        mapView.map.addTapListener(tapListener)
        mapView.map.addInputListener(inputListener)
    }

    fun showHide(show: Boolean) {
        mapView.mapWindow.map.mapObjects.clear()
        placemark = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(Mapkit.latitude, Mapkit.longitude)
            setIcon(ImageProvider.fromResource(context, R.drawable.ic_me))
        }
        if (show) {
            placemark.setIconStyle(
                IconStyle().apply {
                    visible = true
                }
            )
        } else {
            placemark.setIconStyle(
                IconStyle().apply {
                    visible = false
                }
            )
        }
    }

    fun setCameraPosition(latitude: Double, longitude: Double) {
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

    private val tapListener = object : GeoObjectTapListener {
        override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
            val selectionMetadata: GeoObjectSelectionMetadata = geoObjectTapEvent
                .geoObject
                .metadataContainer
                .getItem(GeoObjectSelectionMetadata::class.java)
            mapView.map.selectGeoObject(selectionMetadata)
            latitude = geoObjectTapEvent.geoObject.geometry[0].point!!.latitude
            longitude = geoObjectTapEvent.geoObject.geometry[0].point!!.longitude
            cardView.visibility = View.VISIBLE
            return false
        }
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
                ?:  ""
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

            if (house != "" && street != ""){
                someInformation.text = "$street, $house"
            }else if (house != ""){
                someInformation.text = house
            }else if (street != ""){
                someInformation.text = street
            }else if (city != "" && country != ""){
                tvLatitude.text = latitude.toString()
                tvLongutude.text = longitude.toString()
                someInformation.text = "$city, $country"
            }else if (city != ""){
                tvLatitude.text = latitude.toString()
                tvLongutude.text = longitude.toString()
                someInformation.text = city
            }else if (place != "" && country != ""){
                tvLatitude.text = latitude.toString()
                tvLongutude.text = longitude.toString()
                someInformation.text = "$place, $country"
            }else if (place != ""){
                tvLatitude.text = latitude.toString()
                tvLongutude.text = longitude.toString()
                someInformation.text = place
            }else if (country != ""){
                tvLatitude.text = latitude.toString()
                tvLongutude.text = longitude.toString()
                someInformation.text = country
            }else if (hydro != ""){
                tvLatitude.text = latitude.toString()
                tvLongutude.text = longitude.toString()
                someInformation.text = hydro
            }
            val polyline = Polyline()
        }

        override fun onSearchError(p0: Error) {
        }
    }

    lateinit var searchSession1: Session
    private val route = Route(mapView, context)

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            val searchManager =
                SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
            searchSession1 = searchManager.submit(point, 20, SearchOptions(), searchListener)
        }

        override fun onMapLongTap(map: Map, point: Point) {
            selectedPointLatitude = point.latitude
            selectedPointLongitude = point.longitude
            mapView.mapWindow.map.mapObjects.clear()
            setPoint(Mapkit.latitude, Mapkit.longitude)

            if (Mapkit.type == 1){
                route.setWalkingRoute()
            }else if (Mapkit.type == 2){
                route.setCarRoute()
            }

            val searchManager =
                SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
            searchSession1 = searchManager.submit(point, 20, SearchOptions(), searchListener)

            setSelectedPoint(point.latitude, point.longitude)

            latitude = placemark.geometry.latitude
            longitude = placemark.geometry.longitude
            cardView.visibility = View.VISIBLE
            tvLatitude.text = latitude.toString()
            tvLongutude.text = longitude.toString()
        }
    }

    fun setSelectedPoint(latitude: Double, longitude: Double) {
        placemark = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(latitude, longitude)
            setIcon(ImageProvider.fromResource(context, R.drawable.selected_location))
        }
    }

    private var searchSession: Session? = null
    lateinit var editText: EditText

    private fun submitQuery(query: String) {
        val searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.COMBINED
        )
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView!!.map.visibleRegion),
            SearchOptions(),
            searchListener1
        )
    }

    val searchListener1 = object : Session.SearchListener {
        override fun onSearchResponse(p0: Response) {
            val mapObjects = mapView!!.mapWindow.map.mapObjects
            mapObjects.clear()
            val searchResultImageProvider =
                ImageProvider.fromResource(context, R.drawable.ic_pin)
            for (searchResult in p0.collection.children) {
                val resultLocation = searchResult.obj!!.geometry[0].point
                if (resultLocation != null) {
                    mapObjects.addPlacemark { placemark: PlacemarkMapObject ->
                        placemark.geometry = resultLocation
                        placemark.setIcon(searchResultImageProvider)
                    }
                }
            }
        }

        override fun onSearchError(p0: Error) {
            var errorMessage = "Unknown error"
            if (p0 is RemoteError) {
                errorMessage = "Remote server error"
            } else if (p0 is NetworkError) {
                errorMessage = "Network error"
            }

            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPosition = object : CameraListener {
        override fun onCameraPositionChanged(
            map: Map,
            cameraPosition: CameraPosition,
            cameraUpdateReason: CameraUpdateReason,
            finished: Boolean
        ) {
            if (finished) {
                submitQuery(editText!!.text.toString())
            }
        }
    }
}