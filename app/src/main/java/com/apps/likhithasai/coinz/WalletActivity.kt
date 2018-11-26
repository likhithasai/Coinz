package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_wallet.*
import java.math.RoundingMode
import kotlin.math.roundToInt
import com.google.firebase.firestore.CollectionReference
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
//import sun.text.normalizer.UTF16.append
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
//import com.google.firebase.firestore.DocumentSnapshot
//import org.junit.experimental.results.ResultMatchers.isSuccessful
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

        readData(object : MyCallBack {
            override fun onCallBack(wallet: Set<String>) {
                recyclerView_main.layoutManager = LinearLayoutManager(this@WalletActivity)
                recyclerView_main.adapter = CustomAdaptor(wallet.toMutableSet())
            }
        })
   }

    fun readData(myCallback: MyCallBack) {
        var documentReference = FirebaseFirestore.getInstance().collection("users").document(prefs!!.currentUser)
        documentReference.get().addOnCompleteListener(object: OnCompleteListener<DocumentSnapshot>{
            override fun onComplete(task: Task<DocumentSnapshot>) {
                if (task.isSuccessful){
                    val document = task.result
                    val walletdb = document.data
                    var wallet = mutableSetOf<String>()
                    for ((key, value) in walletdb) {
                        Log.d(tag, "Value: $value")
                        wallet?.add(value as String)
                    }
                    myCallback.onCallBack(wallet)
                }
            }

        })
    }



}
