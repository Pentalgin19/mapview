package com.example.navigation

import android.content.Context
import com.yandex.mapkit.mapview.MapView

class ClearPoint(context1: Context, mapView1: MapView) {
    private val context = context1
    private val mapView = mapView1
    private val pointObj = PointObj(context, mapView)

    fun clearSetPoint() {
        mapView.mapWindow.map.mapObjects.clear()
        setMyPositionAndSelectedPoint()
    }

    private fun setMyPositionAndSelectedPoint() {
        pointObj.setPoint(PointObj.myLatitude, PointObj.myLongitude)
        pointObj.setSelectedPoint(PointObj.selectedPointLatitude, PointObj.selectedPointLongitude)

    }
}