package com.apps.likhithasai.coinz

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_navigator.*
import kotlinx.android.synthetic.main.app_bar_navigator.*
import android.content.Intent
import android.widget.Button




class Navigator : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

//    private var mAuth: FirebaseAuth? = null
//    var prefs: SharedPrefs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigator)
        setSupportActionBar(toolbar)

        val playButton = findViewById<Button>(R.id.button)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        playButton.setOnClickListener {
            // Handler code here.
            //Toast.makeText(this, "Button is clicked", Toast.LENGTH_LONG).show();
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigator, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {    //REMOVE RETURN IF THINGS DONT WORK
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.action_health -> {
                intent = Intent(this, PedometerActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                this.startActivity(intent)
            }
            R.id.action_message -> {
                intent = Intent(this, MessageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                this.startActivity(intent)

            }
            R.id.action_leaderboard -> {
                intent = Intent(this, LeaderboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                this.startActivity(intent)

            }
            R.id.action_wallet -> {
                intent = Intent(this, WalletActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                this.startActivity(intent)

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
