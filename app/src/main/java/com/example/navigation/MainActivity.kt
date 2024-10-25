package com.example.navigation

import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.navigation.databinding.ActivityMainBinding
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.mapview.MapView
import kotlin.properties.Delegates


private var latitude by Delegates.notNull<Double>()
private var lotitude by Delegates.notNull<Double>()
private lateinit var binding: ActivityMainBinding
public lateinit var mapView: MapView
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
//        MapKitFactory.setApiKey("eed2a724-fc95-4e5d-935a-8e0c346df956")
//        MapKitFactory.initialize(this)
        setContentView(root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    val locationListener = object : LocationListener{
        override fun onLocationUpdated(p0: Location) {
            val main = MainActivity2()
            val location = main.location()
            val s = intent.extras
            val a = location.indexOf("lat =") + 5
            val b = location.indexOf(",")
            var c = "" // широта
            for (i in a..b) {
                c += location[i]
            }
            val a1 = location.indexOf("lon =")
            val b1 = location.length - 1
            var c1 = ""
            for (i in a1..b1) {
                c1 += location[i]
            }
            binding.text.text = location + "jgjkhjgjkklh"
        }

        override fun onLocationStatusUpdated(p0: LocationStatus) {

        }
    }
    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            false
        } else {
            true
        }
    }
}
