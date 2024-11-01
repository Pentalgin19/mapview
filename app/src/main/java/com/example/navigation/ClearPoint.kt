package com.example.navigation

import android.content.Context
import com.yandex.mapkit.mapview.MapView

class ClearPoint(context1: Context, mapView1: MapView) {
    private val context = context1
    private val mapView = mapView1
    private val eee = EeE(context, mapView)

    fun clearSetPoint() {
        mapView.mapWindow.map.mapObjects.clear()
        setMyPositionAndSelectedPoint()
    }

    private fun setMyPositionAndSelectedPoint() {
        eee.setPoint(EeE.myLatitude, EeE.myLongitude)
        eee.setSelectedPoint(EeE.selectedPointLatitude, EeE.selectedPointLongitude)

    }
}