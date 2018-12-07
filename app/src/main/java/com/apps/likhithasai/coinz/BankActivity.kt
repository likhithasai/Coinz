package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_bank.*
import android.content.Intent

class BankActivity : AppCompatActivity() {

    private var prefs:SharedPrefs ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)

        prefs = SharedPrefs(applicationContext)

        val shilRate = prefs!!.shil_rate
        val dolrRate = prefs!!.dolr_rate
        val quidRate = prefs!!.quid_rate
        val penyRate = prefs!!.peny_rate

        val tv = findViewById<TextView>(R.id.mywidget)
        tv.text = "Rates for today: SHIL to GOLD: $shilRate DOLR to GOLD: $dolrRate QUID to GOLD: $quidRate PENY to GOLD: $penyRate"
        tv!!.isSelected = true  // Set focus to the textview

        goldDisp.text = prefs!!.goldcoins
        spareChangeDisp.text = prefs!!.spareChange

        shareBtn.setOnClickListener {
            val s = "I possess ${prefs!!.goldcoins} gold coins. What's your net value on Coinz? ;)"
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, s)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Download the best game in town, Coinz!")
            startActivity(Intent.createChooser(shareIntent, "Share text via"))
        }





    }



}
