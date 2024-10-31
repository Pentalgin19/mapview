package com.example.navigation

import android.content.Context
import android.widget.Toast
import com.example.navigation.Mapkit.Companion.latitude
import com.example.navigation.Mapkit.Companion.longitude
import com.example.navigation.Mapkit.Companion.type
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
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
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
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError


class Route(mapView: MapView, context: Context) : DrivingSession.DrivingRouteListener{

    companion object{
        @JvmStatic var walkRoute = false
        @JvmStatic var carRoute = false
    }
    val context = context
    val mapView = mapView
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var routeStartLocation = Point(0.0000, 0.000)
    private var routeEndLocation =
        Point(51.770846, 55.123653)//51.770846, 55.123653
    private var drivingSession: DrivingSession? = null
    private lateinit var mtRouter: MasstransitRouter

    fun setCarRoute() {
        carRoute = true
        walkRoute = false
        routeEndLocation = Point(EeE.latitude, EeE.longitude)
        routeStartLocation = Point(latitude, longitude)
        drivingRouter =
            DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        mapObjects = mapView.mapWindow.map.mapObjects.addCollection()

        submitRequest()
    }

    private val clearPoint = ClearPoint(context, mapView)
    fun setWalkingRoute(){
        walkRoute = true
        carRoute = false
        clearPoint.clearSetPoint()
        val transitOptions = TransitOptions(FilterVehicleTypes.NONE.value, TimeOptions())
        val avoidSteep = false
        val routeOptions = RouteOptions(FitnessOptions(avoidSteep))
        val points: MutableList<RequestPoint> = ArrayList()
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

    private fun drawSection(data: SectionData, geometry: Polyline) {
        val polylineMapObject = mapView.mapWindow.map.mapObjects.addPolyline(geometry)
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

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        for (route in p0) {
            mapView.mapWindow.map.mapObjects.addPolyline(route.geometry)
        }
    }

    override fun onDrivingRoutesError(p0: Error) {
        val error = "unknown error"
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }
}