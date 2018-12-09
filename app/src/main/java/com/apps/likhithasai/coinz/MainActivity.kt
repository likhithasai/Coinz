package com.apps.likhithasai.coinz

import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.*
import android.support.design.widget.Snackbar
import android.util.Log
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
import com.mapbox.geojson.*
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.annotations.IconFactory
import org.json.JSONObject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mapbox.mapboxsdk.annotations.Marker
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), PermissionsListener, LocationEngineListener, OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionManager: PermissionsManager
    private var originLocation: Location ?= null //Chnage this if it crashes
    private val tag = "MainActivity"
    //private val markers: kotlin.collections.MutableList<Marker> = java.util.ArrayList()

    private var markers = HashMap<Marker, Coin>()

    private var walletdb = HashMap<String, Any>()

    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null

    private var currentUser = ""

    private var coinsCollected: MutableSet<String>? = null
    private var wallet: MutableSet<String>? = null

    private val purple = "#0000ff"
    private val red = "#ff0000"
    private val yellow = "#ffdf00"
    private val green = "#008000"

    private var prefs: SharedPrefs? = null

    private var downloadDate = "2018/10/03"

    private fun pickColorString(hex: String): String {
        when (hex) {
            purple -> return "p"
            red -> return "r"
            green -> return "g"
            yellow -> return "y"
        }
        return ""
    }

    object DownloadCompleteRunner : DownloadCompleteListener {
        private var result: String? = null
        override fun downloadComplete(result: String) {
            this.result = result
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = SharedPrefs(applicationContext)
        currentUser = prefs!!.currentUser
        Log.d(tag, "Current user: $currentUser")

        coinsDisp.text = "You have collected ${prefs!!.coinConstraint} out of 50 coins today"

        coinsCollected = prefs!!.coinsCollected?.toMutableSet()
        wallet = prefs!!.wallet?.toMutableSet()

        Mapbox.getInstance(applicationContext, getString(R.string.access_token))

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        Log.d(tag, "[onMapReady] DOES LOGGING WORK")
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap?) {
        if (mapboxMap == null) {
            Log.d(tag, "[onMapReady] mapboxMap is null")
        } else {
            map = mapboxMap
            Log.d(tag, "[onMapReady] inside onMapReady")
            map.uiSettings.isCompassEnabled = true
            map.uiSettings.isZoomControlsEnabled = true

            val date: Date = Calendar.getInstance().time

            // Display a date in day, month, year format
            val formatter: DateFormat = SimpleDateFormat("yyyy/MM/dd")
            downloadDate = formatter.format(date)
            Log.d(tag, "Today : $downloadDate")

            val mapfeat: String

            if (downloadDate != prefs!!.lastDownloadDate) {
                val url = "http://homepages.inf.ed.ac.uk/stg/coinz/$downloadDate/coinzmap.geojson"
                mapfeat = DownloadFeaturesTask(DownloadCompleteRunner).execute(url).get()
                prefs!!.mapfeat = mapfeat
                prefs!!.lastDownloadDate = downloadDate
                prefs!!.coinConstraint = 0
                prefs!!.depositLimit = 0
            } else {
                mapfeat = prefs!!.mapfeat
            }
            //val url = "http://homepages.inf.ed.ac.uk/stg/coinz/2018/10/03/coinzmap.geojson"

            Log.d(tag, "JSON FILE: $mapfeat")
            // To get the rates
            val jsonObj = JSONObject(mapfeat)
            val rates = jsonObj.getJSONObject("rates")

            prefs!!.shil_rate = rates.getString("SHIL")
            prefs!!.dolr_rate = rates.getString("DOLR")
            prefs!!.quid_rate = rates.getString("QUID")
            prefs!!.peny_rate = rates.getString("PENY")


            val featureCollection: FeatureCollection = FeatureCollection.fromJson(mapfeat)
            val features: List<Feature>? = featureCollection.features()

            for (f in features!!.iterator()) {
                if (f.geometry() is Point) {
                    val id = f.getStringProperty("id")

                    if (!coinsCollected?.contains(id)!!) {

                        Log.d(tag, "Inside the for loop for adding markers")
                        val point = f.geometry() as Point

                        var colorStr: String
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
                    } else {

                        Log.d(tag, "Coin collected $id")
                    }
                }
            }
            enableLocation()
        }
    }


    private fun enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine()
            initializeLocationLayer()
        } else {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine() {
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

    private fun setCameraPosition(location: Location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.latitude), 15.0))
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        //Present a toast explaining why they need to grant access.
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLocationChanged(location: Location?) {
//       location?.let {
//            //originLocation = location
//            setCameraPosition(location)
//        }
        if (originLocation != null){
            prefs!!.distanceWalked = prefs!!.distanceWalked + originLocation!!.distanceTo(location)
        } else {
            originLocation = location!!
        }

//
        var loccoinCollected: Marker? = null



        for ((marker, coin) in markers.iterator()) {
            val loc1 = Location("")
            loc1.latitude = location!!.latitude
            loc1.longitude = location.longitude

            val loc2 = Location("")
            loc2.latitude = marker.position.latitude
            loc2.longitude = marker.position.longitude

            val distanceInMeters = loc1.distanceTo(loc2)

            if (distanceInMeters <= 25) {
                Log.d(tag, "Coin collected!")
                marker.remove()
                loccoinCollected = marker
                //coinCollected = coin
                val coinCollected = coin.currency + coin.value
                Log.d(tag, "Coin collected: $coinCollected")
                //Log.d(tag, "Coin ID: ${coinCollected.id}")
                coinsCollected!!.add(coin.id)
                wallet!!.add(coinCollected)

                walletdb[coin.id] = coinCollected

                prefs!!.coinConstraint = prefs!!.coinConstraint + 1
                Log.d(tag, "The coin constraint is ${prefs!!.coinConstraint}")

                coinsDisp.text = "You have collected ${prefs!!.coinConstraint} out of 50 coins today"

                val sb = Snackbar.make(findViewById(R.id.layout),"You have collected a coin!", Snackbar.LENGTH_LONG)
                        .setAction("Dismiss"){}
                sb.show()

            }
        }
        markers.remove(loccoinCollected)
        val size = markers.size
        Log.d(tag, "Coins now: $size")
        //Toast.makeText(this, "There are now $size markers", Toast.LENGTH_SHORT).show()
    }


    @SuppressWarnings("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
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

        Log.d(tag, "Distance walked: ${prefs!!.distanceWalked}")

        //Stores data in shared prefs when app is exited
        prefs!!.lastDownloadDate = downloadDate
        prefs?.coinsCollected = coinsCollected
        prefs?.wallet = wallet

        val ref = FirebaseFirestore.getInstance().collection("UsersWallet")
        val documentId = prefs!!.currentUser

        ref.document(documentId).set(walletdb, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(tag, "Things added")
                }
                .addOnFailureListener {
                    Log.d(tag, "Failed")
                    Log.d(tag, it.message)
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