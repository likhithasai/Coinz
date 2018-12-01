package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_wallet.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.Query
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


class WalletActivity : AppCompatActivity() {

    private val tag = "WalletActivity"

    var prefs:SharedPrefs ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        prefs = SharedPrefs(applicationContext)
        val shil_rate = prefs!!.shil_rate
        val dolr_rate = prefs!!.dolr_rate
        val peny_rate = prefs!!.peny_rate
        val quid_rate = prefs!!.quid_rate
        val user = prefs!!.currentUser

        readData(object : MyCallBack {
            override fun onCallBack(wallet: Set<String>) {
                recyclerView_main.layoutManager = LinearLayoutManager(this@WalletActivity)
                recyclerView_main.adapter = CustomAdaptor(wallet.toMutableSet(),user, shil_rate, peny_rate, dolr_rate, quid_rate)
            }
        })

    }

    fun readData(myCallback: MyCallBack) {
        var documentReference = FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser)
        documentReference.get().addOnCompleteListener(object: OnCompleteListener<DocumentSnapshot>{
            override fun onComplete(task: Task<DocumentSnapshot>) {
                Log.d(tag, "inOnComplete")
                if (task.isSuccessful){
                    val document = task.result
                    val walletdb = document!!.data
                    var wallet = mutableSetOf<String>()
                    var gold:String = ""
                    if (walletdb != null) {
                        for ((key, value) in walletdb) {
                            if (!key.equals("gold")){
                                Log.d(tag, "Value: $value")
                                wallet.add(value as String)
                            }
                            else{
                                gold = value as String
                            }
                        }
                    }
                    myCallback.onCallBack(wallet)
                }
            }

        })
    }



}