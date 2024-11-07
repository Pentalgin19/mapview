package com.example.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class IndoorNavigation(
    val map: MapView,
    val mapkit: Mapkit,
    val context: Context
) {

    companion object {
        @JvmStatic
        var redactPositionState = false
    }

    var indoorRoute: MutableList<Point> = mutableListOf(
        Point(51.765273, 55.124219),
        Point(51.765294, 55.124176),
        Point(51.765323, 55.124140),
        Point(51.765242, 55.123898),
        Point(51.765230, 55.123901)
    )
    var indoorRoutePolylineObject: PolylineMapObject? = null

    val newPoints = mutableListOf(
        Point(51.765268, 55.124222),
        Point(51.765271, 55.124206),
        Point(51.765278, 55.124195),
        Point(51.765289, 55.124185),
        Point(51.765298, 55.124176),
        Point(51.765305, 55.124169),
        Point(51.765311, 55.124162),
        Point(51.765318, 55.124156),
        Point(51.765325, 55.124150),
        Point(51.765321, 55.124137),
        Point(51.765316, 55.124120),
        Point(51.765310, 55.124101),
        Point(51.765304, 55.124084),
        Point(51.765298, 55.124065),
        Point(51.765290, 55.124048),
        Point(51.765284, 55.124031),
        Point(51.765278, 55.124013),
        Point(51.765271, 55.123996),
        Point(51.765266, 55.123979),
        Point(51.765259, 55.123960),
        Point(51.765253, 55.123945),
        Point(51.765246, 55.123927),
        Point(51.765239, 55.123910),
        Point(51.765229, 55.123904),
    )

    val carcas = listOf(
        Point(51.765171, 55.123939),
        Point(51.765119, 55.123990),
        Point(51.765073, 55.123866),
        Point(51.765249, 55.123701),
        Point(51.765540, 55.124500),
        Point(51.765369, 55.124662),
        Point(51.765326, 55.124545),
        Point(51.765499, 55.124378),
        Point(51.765372, 55.124497),
        Point(51.765373, 55.124497),
        Point(51.765171, 55.123939),

        Point(51.765294, 55.123821),
    )
    val ladder1 = listOf(
        Point(51.765347, 55.123966),
        Point(51.765362, 55.124006),
        Point(51.765315, 55.124050),
        Point(51.765298, 55.124010),
        Point(51.765347, 55.123966),
    )
    val ladder2 = listOf(
        Point(51.765398, 55.124282),
        Point(51.765384, 55.124239),
        Point(51.765430, 55.124195),
        Point(51.765445, 55.124235),
        Point(51.765398, 55.124282),
    )
    val zalDoor = listOf(
        Point(51.765240, 55.123872),
        Point(51.765233, 55.123878)
    )

    var t = true

    fun collegeAuditoriums() {
        val k104 = OfficeMap(
            roomCoords = listOf(
                Point(51.765248, 55.123869),
                Point(51.765294, 55.123821),
                Point(51.765347, 55.123966),
                Point(51.765298, 55.124010),
                Point(51.765248, 55.123869)
            ),
            doorCoords = listOf(
                Point(51.765284, 55.123974),
                Point(51.765289, 55.123987),
            )
        )
        val k105 = OfficeMap(
            roomCoords = listOf(
                Point(51.765271, 55.124034),
                Point(51.765222, 55.124079),
                Point(51.765256, 55.124172),
                Point(51.765304, 55.124122),
                Point(51.765271, 55.124034)
            ),
            doorCoords = listOf(
                Point(51.765225, 55.123899),
                Point(51.765230, 55.123912),
            )
        )
        val k106 = OfficeMap(
            roomCoords = listOf(
                Point(51.765172, 55.123940),
                Point(51.765223, 55.123895),
                Point(51.765271, 55.124034),
                Point(51.765222, 55.124079),
                Point(51.765172, 55.123940),
            ),
            doorCoords = listOf(
                Point(51.765279, 55.124054),
                Point(51.765283, 55.124064),
            )
        )
        val k107 = OfficeMap(
            roomCoords = listOf(
                Point(51.765289, 55.124267),
                Point(51.765341, 55.124215),
                Point(51.765372, 55.124307),
                Point(51.765321, 55.124354),
                Point(51.765289, 55.124267),
            ),
            doorCoords = listOf(
                Point(51.765354, 55.124256),
                Point(51.765359, 55.124268),
            )
        )
        val k108 = OfficeMap(
            roomCoords = listOf(
                Point(51.765372, 55.124307),
                Point(51.765321, 55.124354),
                Point(51.765373, 55.124497),
                Point(51.765422, 55.124448),
                Point(51.765372, 55.124307),
            ),
            doorCoords = listOf(
                Point(51.765377, 55.124320),
                Point(51.765380, 55.124330),
            )
        )

        val cloakroom = OfficeMap(
            roomCoords = listOf(
                Point(51.765497, 55.124379),
                Point(51.765454, 55.124420),
                Point(51.765437, 55.124366),
                Point(51.765478, 55.124326),
                Point(51.765497, 55.124378),
            ),
            doorCoords = listOf(
                Point(51.765440, 55.124379),
                Point(51.765445, 55.124392),
            )
        )
        val k111 = OfficeMap(
            roomCoords = listOf(
                Point(51.765432, 55.124371),
                Point(51.765478, 55.124326),
                Point(51.765445, 55.124236),
                Point(51.765399, 55.124281),
                Point(51.765432, 55.124371),
            ),
            doorCoords = listOf(
                Point(51.765404, 55.124295),
                Point(51.765408, 55.124306),
            )
        )
        val k112 = OfficeMap(
            roomCoords = listOf(
                Point(51.765315, 55.124050),
                Point(51.765398, 55.124282),
                Point(51.765445, 55.124235),
                Point(51.765362, 55.124006),
                Point(51.765315, 55.124050),
            ),
            doorCoords = listOf(
                Point(51.765355, 55.124161),
                Point(51.765359, 55.124175),
            )
        )

        val carcasPolyline = Polyline(carcas)

        val zal = zalDoor


        val ladder1 = Polyline(ladder1)
        val ladder2 = Polyline(ladder2)

        val carcas = map.map.mapObjects.addPolyline(carcasPolyline)
//        carcas.apply {
//            setStrokeColor(ContextCompat.getColor(context, R.color.red))
//        }
        val k104Room = map.map.mapObjects.addPolyline(Polyline(k104.roomCoords))
        val k105Room = map.map.mapObjects.addPolyline(Polyline(k105.roomCoords))
        val k106Room = map.map.mapObjects.addPolyline(Polyline(k106.roomCoords))
        val k107Room = map.map.mapObjects.addPolyline(Polyline(k107.roomCoords))
        val k108Room = map.map.mapObjects.addPolyline(Polyline(k108.roomCoords))
        val cloakRoom = map.map.mapObjects.addPolyline(Polyline(cloakroom.roomCoords))
        val k111Room = map.map.mapObjects.addPolyline(Polyline(k111.roomCoords))
        val k112Room = map.map.mapObjects.addPolyline(Polyline(k112.roomCoords))

        listOfPoints(carcas)
        listOfPoints(k104Room)
        listOfPoints(k105Room)
        listOfPoints(k106Room)
        listOfPoints(k107Room)
        listOfPoints(k108Room)
        listOfPoints(cloakRoom)
        listOfPoints(k111Room)
        listOfPoints(k112Room)

        if (t) {
            indoorRoutePolylineObject = map.map.mapObjects.addPolyline(Polyline(indoorRoute))
                .apply {
                    dashLength = 6f
                    gapLength = 6f
                    setStrokeColor(ContextCompat.getColor(context, R.color.customBlue))
                    strokeWidth = 3f
                }
            t = false
        }

        val lader11 = map.map.mapObjects.addPolyline(ladder1)
        val lader22 = map.map.mapObjects.addPolyline(ladder2)

        listOfPoints(lader11)
        listOfPoints(lader22)

        val zal2Door = map.map.mapObjects.addPolyline(
            Polyline(
                listOf(
                    Point(51.765434, 55.124438),
                    Point(51.765444, 55.124428),
                )
            )
        )
        val zalDoor = map.map.mapObjects.addPolyline(Polyline(zal))

        listOfPoints(zal2Door)
        listOfPoints(zalDoor)

        val k104Door = map.map.mapObjects.addPolyline(Polyline(k104.doorCoords))
        val k105Door = map.map.mapObjects.addPolyline(Polyline(k105.doorCoords))
        val k106Door = map.map.mapObjects.addPolyline(Polyline(k106.doorCoords))
        val k107Door = map.map.mapObjects.addPolyline(Polyline(k107.doorCoords))
        val k108Door = map.map.mapObjects.addPolyline(Polyline(k108.doorCoords))
        val cloakroomDoor = map.map.mapObjects.addPolyline(Polyline(cloakroom.doorCoords))
        val k111Door = map.map.mapObjects.addPolyline(Polyline(k111.doorCoords))
        val k112Door = map.map.mapObjects.addPolyline(Polyline(k112.doorCoords))

        listOfPoints(k104Door)
        listOfPoints(k105Door)
        listOfPoints(k106Door)
        listOfPoints(k107Door)
        listOfPoints(k108Door)
        listOfPoints(cloakroomDoor)
        listOfPoints(k111Door)
        listOfPoints(k112Door)

        k104Door.apply {
            setStrokeColor(ContextCompat.getColor(context, R.color.black))
        }
        k105Door.apply {
            setStrokeColor(ContextCompat.getColor(context, R.color.black))
        }
        k106Door.apply {
            setStrokeColor(ContextCompat.getColor(context, R.color.black))
        }
        k107Door.apply {
            setStrokeColor(ContextCompat.getColor(context, R.color.black))
        }
        k108Door.apply {
            setStrokeColor(ContextCompat.getColor(context, R.color.black))
        }
        cloakroomDoor.apply {
            setStrokeColor(ContextCompat.getColor(context, R.color.black))
        }
        k111Door.apply {
            setStrokeColor(ContextCompat.getColor(context, R.color.black))
        }
        k112Door.apply {
            setStrokeColor(ContextCompat.getColor(context, R.color.black))
        }

    }

    var listRoomCords: MutableList<PolylineMapObject> = mutableListOf()

    private fun listOfPoints(polylineMapObject: PolylineMapObject) {
        listRoomCords.add(polylineMapObject)
    }

    val okeiBorders = mapOf(
        "bl" to Point(51.765072, 55.123865),
        "br" to Point(51.765370, 55.124666),
        "tl" to Point(51.765252, 55.123696),
        "tr" to Point(51.765543, 55.124501),
    )

    @SuppressLint("MissingPermission")
    fun checkIfUserInBuilding(position: Point) {
        if (position.longitude >= okeiBorders["bl"]?.longitude ?: 0.0 &&
            position.longitude <= okeiBorders["tr"]?.longitude ?: 0.0
        ) {
            if (position.latitude >= okeiBorders["bl"]?.latitude ?: 0.0 &&
                position.latitude <= okeiBorders["tr"]?.latitude ?: 0.0
            ) {
                redactPositionState = true
                Log.i("oioioi", "Пользователь в здании")
                return
            }
        }

        redactPositionState = false
        Log.i("oioioi", "Пользователь не в здании")
    }

    val pointObj = PointObj(context, map)
    var userIcon: PlacemarkMapObject? = null

    var isIndoor: Boolean = false

    fun checkIndoorPoints(position: Point) {
        pointObj.deleteAllRoute()

        if (
            abs(position.latitude - 51.765268) <= 0.000010
            && abs(position.longitude - 55.124222) <= 0.000010
        ) {
            // удалить
            CoroutineScope(Dispatchers.Main).launch {
                for (i in 0..<newPoints.size) {
                    mapkit.closeUserPin()
                    newPoints.removeFirstOrNull()
                    delay(1000)

                    if (indoorRoutePolylineObject?.isValid == true) {
                        map.mapWindow.map.mapObjects.remove(indoorRoutePolylineObject as MapObject)
                    }

                    indoorRoutePolylineObject =
                        map.mapWindow.map.mapObjects.addPolyline(Polyline(newPoints))
                            .apply {
                                dashLength = 6f
                                gapLength = 6f
                                setStrokeColor(ContextCompat.getColor(context, R.color.customBlue))
                                strokeWidth = 3f
                            }
                    userIcon = newPoints.firstOrNull()
                        ?.let {
                            userIcon?.let { userIcon ->
                                map.mapWindow.map.mapObjects.remove(userIcon as MapObject)
                            }

                            map.mapWindow.map.mapObjects.addPlacemark(
                                it,
                                ImageProvider.fromResource(context, R.drawable.ic_me)
                            )
                        }
                }
            }
            isIndoor = true
            // удалить
        }

        if (!isIndoor) {
            val firstPoint = indoorRoute.getOrNull(1) ?: return

            if (abs(position.latitude - firstPoint.latitude) <= 0.000010
                && abs(position.longitude - firstPoint.longitude) <= 0.000010
            ) {
                indoorRoute.remove(firstPoint)
            }

            indoorRoute[0] = position
            if (indoorRoutePolylineObject?.isValid == true) {
                map.mapWindow.map.mapObjects.remove(indoorRoutePolylineObject as MapObject)
            }

            indoorRoutePolylineObject =
                map.mapWindow.map.mapObjects.addPolyline(Polyline(indoorRoute))
                    .apply {
                        dashLength = 6f
                        gapLength = 6f
                        setStrokeColor(ContextCompat.getColor(context, R.color.customBlue))
                        strokeWidth = 3f
                    }
        }

    }
}

data class OfficeMap(
    val roomCoords: List<Point>,
    val doorCoords: List<Point>,
)