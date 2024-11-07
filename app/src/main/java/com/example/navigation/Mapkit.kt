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
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
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
    Session.SearchListener, CameraListener {
    companion object {
        @JvmStatic
        var latitude: Double = 0.0000
        @JvmStatic
        var longitude: Double = 0.0000
    }

    private val mapView = mapView
    private var pointObj: PointObj = PointObj(context, mapView)
    private val route = Route(mapView, context)

    private val mapkit = MapKitFactory.getInstance()
    private val trafficJams = mapkit.createTrafficLayer(mapView.mapWindow)
    private var locationMapkit = mapkit.createUserLocationLayer(mapView.mapWindow)
    private lateinit var searchManager: SearchManager
    private lateinit var session: Session

    private var routeStartLocation = Point(0.0000, 0.000)
    private var routeEndLocation = Point(51.770846, 55.123653)//51.770846, 55.123653
    private val screenCenter = Point(
        (routeStartLocation.latitude + routeEndLocation.latitude) / 2,
        (routeStartLocation.longitude + routeEndLocation.longitude) / 2,
    )

    fun subQuery(string: String) {
        locationMapkit.setObjectListener(this)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapView.mapWindow.map.addCameraListener(this)
        searchEditText.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
                if (string == "Оренбургский колледж экономики и информатики " ||
                    string == "ОКЭИ " ||
                    string == "ОКЭИ 105 кабинет "
                ) {
                    Toast.makeText(context, "iii", Toast.LENGTH_SHORT).show()
                    submitQuery("Оренбург, улица Чкалова 11")
                } else {
                    submitQuery(searchEditText.text.toString())
                }
            }
            if (string == "Оренбургский колледж экономики и информатики " ||
                string == "ОКЭИ " ||
                string == "ОКЭИ 105 кабинет "
            ) {
                Toast.makeText(context, "iii", Toast.LENGTH_SHORT).show()
                submitQuery("Оренбург, улица Чкалова 11")
                PointObj.selectedPointLatitude = 51.765334
                PointObj.selectedPointLongitude = 55.124147
                route.setWalkingRoute()
            } else {
                submitQuery(searchEditText.text.toString())
            }
            false
        }
    }

    fun showUserPin() {
        locationMapkit.isVisible = true
        locationMapkit.isHeadingEnabled = true
        locationMapkit.setObjectListener(this)
    }

    fun closeUserPin() {
        locationMapkit.isVisible = false
        locationMapkit.isHeadingEnabled = false
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
            ) != PackageManager.PERMISSION_GRANTED
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
        val icon = R.drawable.ic_me
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(context, icon),
            IconStyle().setRotationType(RotationType.ROTATE)
        )
        userLocationView.accuracyCircle.fillColor = Color.TRANSPARENT
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onSearchResponse(response: Response) {
        val mapObjectListener = mapView.mapWindow.map.mapObjects
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point!!
            mapObjectListener.addPlacemark(
                resultLocation,
                ImageProvider.fromResource(
                    context,
                    com.yandex.maps.mobile.R.drawable.search_layer_pin_icon_default
                )
            )
            pointObj.setCameraPosition(resultLocation.latitude, resultLocation.longitude)
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
        map: Map, cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason, finished: Boolean
    ) {
        if (finished) {
            submitQuery(searchEditText.text.toString())
        }
    }

    fun setCarRoute() {
        route.setCarRoute()
    }

    fun setWalkingRoute() {
        route.setWalkingRoute()
    }
}