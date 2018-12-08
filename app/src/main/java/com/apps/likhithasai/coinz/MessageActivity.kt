package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_message.*
import java.math.BigDecimal
import java.math.RoundingMode

class MessageActivity : AppCompatActivity() {

    private val tag = "MessageActivity"

    private var prefs: SharedPrefs? = null


    companion object {
        const val COLLECTION_KEY = "Chat"
        const val DOCUMENT_KEY = "Message"
        const val NAME_FIELD = "Name"
        const val TEXT_FIELD = "Text"
    }

    private val firestoreChat by lazy {
        FirebaseFirestore.getInstance().collection(COLLECTION_KEY)
        //Changing this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        prefs = SharedPrefs(applicationContext)

        spareChange.text = prefs!!.spareChange

        val users: MutableList<String> ?= null

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("whatevs", "likhi"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        positionSpinner.adapter = adapter

        positionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // either one will work as well
                // val item = parent.getItemAtPosition(position) as String
                val item = adapter.getItem(position)
            }
        }
        realtimeUpdateListener()
        btnSend.setOnClickListener { sendMessage() }

    }


    private fun sendMessage() {
        if (amtSend != null){
            val newMessage = mapOf(
                    NAME_FIELD to prefs!!.currentUserName,
                    TEXT_FIELD to amtSend.text.toString())
            firestoreChat.document(userSend.text.toString()).set(newMessage)
                    .addOnSuccessListener {
                        Toast.makeText(this@MessageActivity, "Message Sent", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e -> Log.e("ERROR", e.message) }

            var newSpareChange:BigDecimal = BigDecimal("0")

            readData(object : GoldCallBack{
                override fun onCallBack(spareChange: String) {
                    //Enter condition to see that value entered is less than
                    var oldSpareChange: BigDecimal = spareChange.toBigDecimal().setScale(3, RoundingMode.CEILING)
                    Log.d(tag,"Old spare change in: $oldSpareChange")
                    newSpareChange = oldSpareChange - amtSend.text.toString().toBigDecimal().setScale(3, RoundingMode.CEILING)
                    newSpareChange = newSpareChange.setScale(5, RoundingMode.CEILING)
                    Log.d(tag,"New Gold: $newSpareChange")

                    FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser).update("sparechange", newSpareChange.toString()).addOnSuccessListener {
                        Log.d(tag, "Spare Change updated")
                    }

                    prefs!!.spareChange = newSpareChange.toString()

                }
            }, "sparechange")
        } else {
            Toast.makeText(this, "Enter a valid value for amount", Toast.LENGTH_LONG)
        }

    }


    private fun realtimeUpdateListener() {
        var goldSent:String ?= null
        firestoreChat.document(prefs!!.currentUserName).addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> Log.e("ERROR", e.message)
                documentSnapshot != null && documentSnapshot.exists() -> {
                    with(documentSnapshot) {
                        if (data[NAME_FIELD] != null && data[TEXT_FIELD] != null )
                            txtDisp.text = "${data[NAME_FIELD]} sent you ${data[TEXT_FIELD]}"
                            goldSent = data[TEXT_FIELD] as String

                    }
                }
            }
        }

        var newGold:BigDecimal = BigDecimal("0")

        if(goldSent!=null){
            readData(object : GoldCallBack{
                override fun onCallBack(gold: String) {
                    var oldGold: BigDecimal = gold.toBigDecimal().setScale(3, RoundingMode.CEILING)
                    Log.d(tag, "Old Gold in: $oldGold")
                    newGold = oldGold + goldSent!!.toBigDecimal().setScale(3, RoundingMode.CEILING)
                    newGold = newGold.setScale(5, RoundingMode.CEILING)
                    Log.d(tag, "New Gold: $newGold")

                    FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser).update("gold", newGold.toString())
                    prefs!!.goldcoins = newGold.toString()

                    val userdata = User(prefs!!.currentUserName, newGold.toString())

                    FirebaseDatabase.getInstance().reference.child("goldcoins").child(prefs!!.currentUser)
                            .setValue(userdata)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(tag, "Gold coins added")
                                } else {
                                    Log.d(tag, "Gold coins addition failed")
                                }
                            }

                    FirebaseFirestore.getInstance().collection("Leaderboards").document(prefs!!.currentUser).set(mapOf( "name" to prefs!!.currentUserName,
                            "score" to newGold.toString()))
                }
            }, "gold")

        }

    }

    private fun readData(myCallBack: GoldCallBack, key: String?) {
        var gold:Any ?= null
        var sparechange:Any ?= null
        if (key.equals("gold")){
            FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser).get().addOnSuccessListener {
                gold = it.get("gold")
                if (gold == null){
                    gold = "0"
                }
                Log.d(tag, "Old Gold" + gold)
                myCallBack.onCallBack(gold as String)
            }
        } else {
            FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser).get().addOnSuccessListener {
                sparechange = it.get("sparechange")
                if (sparechange == null){
                    sparechange = "0"
                }
                Log.d(tag, "Old sparechange" + sparechange)
                myCallBack.onCallBack(sparechange as String)
            }



        }


    }




}


