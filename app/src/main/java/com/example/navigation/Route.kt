package com.example.navigation

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.yandex.mapkit.geometry.PolylinePosition
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.geometry.geo.PolylineIndex
import com.yandex.mapkit.geometry.geo.PolylineUtils
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.FilterVehicleTypes
import com.yandex.mapkit.transport.masstransit.FitnessOptions
import com.yandex.mapkit.transport.masstransit.MasstransitRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.RouteOptions
import com.yandex.mapkit.transport.masstransit.SectionMetadata.SectionData
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.Session.RouteListener
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.mapkit.transport.masstransit.TransitOptions
import com.yandex.mapkit.transport.masstransit.Transport
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlin.math.roundToInt


class Route(mapView: MapView, context: Context) : DrivingSession.DrivingRouteListener {

    companion object {
        @JvmStatic
        var walkRoute = false
        @JvmStatic
        var carRoute = false
        @JvmStatic
        var walkRoute1 = false
        @JvmStatic
        var carRoute1 = false
        @JvmStatic
        var carPolylineMapObject: PolylineMapObject? = null
        @JvmStatic
        var walkPolylineMapObject: PolylineMapObject? = null
        @JvmStatic
        var carPolylineMapObject1: PolylineMapObject? = null
        @JvmStatic
        var walkPolylineMapObject1: PolylineMapObject? = null
        @JvmStatic
        var tvTime: TextView? = null
        @JvmStatic
        var tvLenght: TextView? = null
    }

    val context = context
    val mapView = mapView
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var routeStartLocation = Point(0.0000, 0.000)
    private var routeEndLocation =
        Point(51.770846, 55.123653)
    var drivingSession: DrivingSession? = null
    private lateinit var mtRouter: MasstransitRouter
    var walkingSession: Session? = null

    fun setCarRoute1() {
        carRoute = true
        walkRoute = false
        carRoute1 = true
        walkRoute1 = false
        routeStartLocation = Point(PointObj.myLatitude, PointObj.myLongitude)
        routeEndLocation = Point(PointObj.selectedPointLatitude, PointObj.selectedPointLongitude)
        drivingRouter =
            DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        mapObjects = mapView.mapWindow.map.mapObjects.addCollection()

        submitRequest1()
    }

    fun setCarRoute() {
        carRoute = true
        walkRoute = false
        routeStartLocation = Point(PointObj.myLatitude, PointObj.myLongitude)
        routeEndLocation = Point(PointObj.selectedPointLatitude, PointObj.selectedPointLongitude)
        drivingRouter =
            DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        mapObjects = mapView.mapWindow.map.mapObjects.addCollection()

        submitRequest()
    }

    fun setWalkingRoute() {
        walkRoute = true
        carRoute = false
        val transitOptions = TransitOptions(FilterVehicleTypes.NONE.value, TimeOptions())
        val avoidSteep = false
        val routeOptions = RouteOptions(FitnessOptions(avoidSteep))
        val points: MutableList<RequestPoint> = ArrayList()
        points.clear()
        points.add(
            RequestPoint(
                Point(PointObj.myLatitude, PointObj.myLongitude),
                RequestPointType.WAYPOINT,
                null,
                null
            )
        )
        points.add(
            RequestPoint(
                Point(PointObj.selectedPointLatitude, PointObj.selectedPointLongitude),
                RequestPointType.WAYPOINT,
                null,
                null
            )
        )
        mtRouter = TransportFactory.getInstance().createMasstransitRouter()
        walkingSession =
            mtRouter.requestRoutes(points, transitOptions, routeOptions, masstransitRouter)
    }

    fun setWalkingRoute1() {
        walkRoute = true
        carRoute = false
        walkRoute1 = true
        carRoute1 = false
        val transitOptions = TransitOptions(FilterVehicleTypes.NONE.value, TimeOptions())
        val avoidSteep = false
        val routeOptions = RouteOptions(FitnessOptions(avoidSteep))
        val points: MutableList<RequestPoint> = ArrayList()
        points.clear()
        points.add(
            RequestPoint(
                Point(PointObj.myLatitude, PointObj.myLongitude),
                RequestPointType.WAYPOINT,
                null,
                null
            )
        )
        points.add(
            RequestPoint(
                Point(PointObj.selectedPointLatitude, PointObj.selectedPointLongitude),
                RequestPointType.WAYPOINT,
                null,
                null
            )
        )
        mtRouter = TransportFactory.getInstance().createMasstransitRouter()
        walkingSession =
            mtRouter.requestRoutes(points, transitOptions, routeOptions, masstransitRouter1)
    }

    private val masstransitRouter1 = object : RouteListener {
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

    private fun submitRequest1() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        var requestPoints: ArrayList<RequestPoint> = ArrayList()
        requestPoints.clear()
        requestPoints.add(RequestPoint(routeStartLocation, RequestPointType.WAYPOINT, null, null))
        requestPoints.add(RequestPoint(routeEndLocation, RequestPointType.WAYPOINT, null, null))
        drivingSession =
            drivingRouter!!.requestRoutes(requestPoints, drivingOptions, vehicleOptions, drSession)
    }

    private val drSession = object : DrivingSession.DrivingRouteListener {
        override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
            for (route in p0) {
                walkPolylineMapObject1 =
                    mapView.mapWindow.map.mapObjects.addPolyline(route.geometry)
                walkPolylineMapObject1!!.apply {
                    strokeWidth = 5f
                    setStrokeColor(ContextCompat.getColor(context, R.color.customBlue))
                    dashLength = 6f
                    gapLength = 6f
                }
            }

        }

        override fun onDrivingRoutesError(p0: Error) {
            val error = "unknown error"
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    private val masstransitRouter = object : RouteListener {
        override fun onMasstransitRoutes(p0: MutableList<Route>) {
            if (p0.size > 0) {
                for (section in p0[0].sections) {
                    drawSection1(
                        section.metadata.data,
                        SubpolylineHelper.subpolyline(
                            p0[0].geometry, section.geometry
                        )
                    )
                }
                var hours = 0.0
                var minutes = p0[0].metadata.weight.time.value / 60
                var times = ""
                if (minutes < 1){
                    times = "1 мин "
                }else if (minutes > 60) {
                    hours = minutes / 60
                    minutes -= hours * 60
                    times = "${hours.roundToInt()} ч ${minutes.roundToInt()} мин "
                } else {
                    times = minutes.roundToInt().toString() + " мин "
                }
                tvTime!!.text = times
                var length = p0[0].metadata.weight.walkingDistance.value
                if (length > 1000) {
                    length = (length / 1000).roundToInt().toDouble()
                    tvLenght!!.text = length.toString() + " км"
                } else {
                    tvLenght!!.text = length.roundToInt().toString() + " м"
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
        carPolylineMapObject = mapView.mapWindow.map.mapObjects.addPolyline(geometry)
        carPolylineMapObject!!.apply {
            strokeWidth = 5f
            setStrokeColor(ContextCompat.getColor(context, R.color.customBlue))
            dashLength = 6f
            gapLength = 6f
        }
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
                    carPolylineMapObject!!.setStrokeColor(-0xff0100) // Green
                    return
                } else if (sectionVehicleType == "tramway") {
                    carPolylineMapObject!!.setStrokeColor(-0x10000) // Red
                    return
                }
            }
//            polylineMapObject.setStrokeColor(-0xffff01) // Blue
        } else {
            carPolylineMapObject!!.setStrokeColor(-0x1000000) // Black
        }
    }

    private fun drawSection1(data: SectionData, geometry: Polyline) {
        carPolylineMapObject1 = mapView.mapWindow.map.mapObjects.addPolyline(geometry)
        carPolylineMapObject1!!.apply {
            strokeWidth = 5f
            setStrokeColor(ContextCompat.getColor(context, R.color.customBlue))
            dashLength = 6f
            gapLength = 6f
        }
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
                    carPolylineMapObject1!!.setStrokeColor(-0xff0100) // Green
                    return
                } else if (sectionVehicleType == "tramway") {
                    carPolylineMapObject1!!.setStrokeColor(-0x10000) // Red
                    return
                }
            }
//            polylineMapObject.setStrokeColor(-0xffff01) // Blue
        } else {
            carPolylineMapObject1!!.setStrokeColor(-0x1000000) // Black
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
        walkPolylineMapObject = mapView.mapWindow.map.mapObjects.addPolyline(p0[0].geometry)
        walkPolylineMapObject!!.apply {
            strokeWidth = 5f
            setStrokeColor(ContextCompat.getColor(context, R.color.customBlue))
            dashLength = 6f
            gapLength = 6f
        }
        var hours = 0.0
        var minutes = p0[0].metadata.weight.time.value / 60
        var times = ""
        if (minutes > 60) {
            hours = minutes / 60
            minutes -= hours * 60
            times = "${hours.roundToInt()} ч ${minutes.roundToInt()} мин "
        } else {
            times = minutes.roundToInt().toString() + " мин "
        }
        tvTime!!.text = times
        var length = p0[0].metadata.weight.distance.value
        if (length > 1000) {
            length = (length / 1000).roundToInt().toDouble()
            tvLenght!!.text = length.toString() + " км"
        } else {
            tvLenght!!.text = length.roundToInt().toString() + " м"
        }
    }

    override fun onDrivingRoutesError(p0: Error) {
        val error = "unknown error"
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

}