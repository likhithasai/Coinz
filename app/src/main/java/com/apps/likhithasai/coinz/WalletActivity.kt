package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_wallet.*


/**
 * Activity class to implement wallet
 *
 * This class models the wallet activity. The primary objective being sending the data to the
 * CustomAdaptor class to fill in the Recycler view.
 *
 * @property prefs an instance of the shared prefs class to acquire rates
 */
class WalletActivity : AppCompatActivity() {

    private val tag = "WalletActivity"

    private var prefs:SharedPrefs ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        prefs = SharedPrefs(applicationContext)
        val shilRate = prefs!!.shil_rate
        val dolrRate = prefs!!.dolr_rate
        val penyRate = prefs!!.peny_rate
        val quidRate = prefs!!.quid_rate
        val user = prefs!!.currentUser



        /**
         * The function call is to wait for the data to be read from the database before transferring it to
         * the adaptor as database processes are async processes.
         */
        readData(object : MyCallBack {
            override fun onCallBack(valuesWallet: Set<String>, coins: MutableMap<String, Any>) {
                recyclerView_main.layoutManager = LinearLayoutManager(this@WalletActivity)
                recyclerView_main.adapter = CustomAdaptor(valuesWallet.toMutableSet(), coins ,applicationContext, user, shilRate, penyRate, dolrRate, quidRate)
            }
        })

    }

    /**
     *
     * The readData function reads data related to the users data from the firebase database to send to the adaptor
     * and fill the recycler view.
     *
     * @param myCallback interface object to listen to the data
     *
     */
    private fun readData(myCallback: MyCallBack) {
        val documentReference = FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser)
        documentReference.get().addOnCompleteListener { task ->
            Log.d(tag, "inOnComplete")
            if (task.isSuccessful){
                val document = task.result
                val walletdb = document!!.data
                val wallet = mutableSetOf<String>()
                for ((key, value) in walletdb) {
                    if (key != "gold" && key != "sparechange"){
                        Log.d(tag, "Value: $value")
                        wallet.add(value as String)
                    }

                }

                myCallback.onCallBack(wallet, walletdb.toMutableMap())
            }
        }
    }



}