package com.apps.likhithasai.coinz

import android.graphics.Bitmap
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.*
import android.util.Log
import android.widget.Toast
import java.net.HttpURLConnection
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.geojson.*;
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.annotations.IconFactory
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.mapbox.mapboxsdk.style.light.Position
import java.io.IOException
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.mapbox.mapboxsdk.annotations.Marker

class MainActivity() : AppCompatActivity(), PermissionsListener, LocationEngineListener, OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionManager: PermissionsManager
    private lateinit var originLocation: Location
    private val tag = "MainActivity"

    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null

    val CONNECTION_TIMEOUT_MILLISECONDS = 15000
    val CONNECTION_READTIMEOUT_MILLISECONDS = 10000

    val PURPLE = "#0000ff"
    val RED = "#ff0000"
    val YELLOW = "#ffdf00"
    val GREEN = "#008000"

    object DownloadCompleteRunner: DownloadCompleteListener{
        var result : String?= null
        override fun downloadComplete(result: String) {
            this.result = result;
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Mapbox.getInstance(applicationContext, getString(R.string.access_token))
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        Log.d(tag, "[onMapReady] DOES LOGGING WORK")
        mapView.getMapAsync { mapboxMap ->
            map = mapboxMap
            enableLocation()
            val url = "http://homepages.inf.ed.ac.uk/stg/coinz/2018/10/03/coinzmap.geojson"
            val mapfeat = DownloadFeaturesTask(DownloadCompleteRunner).execute(url).get()

            var featureCollection:FeatureCollection = FeatureCollection.fromJson(mapfeat)
            var features:List<Feature> ?= featureCollection.features()

            for (f in features!!.iterator()) {
                if (f.geometry() is Point) {
                    Log.d(tag,"Inside the for loop for adding markers")
                    val point = f.geometry() as Point
                    var str:String = ""
                    val markerColour = f.getStringProperty("marker-color")
                    if (markerColour.equals(RED)){
                        str += "r"
                    } else if (markerColour.equals(GREEN)){
                        str += "g"
                    } else if (markerColour.equals(YELLOW)){
                        str += "y"
                    } else {
                        str += "p"
                    }

                    val no = f.getStringProperty("marker-symbol")
                    val resId = resources.getIdentifier(str+no, "drawable", packageName)
                    val iconFactory = IconFactory.getInstance(this@MainActivity)
                    val icon = iconFactory.fromResource(resId)
                    map.addMarker(MarkerOptions().position(LatLng(point.latitude(), point.longitude())).setIcon(icon))

                }
            }
        }

}

   override fun onMapReady(mapboxMap: MapboxMap?) {
        Log.d(tag,"Inside the onMapReadyCallback")
        if (mapboxMap == null) {
            Log.d(tag, "[onMapReady] mapboxMap is null")
        } else {
            Log.d(tag,"Inside the onMapReadyCallback")
            map = mapboxMap
            enableLocation()
        }
    }

    fun enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine()
            initializeLocationLayer()
        } else {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine(){
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.addLocationEngineListener(this)
        locationEngine?.activate()

        val lastLocation = locationEngine?.lastLocation
        if (lastLocation != null) {
            originLocation = lastLocation
            setCameraPosition(lastLocation)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationLayer() {
        locationLayerPlugin = LocationLayerPlugin(mapView, map, locationEngine)
        locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode = CameraMode.TRACKING
        locationLayerPlugin?.renderMode = RenderMode.NORMAL
    }

    private fun setCameraPosition(location: Location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.latitude),15.0))
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        //Present a toast explaining why they need to grant access.
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted){
            enableLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            originLocation = location
            setCameraPosition(location)
        }
    }

    @SuppressWarnings("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            locationEngine?.requestLocationUpdates()
            locationLayerPlugin?.onStart()
        }
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationEngine?.deactivate()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


}
