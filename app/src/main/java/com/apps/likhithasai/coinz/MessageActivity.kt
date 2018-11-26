package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_message.*
import java.math.RoundingMode
import kotlin.math.roundToInt

class MessageActivity : AppCompatActivity() {

    private val tag = "MessageActivity"

    var prefs:SharedPrefs ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        prefs = SharedPrefs(applicationContext)

        val shil_rate = prefs!!.shil_rate
        val dolr_rate = prefs!!.dolr_rate
        val quid_rate = prefs!!.quid_rate
        val peny_rate = prefs!!.peny_rate

//        //The marquee for showing rates
//        val tv = findViewById<TextView>(R.id.mywidget)
//        tv.setText("Rates for today: SHIL to GOLD: ${shil_rate} DOLR to GOLD: ${dolr_rate} QUID to GOLD: ${quid_rate } PENY to GOLD: ${peny_rate}")
//        tv!!.setSelected(true)  // Set focus to the textview
//
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("None", "Top", "Bottom"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        positionSpinner.adapter = adapter

        positionSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // either one will work as well
                // val item = parent.getItemAtPosition(position) as String
                val item = adapter.getItem(position)
            }
        }


    }


}
