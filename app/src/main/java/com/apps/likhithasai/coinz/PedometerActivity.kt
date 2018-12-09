package com.apps.likhithasai.coinz

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_pedometer.*

class PedometerActivity : AppCompatActivity(), SensorEventListener {

    private var running = false
    private var sensorManager:SensorManager? = null
    private var prefs:SharedPrefs ?= null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedometer)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //Declaring the shared prefs
        prefs = SharedPrefs(applicationContext)
        //Distance shown in km
        val distKm:Float = prefs!!.distanceWalked/1000F
        //Approx 60 cal burnt for 3km
        val caloriesBurnt = ((distKm/3F) * 60F)

        //Displaying the distance and the calories
        distVal.text = "$distKm km walked"
        calVal.text = "$caloriesBurnt kcal"
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepsSensor == null) {
            Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            //Displaying the sensor event values
            stepsVal.text = " ${event?.values!![0]}"
        }
    }

}