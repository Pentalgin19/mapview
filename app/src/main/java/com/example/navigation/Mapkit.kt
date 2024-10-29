package com.example.navigation

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.inputmethod.EditorInfo
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
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.FilterVehicleTypes
import com.yandex.mapkit.transport.masstransit.FitnessOptions
import com.yandex.mapkit.transport.masstransit.MasstransitRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.RouteOptions
import com.yandex.mapkit.transport.masstransit.SectionMetadata.SectionData
import com.yandex.mapkit.transport.masstransit.Session.RouteListener
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.mapkit.transport.masstransit.TransitOptions
import com.yandex.mapkit.transport.masstransit.Transport
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
    companion object {
        @JvmStatic
        var latitude: Double = 0.0000

        @JvmStatic
        var longitude: Double = 0.0000
    }

    private val mapView = mapView
    private var eee: EeE = EeE(context, mapView)
    private lateinit var mtRouter: MasstransitRouter

    private val mapkit = MapKitFactory.getInstance()
    private val trafficJams = mapkit.createTrafficLayer(mapView.mapWindow)
    private var locationMapkit = mapkit.createUserLocationLayer(mapView.mapWindow)
    private lateinit var searchManager: SearchManager
    private lateinit var session: Session
    private var check = false

    private var routeStartLocation = com.yandex.mapkit.geometry.Point(0.0000, 0.000)
    private var routeEndLocation =
        com.yandex.mapkit.geometry.Point(51.770846, 55.123653)//51.770846, 55.123653
    private val screenCenter = com.yandex.mapkit.geometry.Point(
        (routeStartLocation.latitude + routeEndLocation.latitude) / 2,
        (routeStartLocation.longitude + routeEndLocation.longitude) / 2,
    )
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null

    fun loc() {
        eee.showHide(false)
        locationMapkit.isVisible = true
        locationMapkit.setObjectListener(this)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapView.map.addCameraListener(this)
        searchEditText.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(searchEditText.text.toString())
            }
            false
        }
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

    var e = true
    fun submitQuery(query: String) {
        if (e) {
            session = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.mapWindow.map.visibleRegion),
                SearchOptions(),
                this
            )
            e = false
        }
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

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.accuracyCircle.fillColor = Color.TRANSPARENT
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onSearchResponse(response: Response) {
        mapView.map.mapObjects.clear()//работает, но с задержкой
        val mapObjectListener = mapView.map.mapObjects
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point!!
            mapObjectListener.addPlacemark(
                resultLocation,
                ImageProvider.fromResource(
                    context,
                    com.yandex.maps.mobile.R.drawable.search_layer_pin_icon_default
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

    override fun onCameraPositionChanged(map: Map, cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason, finished: Boolean) {
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
        var requestPoints: ArrayList<RequestPoint> = ArrayList()
        requestPoints.clear()
        requestPoints.add(RequestPoint(routeStartLocation, RequestPointType.WAYPOINT, null, null))
        requestPoints.add(RequestPoint(routeEndLocation, RequestPointType.WAYPOINT, null, null))
        drivingSession =
            drivingRouter!!.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)
    }

    fun setCarRoute() {
        mapView.mapWindow.map.mapObjects.clear()
        routeEndLocation = com.yandex.mapkit.geometry.Point(EeE.latitude, EeE.longitude)
        routeStartLocation = com.yandex.mapkit.geometry.Point(latitude, longitude)
        drivingRouter =
            DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        mapObjects = mapView.map.mapObjects.addCollection()

        submitRequest()
    }

    fun setWalkingRoute(){
        val transitOptions = TransitOptions(FilterVehicleTypes.NONE.value, TimeOptions())
        val avoidSteep = false
        val routeOptions = RouteOptions(FitnessOptions(avoidSteep))
        val points: MutableList<RequestPoint> = ArrayList()
        mapView.mapWindow.map.mapObjects.clear()
        points.clear()
        points.add(
            RequestPoint(
                Point(latitude, longitude), RequestPointType.WAYPOINT, null, null
            )
        )
        points.add(
            RequestPoint(
                Point(EeE.latitude, EeE.longitude), RequestPointType.WAYPOINT, null, null
            )
        )
        mtRouter = TransportFactory.getInstance().createMasstransitRouter()
        mtRouter.requestRoutes(points, transitOptions, routeOptions, masstransitRouter)
    }

    private fun drawSection(data: SectionData, geometry: Polyline) {
        val polylineMapObject = mapView.map.mapObjects.addPolyline(geometry)
        if (data.transports != null) {
            for (transport in data.transports!!) {
                if (transport.line.style != null) {
                    transport.line.style!!.color?.or(-0x1000000)
                    return
                }
            }
            val knownVehicleTypes = HashSet<String>()
            knownVehicleTypes.add("bus")
            knownVehicleTypes.add("tramway")
            for (transport in data.transports!!) {
                val sectionVehicleType = getVehicleType(transport, knownVehicleTypes)
                if (sectionVehicleType == "bus") {
                    polylineMapObject.setStrokeColor(-0xff0100) // Green
                    return
                } else if (sectionVehicleType == "tramway") {
                    polylineMapObject.setStrokeColor(-0x10000) // Red
                    return
                }
            }
//            polylineMapObject.setStrokeColor(-0xffff01) // Blue
        } else {
            polylineMapObject.setStrokeColor(-0x1000000) // Black
        }
    }

    private fun getVehicleType(transport: Transport, knownVehicleTypes: HashSet<String>): String? {
        for (type in transport.line.vehicleTypes) {
            if (knownVehicleTypes.contains(type)) {
                return type
            }
        }
        return null
    }

    private val masstransitRouter = object : RouteListener {
        override fun onMasstransitRoutes(p0: MutableList<Route>) {
            if (p0.size > 0) {
                for (section in p0[0].sections) {
                    drawSection(
                        section.metadata.data,
                        SubpolylineHelper.subpolyline(
                            p0[0].geometry, section.geometry
                        )
                    )
                }
            }
        }

        override fun onMasstransitRoutesError(error: Error) {
            var errorMessage: String? = "unknown error message"
            if (error is RemoteError) {
                errorMessage = "remote error message"
            } else if (error is NetworkError) {
                errorMessage = "network error message"
            }

            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

    }
}