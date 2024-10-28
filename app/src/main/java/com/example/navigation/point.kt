package com.example.navigation

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
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
    private val mapView = mapView
    val finalTarget = Point(51.768996, 55.100944)
    private val context = context

    companion object {
        @JvmStatic
        var latitude = 0.000

        @JvmStatic
        var longitude = 0.000
    }

    private lateinit var placemark: PlacemarkMapObject

    fun setPoint(latitude: Double, longitude: Double) {
        val circle = Circle(
            Point(latitude, longitude),
            20f
        )
        mapView.map.addTapListener(tapListener)
        mapView.map.addInputListener(inputListener)
        placemark = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(Mapkit.latitude, Mapkit.longitude)
            setIcon(ImageProvider.fromResource(context, R.drawable.ic_me))
        }
    }

    fun showHide(show: Boolean) {
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

    val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
        override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
            Toast.makeText(context, drivingRoutes.toString(), Toast.LENGTH_SHORT).show()
        }

        override fun onDrivingRoutesError(error: Error) {
            Toast.makeText(context, "bad", Toast.LENGTH_SHORT).show()
        }
    }

    val searchSessionListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            Toast.makeText(context, response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.STREET) }
                ?.name
                ?: "No fuel", Toast.LENGTH_SHORT).show()
        }

        override fun onSearchError(p0: Error) {
            Toast.makeText(context, "$p0", Toast.LENGTH_SHORT).show()
        }
    }


    fun search(request: String) {
        val searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.ONLINE
        )
        val point = Geometry.fromPoint(Point(59.95, 30.32))
        val options = SearchOptions()
        options.snippets = Snippet.PHOTOS.value
        val searchSession = searchManager!!.submit(request, point, options, searchSessionListener)
    }

    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val place = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.HOUSE) }
                ?.name
                ?: response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.components
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.STREET) }
                    ?.name
                ?: response.collection.children.firstOrNull()?.obj
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
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.COUNTRY) }
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
                    ?.firstOrNull { it.kinds.contains(Address.Component.Kind.HYDRO) }
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
                ?: "я ваще хз что это"
            val polyline = Polyline()
            Toast.makeText(context, place, Toast.LENGTH_SHORT).show()
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


        }

        override fun onMapLongTap(map: Map, point: Point) {
            val searchManager =
                SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
            searchSession1 = searchManager.submit(point, 20, SearchOptions(), searchListener)
            val placemark = mapView.map.mapObjects.addPlacemark().apply {
                geometry = Point(Mapkit.latitude, Mapkit.longitude)
                setIcon(ImageProvider.fromResource(context, R.drawable.ic_pin))
            }
            Toast.makeText(context, "point", Toast.LENGTH_SHORT).show()
        }
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
            Toast.makeText(context, "text", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private var searchSession: Session? = null
    lateinit var editText: EditText

    private fun submitQuery(query: String) {
        val searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.ONLINE
        )
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView!!.map.visibleRegion),
            SearchOptions(),
            searchListener1
        )
    }

    fun q() {
        mapView!!.map.addCameraListener(cameraPosition)
        editText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(editText.text.toString())
            }
            false
        })

        mapView.map.move(
            CameraPosition(Point(50.5, 14.5), 14.0f, 0.0f, 0.0f),
            Animation(Animation.Type.LINEAR, 14f),
            null
        )

        submitQuery(editText.text.toString())
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