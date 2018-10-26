package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth



class StatsActivity : AppCompatActivity() {
    private val tag = "StatsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val playButton = findViewById<Button>(R.id.button)
        val user = FirebaseAuth.getInstance().currentUser

        Log.d(tag, "DOES THE LOGGING WORK")
        Log.d(tag, "${user?.displayName}")

        var user_view: TextView = findViewById(R.id.currentUser) as TextView
        user_view?.text = user?.displayName

        playButton.setOnClickListener {
            // Handler code here.
            //Toast.makeText(this, "Button is clicked", Toast.LENGTH_LONG).show();
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
