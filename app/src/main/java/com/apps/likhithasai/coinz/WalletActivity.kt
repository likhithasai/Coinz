package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import java.math.RoundingMode
import kotlin.math.roundToInt

class WalletActivity : AppCompatActivity() {

    private val tag = "WalletActivity"

    var prefs:SharedPrefs ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        prefs = SharedPrefs(applicationContext)

        val shil_rate = prefs!!.shil_rate
        val dolr_rate = prefs!!.dolr_rate
        val quid_rate = prefs!!.quid_rate
        val peny_rate = prefs!!.peny_rate

        val tv = findViewById<TextView>(R.id.mywidget)
        tv.setText("Rates for today: SHIL to GOLD: ${shil_rate} DOLR to GOLD: ${dolr_rate} QUID to GOLD: ${quid_rate } PENY to GOLD: ${peny_rate}")
        tv!!.setSelected(true)  // Set focus to the textview

        Log.d(tag, "Peny" + prefs!!.peny)
        Log.d(tag, "Quid" + prefs!!.quid)
        Log.d(tag, "Dolr" + prefs!!.dolr)
        Log.d(tag, "Shil" + prefs!!.shil)

        val peny = prefs!!.peny.toBigDecimal().setScale(2, RoundingMode.CEILING).toString()
        val dolr = prefs!!.dolr.toBigDecimal().setScale(2, RoundingMode.CEILING).toString()
        val shil = prefs!!.shil.toBigDecimal().setScale(2, RoundingMode.CEILING).toString()
        val quid = prefs!!.quid.toBigDecimal().setScale(2, RoundingMode.CEILING).toString()


        val quidView = findViewById<TextView>(R.id.quid)
        val shilView = findViewById<TextView>(R.id.shil)
        val dolrView = findViewById<TextView>(R.id.dolr)
        val penyView = findViewById<TextView>(R.id.peny)

        quidView.setText(quid)
        shilView.setText(shil)
        dolrView.setText(dolr)
        penyView.setText(peny)

    }


}
