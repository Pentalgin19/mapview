package com.example.navigation

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError

class Mapkit(
    mapView: MapView, val context: Context, val activity: Activity,
    val searchEditText: EditText
) : UserLocationObjectListener,
    Session.SearchListener, CameraListener, DrivingSession.DrivingRouteListener {
    val mapView = mapView

    val mapkit = MapKitFactory.getInstance()
    val trafficJams = mapkit.createTrafficLayer(mapView.mapWindow)
    var locationMapkit = mapkit.createUserLocationLayer(mapView.mapWindow)
    lateinit var searchManager: SearchManager
    lateinit var session: Session

    private val routeStartLocation = com.yandex.mapkit.geometry.Point(51.765334, 55.124147)
    private val routeEndLocation = com.yandex.mapkit.geometry.Point(51.770846, 55.123653)//
    private val screenCenter = com.yandex.mapkit.geometry.Point(
        (routeStartLocation.latitude + routeEndLocation.latitude) / 2,
        (routeStartLocation.longitude + routeEndLocation.longitude) / 2,
    )
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null

    fun loc() {
        locationMapkit.isVisible = false
        locationMapkit.setObjectListener(this)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapView.map.addCameraListener(this)
        searchEditText.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(searchEditText.text.toString())
            }
            false
        }
        drivingRouter =
            DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        mapObjects = mapView.map.mapObjects.addCollection()

        submitRequest()
    }

    private var t = false
    fun showTrafficJams() {
        if (t) {
            trafficJams.isTrafficVisible = false
            t = false
        } else {
            trafficJams.isTrafficVisible = true
            t = true
        }
    }

    fun submitQuery(query: String) {
        session = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.mapWindow.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION")
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                "android.permission.ACCESS_COARSE_LOCATION"
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
                ), 0
            )
            return
        }

    }

    fun showWhereIAm() {
        val l = mapkit.createUserLocationLayer(mapView.mapWindow)
        l.isVisible = true
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        locationMapkit.setAnchor(
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.5).toFloat()),
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(ImageProvider.fromResource(context, R.drawable.ic_me))
        val picIcon = userLocationView.pin.useCompositeIcon()
        picIcon.setIcon(
            "icon", ImageProvider.fromResource(context, R.drawable.ic_me),
            IconStyle().setAnchor(PointF(0f, 0f)).setRotationType(RotationType.ROTATE)
                .setZIndex(0f).setScale(1f)
        )
        picIcon.setIcon(
            "pin",
            ImageProvider.fromResource(
                context,
                R.drawable.ic_pin
            ),
            IconStyle().setAnchor(PointF(0.5f, 0.5f)).setRotationType(RotationType.ROTATE)
                .setZIndex(1f).setScale(0.5f)
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onSearchResponse(response: Response) {
        val mapObjectListener = mapView.map.mapObjects
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point!!
            mapObjectListener.addPlacemark(
                resultLocation,
                ImageProvider.fromResource(
                    context,
                    R.drawable.ic_pin
                )
            )

        }
    }

    override fun onSearchError(p0: Error) {
        var errorMessage = "Unknown error"
        if (p0 is RemoteError) {
            errorMessage = "Беспроводная ошибка"
        } else if (p0 is NetworkError) {
            errorMessage = "Network error"
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            submitQuery(searchEditText.text.toString())
        }
    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        for (route in p0) {
            mapObjects!!.addPolyline(route.geometry)
        }
    }

    override fun onDrivingRoutesError(p0: Error) {
        val error = "unknown error"
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    private fun submitRequest() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        val requestPoints: ArrayList<RequestPoint> = ArrayList()
        requestPoints.add(RequestPoint(routeStartLocation, RequestPointType.WAYPOINT, null, null))
        requestPoints.add(RequestPoint(routeEndLocation, RequestPointType.WAYPOINT, null, null))
        drivingSession = drivingRouter!!.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)
        Toast.makeText(context, "sdftyui", Toast.LENGTH_SHORT).show()
    }
}