package com.apps.likhithasai.coinz

import android.annotation.SuppressLint
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
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.content_navigator.*

/**
 *
 * The Navigator class is the landing page of the app where the user can the view their gold and sparechange
 * It also holds the navigation bar of the app from where the user can navigate to different activities.
 *
 */

class Navigator : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var prefs: SharedPrefs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigator)
        setSupportActionBar(toolbar)

        val playButton = findViewById<Button>(R.id.button)

        /**
         * Setting the action bar
         */
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        /**
         * When the user clicks the play button, they will be able to navigate to the map
         * activity.
         */
        playButton.setOnClickListener {
            // Handler code here.
            //Toast.makeText(this, "Button is clicked", Toast.LENGTH_LONG).show();
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setUpInfo()
    }

    @SuppressLint("SetTextI18n")
    /**
     * The setUp Info inflates the textviews in the activity using appropriate data
     * obtained from shared prefs
     */
    private fun setUpInfo() {
        //Initialising shared prefs object to access data
        prefs = SharedPrefs(applicationContext)

        val shilRate = prefs!!.shil_rate
        val dolrRate = prefs!!.dolr_rate
        val quidRate = prefs!!.quid_rate
        val penyRate = prefs!!.peny_rate

        //A marquee to show the rates
        val tv = findViewById<TextView>(R.id.mywidget)
        tv.text = "Rates for today: SHIL to GOLD: $shilRate DOLR to GOLD: $dolrRate QUID to GOLD: $quidRate PENY to GOLD: $penyRate"
        tv!!.isSelected = true  // Set focus to the textview

        //Display the gold, spare change and user name
        userDisp.text = "User logged in: ${prefs!!.currentUserName}"
        goldDisp.text = prefs!!.goldcoins
        spareChangeDisp.text = prefs!!.spareChange

        //Share btn onClick Listener
        shareBtn.setOnClickListener {
            val s = "I possess ${prefs!!.goldcoins} gold coins. What's your net value on Coinz? ;)"
            val shareIntent = Intent()
            //Makes use of the share intent to enable sharing data
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, s)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Download the best game in town, Coinz!")
            startActivity(Intent.createChooser(shareIntent, "Share text via"))
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

    /**
     *
     * The onNavigationItemSelected handles the items clicks in the view by navigating to activities
     * based on the items.
     *
     * @param item of the MenItem type to access the different items in the nav menu
     *
     */
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
            R.id.action_logout -> {
                val intent = Intent(this@Navigator, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                FirebaseAuth.getInstance().signOut()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
