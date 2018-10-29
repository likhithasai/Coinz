package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.graphics.Color
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_stats.*
import android.support.v4.content.ContextCompat.startActivity




class StatsActivity : AppCompatActivity() {
//    private val tag = "StatsActivity"
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_stats)
//
//        val playButton = findViewById<Button>(R.id.button)
//        val user = FirebaseAuth.getInstance().currentUser
//
//        Log.d(tag, "DOES THE LOGGING WORK")
//        Log.d(tag, "${user?.displayName}")
//
//        var user_view: TextView = findViewById(R.id.currentUser) as TextView
//        user_view?.text = user?.displayName
//
//        // Configure action bar
//        setSupportActionBar(toolbar)
//        val actionBar = supportActionBar
//        actionBar?.title = "Hello Toolbar"
//
//
//        // Initialize the action bar drawer toggle instance
//        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
//                this,
//                drawer_layout,
//                toolbar,
//                R.string.drawer_open,
//                R.string.drawer_close
//        ){
//            override fun onDrawerClosed(view: View){
//                super.onDrawerClosed(view)
//                //toast("Drawer closed")
//            }
//
//            override fun onDrawerOpened(drawerView: View){
//                super.onDrawerOpened(drawerView)
//                //toast("Drawer opened")
//            }
//        }
//
//
//        // Configure the drawer layout to add listener and show icon on toolbar
//        drawerToggle.isDrawerIndicatorEnabled = true
//        drawer_layout.addDrawerListener(drawerToggle)
//        drawerToggle.syncState()
//
//
//        // Set navigation view navigation item selected listener
//        navigation_view.setNavigationItemSelectedListener{
//            when (it.itemId){
//                R.id.action_health -> {
//                    intent = Intent(this, PedometerActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    this.startActivity(intent)
//                    Log.d(tag, "YOU CLICKED health")
//
//                }
//                R.id.action_leaderboard -> {
//                    Toast.makeText(this, "This is a leaderboard", Toast.LENGTH_LONG)
//                    Log.d(tag, "YOU CLICKED LEADERBOARD")
//                }
//                R.id.action_wallet -> {
//                    Toast.makeText(this, "This is a wallet", Toast.LENGTH_SHORT)
//                    Log.d(tag, "YOU CLICKED WALLET")
//                }
//                R.id.action_message ->{
//                    Toast.makeText(this, "This is a message", Toast.LENGTH_SHORT)
//                    Log.d(tag, "YOU CLICKED MESSAGE")
//                }
//
//            }
//            // Close the drawer
//           // drawer_layout.closeDrawer(GravityCompat.START)
//            true
//        }
//
//
//    }

}
