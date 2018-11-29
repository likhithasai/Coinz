package com.apps.likhithasai.coinz

import android.content.SharedPreferences
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.mapbox.mapboxsdk.style.light.Position
import java.io.IOException
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.mapbox.mapboxsdk.annotations.Marker
import java.math.BigDecimal
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class MainActivity() : AppCompatActivity(), PermissionsListener, LocationEngineListener, OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionManager: PermissionsManager
    private lateinit var originLocation: Location
    private val tag = "MainActivity"
    //Probably should be a hashmap with coin id ? but shall see
    //private val markers: kotlin.collections.MutableList<Marker> = java.util.ArrayList()

    private var markers = HashMap<Marker, Coin>()

    private var walletdb = HashMap<String, Any>()

    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null

    private var currentUser = ""

    private var coinsCollected:MutableSet<String> ?= null
    private var wallet:MutableSet<String> ?= null

    private val CONNECTION_TIMEOUT_MILLISECONDS = 15000
    private val CONNECTION_READTIMEOUT_MILLISECONDS = 10000

    private val PURPLE = "#0000ff"
    private val RED = "#ff0000"
    private val YELLOW = "#ffdf00"
    private val GREEN = "#008000"

    var prefs: SharedPrefs? = null

    private var downloadDate = "2018/10/03"


    fun pickColorString(hex: String):String {
        when (hex) {
            PURPLE -> return "p"
            RED -> return "r"
            GREEN -> return "g"
            YELLOW -> return "y"
        }
        return ""
    }

    object DownloadCompleteRunner: DownloadCompleteListener{
        var result : String?= null
        override fun downloadComplete(result: String) {
            this.result = result;
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = SharedPrefs(applicationContext);
        currentUser = prefs!!.currentUser;
        Log.d(tag, "Current user: $currentUser")


        Log.d(tag, "On start, peny: "+ prefs!!.peny)
        Log.d(tag, "On start, dolr: "+ prefs!!.dolr)
        Log.d(tag, "On start, shil: "+ prefs!!.shil)
        Log.d(tag, "On start, quid: "+ prefs!!.quid)

        coinsCollected = prefs!!.coinsCollected?.toMutableSet()
        wallet = prefs!!.wallet?.toMutableSet()

        Mapbox.getInstance(applicationContext, getString(R.string.access_token))

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        Log.d(tag, "[onMapReady] DOES LOGGING WORK")
        mapView?.getMapAsync (this)
    }

    override fun onMapReady(mapboxMap: MapboxMap?) {
        if (mapboxMap == null) {
            Log.d(tag, "[onMapReady] mapboxMap is null")
        } else {
            map = mapboxMap
            Log.d(tag, "[onMapReady] inside onMapReady")
            map?.uiSettings?.isCompassEnabled = true
            map?.uiSettings?.isZoomControlsEnabled = true

            var date: Date = Calendar.getInstance().getTime();

            // Display a date in day, month, year format
            val formatter:DateFormat  = SimpleDateFormat("yyyy/MM/dd");
            downloadDate = formatter.format(date);
            Log.d(tag,"Today : " + downloadDate);

            var mapfeat:String = ""

            if (!downloadDate.equals(prefs!!.lastDownloadDate)){
                val url = "http://homepages.inf.ed.ac.uk/stg/coinz/${downloadDate}/coinzmap.geojson"
                mapfeat = DownloadFeaturesTask(DownloadCompleteRunner).execute(url).get()
                prefs!!.mapfeat = mapfeat
                prefs!!.lastDownloadDate = downloadDate
            }
            else {
//                val url = "http://homepages.inf.ed.ac.uk/stg/coinz/${downloadDate}/coinzmap.geojson"
//                mapfeat = DownloadFeaturesTask(DownloadCompleteRunner).execute(url).get()
//                prefs!!.mapfeat = mapfeat
                mapfeat = prefs!!.mapfeat
            }
            //val url = "http://homepages.inf.ed.ac.uk/stg/coinz/2018/10/03/coinzmap.geojson"

            Log.d(tag,"JSON FILE:" + mapfeat)
           // To get the rates
            val jsonObj = JSONObject(mapfeat)
            val rates = jsonObj.getJSONObject("rates")

            prefs!!.shil_rate = rates.getString("SHIL")
            prefs!!.dolr_rate = rates.getString("DOLR")
            prefs!!.quid_rate = rates.getString("QUID")
            prefs!!.peny_rate = rates.getString("PENY")


            var featureCollection: FeatureCollection = FeatureCollection.fromJson(mapfeat)
            var features: List<Feature>? = featureCollection.features()

            for (f in features!!.iterator()) {
                if (f.geometry() is Point) {
                    val id = f.getStringProperty("id")

                    if(!coinsCollected?.contains(id)!!){

                        Log.d(tag, "Inside the for loop for adding markers")
                        val point = f.geometry() as Point

                        var colorStr: String = ""
                        val markerColour = f.getStringProperty("marker-color")
                        colorStr = pickColorString(markerColour)

                        val no = f.getStringProperty("marker-symbol")
                        val currency = f.getStringProperty("currency")
                        val value = f.getStringProperty("value")

                        //Getting the drawable resource according color and maker symbol
                        val resId = resources.getIdentifier(colorStr + no, "drawable", packageName)

                        //Creating the icon from the drawable resource obtained
                        val iconFactory = IconFactory.getInstance(this@MainActivity)
                        val icon = iconFactory.fromResource(resId)

                        //Adding the markers to the map
                        val m = map.addMarker(MarkerOptions().position(LatLng(point.latitude(), point.longitude())).setIcon(icon))

                        //Adding marker to the list
                        val coin = Coin(id = id, currency = currency, value = value)
                        markers[m] = coin
                    }
                    else{

                        Log.d(tag, "Coin collected ${id} or something wrong lol")
                    }
                }
            }
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
//       location?.let {
//            originLocation = location
//            setCameraPosition(location)
//        }
//         Don't really need this...Do I
        val distances = FloatArray(10)
        var loccoinCollected: Marker? = null
        //var coinCollected:Coin ?= null

        for ((marker,coin) in markers!!.iterator()) {
            Location.distanceBetween(location!!.latitude,location.longitude,
                    marker.position.latitude,marker.position.longitude,distances)
            if (distances[0] <= location!!.accuracy && distances[0] <= 25) {
                Log.d(tag, "Coin collected!")
                marker.remove()
                loccoinCollected = marker
                //coinCollected = coin
                val coinCollected = downloadDate + coin.currency + coin.value
                Log.d(tag, "Coin collected: $coinCollected")
                //Log.d(tag, "Coin ID: ${coinCollected.id}")
                coinsCollected!!.add(coin.id)
                wallet!!.add(coinCollected)
                walletdb!!.put(coin.id, coinCollected)

            }
        }
        markers.remove(loccoinCollected)
        val size = markers.size
        Log.d(tag, "Coins now: ${size}")
        Toast.makeText(this, "There are now ${size} markers", Toast.LENGTH_SHORT).show()
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

        // use ”” as the default value (this might be the first time the app is run)
        downloadDate = prefs!!.lastDownloadDate
        // Write a message to ”logcat” (for debugging purposes)
        Log.d(tag, "[onStart] Recalled lastDownloadDate is ’$downloadDate’")

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

        prefs!!.lastDownloadDate = downloadDate
        prefs?.coinsCollected = coinsCollected
        prefs?.wallet = wallet

        val ref = FirebaseFirestore.getInstance().collection("users")
        val documentId = prefs!!.currentUser

        ref.document(documentId).set(walletdb).addOnCompleteListener {
            Log.d(tag, "Things added")
        }
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
