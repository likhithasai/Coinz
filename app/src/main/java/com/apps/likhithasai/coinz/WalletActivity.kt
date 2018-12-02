package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_wallet.*



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

        readData(object : MyCallBack {
            override fun onCallBack(valuesWallet: Set<String>, coins: MutableMap<String, Any>) {
                recyclerView_main.layoutManager = LinearLayoutManager(this@WalletActivity)
                recyclerView_main.adapter = CustomAdaptor(valuesWallet.toMutableSet(), coins ,user, shilRate, penyRate, dolrRate, quidRate)
            }
        })

    }

    private fun readData(myCallback: MyCallBack) {
        val documentReference = FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser)
        documentReference.get().addOnCompleteListener { task ->
            Log.d(tag, "inOnComplete")
            if (task.isSuccessful){
                val document = task.result
                val walletdb = document!!.data
                val wallet = mutableSetOf<String>()
                for ((key, value) in walletdb) {
                    if (key != "gold"){
                        Log.d(tag, "Value: $value")
                        wallet.add(value as String)
                    }

                }

                myCallback.onCallBack(wallet, walletdb.toMutableMap())
            }
        }
    }



}