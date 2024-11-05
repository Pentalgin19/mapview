package com.example.navigation

import android.content.Context
import android.widget.Toast
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.Flags
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.navigation.automotive.GuidanceListener
import com.yandex.mapkit.navigation.automotive.NavigationFactory
import com.yandex.mapkit.navigation.automotive.NavigationListener
import com.yandex.mapkit.navigation.automotive.RouteChangeReason
import com.yandex.mapkit.navigation.automotive.layer.NavigationLayerFactory
import com.yandex.mapkit.navigation.automotive.layer.RouteView
import com.yandex.mapkit.navigation.automotive.layer.RouteViewListener
import com.yandex.mapkit.navigation.automotive.layer.RoutesSource
import com.yandex.mapkit.navigation.automotive.layer.styling.BalloonImageProvider
import com.yandex.mapkit.navigation.automotive.layer.styling.JamStyle
import com.yandex.mapkit.navigation.automotive.layer.styling.NavigationStyleProvider
import com.yandex.mapkit.navigation.automotive.layer.styling.RequestPointStyleProvider
import com.yandex.mapkit.navigation.automotive.layer.styling.RoutePinsStyleProvider
import com.yandex.mapkit.navigation.automotive.layer.styling.RouteStyle
import com.yandex.mapkit.navigation.automotive.layer.styling.RouteViewStyleProvider
import com.yandex.mapkit.navigation.automotive.layer.styling.UserPlacemarkStyleProvider
import com.yandex.mapkit.road_events.EventTag
import com.yandex.mapkit.road_events_layer.HighlightCircleStyle
import com.yandex.mapkit.road_events_layer.HighlightMode
import com.yandex.mapkit.road_events_layer.RoadEventStyle
import com.yandex.mapkit.road_events_layer.RoadEventStylingProperties
import com.yandex.mapkit.road_events_layer.RoadEventsLayer
import com.yandex.mapkit.road_events_layer.RoadEventsLayerListener
import com.yandex.mapkit.road_events_layer.StyleProvider
import com.yandex.mapkit.styling.ArrowStyle
import com.yandex.mapkit.styling.PolylineStyle
import com.yandex.mapkit.styling.automotivenavigation.AutomotiveNavigationStyleProvider

class NavKit(context: Context, mapView: MapView) {
    val context = context
    val mapView = mapView
    val navigation = NavigationFactory.createNavigation(DrivingRouterType.COMBINED)
    fun optimalRoute() {
        val requestPoints = listOf(
            RequestPoint(
                Point(PointObj.myLatitude, PointObj.myLongitude),
                RequestPointType.WAYPOINT,
                null,
                null
            ),
            RequestPoint(Point(51.766262, 55.127119), RequestPointType.WAYPOINT, null, null),
        )
        navigation.requestRoutes(
            requestPoints,
            navigation.guidance.location?.heading,
            3,
        )
    }


    fun navListener() {
        val navigationListener = object : NavigationListener {
            override fun onRoutesRequested(p0: MutableList<RequestPoint>) {

            }

            override fun onAlternativesRequested(p0: DrivingRoute) {
                TODO("Not yet implemented")
            }

            override fun onUriResolvingRequested(p0: String) {
                TODO("Not yet implemented")
            }

            override fun onRoutesBuilt() {
                val routes = navigation.routes
                val fastestRoute = routes[0]
            }

            override fun onRoutesRequestError(p0: com.yandex.runtime.Error) {
                TODO("Not yet implemented")
            }

            override fun onResetRoutes() {
                TODO("Not yet implemented")
            }
        }
        navigation.addListener(navigationListener)
    }

    fun guidanceListener() {
        val guidanceListener = object : GuidanceListener {
            override fun onLocationChanged() = Unit
            override fun onCurrentRouteChanged(reason: RouteChangeReason) = Unit
            override fun onRouteLost() = Unit
            override fun onReturnedToRoute() = Unit
            override fun onRouteFinished() = Unit
            override fun onWayPointReached() = Unit
            override fun onStandingStatusChanged() = Unit
            override fun onRoadNameChanged() = Unit
            override fun onSpeedLimitUpdated() = Unit
            override fun onSpeedLimitStatusUpdated() = Unit
            override fun onAlternativesChanged() = Unit
            override fun onFastestAlternativeChanged() = Unit
        }
    }

    val styleProvider = object : StyleProvider {
        override fun provideStyle(
            p0: RoadEventStylingProperties,
            p1: Boolean,
            p2: Float,
            p3: RoadEventStyle
        ): Boolean {
            return true
        }

        override fun provideHighlightCircleStyle(
            p0: Boolean,
            p1: HighlightMode
        ): HighlightCircleStyle? {
            return null
        }

    }
    val roadEventsLayer = MapKitFactory.getInstance().createRouteRoadEventsLayer(mapView.mapWindow, styleProvider)
    val road = object : RoadEventsLayer {
        override fun addListener(p0: RoadEventsLayerListener) {

        }

        override fun removeListener(p0: RoadEventsLayerListener) {
        }

        override fun selectRoadEvent(p0: String) {
        }

        override fun deselectRoadEvent() {
        }

        override fun setRoadEventVisibleOnRoute(p0: EventTag, p1: Boolean) {
        }

        override fun isValid(): Boolean {
            return mapView.mapWindow.isValid
        }
    }

    private val styleP = AutomotiveNavigationStyleProvider(context)
    private val routeViewStyleProvider = styleP.routeViewStyleProvider()

    val navigationStyleProvider = object : NavigationStyleProvider {
        override fun balloonImageProvider(): BalloonImageProvider {
            return styleP.balloonImageProvider()
        }

        override fun requestPointStyleProvider(): RequestPointStyleProvider {
            return styleP.requestPointStyleProvider()
        }

        override fun userPlacemarkStyleProvider(): UserPlacemarkStyleProvider {
            return styleP.userPlacemarkStyleProvider()
        }

        override fun routePinsStyleProvider(): RoutePinsStyleProvider {
            return styleP.routePinsStyleProvider()
        }

        override fun routeViewStyleProvider(): RouteViewStyleProvider {

            return object : RouteViewStyleProvider {

                override fun provideJamStyle(p0: Flags, p1: Boolean, p2: Boolean, p3: JamStyle) {

                }

                override fun providePolylineStyle(p0: Flags, p1: Boolean, p2: Boolean, p3: PolylineStyle) {

                }

                override fun provideManoeuvreStyle(p0: Flags, p1: Boolean, p2: Boolean, p3: ArrowStyle) {
                }

                override fun provideRouteStyle(flags: Flags, isSelected: Boolean,
                                               isNightMode: Boolean, routeStyle: RouteStyle) {
                    routeViewStyleProvider.provideRouteStyle(flags, isSelected, isNightMode, routeStyle)
                    // Only the current route will display traffic jams
                    routeStyle.setShowJams(isSelected)
                }
            }
        }

    }

    val navigationLayer = NavigationLayerFactory.createNavigationLayer(mapView.mapWindow, roadEventsLayer,
        navigationStyleProvider, navigation)
    fun y(){
        Toast.makeText(context, "j", Toast.LENGTH_SHORT).show()
    }

    fun u (){
        val routeViewListener = object : RouteViewListener {
            override fun onRouteViewTap(routeView: RouteView) {
                when (navigationLayer.routesSource) {
                    RoutesSource.NAVIGATION -> navigationLayer.selectRoute(routeView)
                    RoutesSource.GUIDANCE -> navigationLayer.navigation.guidance.switchToRoute(routeView.route)
                }
            }

            override fun onRouteViewsChanged() {
                if (navigationLayer.selectedRoute() != null) return
                val route = navigationLayer.routes.firstOrNull() ?: return
                navigationLayer.selectRoute(route)
            }
        }
        navigationLayer.addRouteViewListener(routeViewListener)

    }

}
