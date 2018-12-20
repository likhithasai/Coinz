package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_message.*
import java.math.BigDecimal
import java.math.RoundingMode

/**
 *Activity class for messaging spare change
 *
 */
class MessageActivity : AppCompatActivity() {

    private val tag = "MessageActivity"

    private var prefs: SharedPrefs? = null


    companion object {
        const val COLLECTION_KEY = "Chat"
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



        realtimeUpdateListener()
        btnSend.setOnClickListener { sendMessage() }

    }

    /**
     * sendMessage function sends the message from current user to the user the user chooses to send their
     * spare change to
     */
    private fun sendMessage() {
        if (amtSend != null){
            if(!userSend.text.equals(prefs!!.currentUserName)){
                val newMessage = mapOf(
                        NAME_FIELD to prefs!!.currentUserName,
                        TEXT_FIELD to amtSend.text.toString())
                firestoreChat.document(userSend.text.toString()).set(newMessage)
                        .addOnSuccessListener {
                            Toast.makeText(this@MessageActivity, "Message Sent", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e -> Log.e("ERROR", e.message) }

                var newSpareChange:BigDecimal

                readData(object : GoldCallBack{
                    override fun onCallBack(result: String) {
                        //Enter condition to see that value entered is less than
                        val oldSpareChange: BigDecimal = result.toBigDecimal().setScale(3, RoundingMode.CEILING)
                        Log.d(tag,"Old spare change in: $oldSpareChange")
                        newSpareChange = oldSpareChange - amtSend.text.toString().toBigDecimal().setScale(3, RoundingMode.CEILING)
                        newSpareChange = newSpareChange.setScale(5, RoundingMode.CEILING)
                        Log.d(tag,"New Gold: $newSpareChange")

                        FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser).update("sparechange", newSpareChange.toString()).addOnSuccessListener {
                            Log.d(tag, "Spare Change updated")
                        }

                        prefs!!.spareChange = newSpareChange.toString()

                        spareChange.text = "${prefs!!.spareChange} gold coins"



                    }
                }, "sparechange")
            }
            else {
                Toast.makeText(this, "Nice try but you can't send gold to your self", Toast.LENGTH_LONG)
            }
        } else {
            Toast.makeText(this, "Enter a valid value for amount", Toast.LENGTH_LONG)
        }

    }


    /**
     * realTimeUpdate Listener reads the database to see if the current user has any coins that
     * other users sent to them and updates their gold.
     */
    private fun realtimeUpdateListener() {
        var goldSent:String ?= null
        firestoreChat.document(prefs!!.currentUserName).addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> Log.e("ERROR", e.message)
                documentSnapshot != null && documentSnapshot.exists() -> {
                    with(documentSnapshot) {
                        if (data[NAME_FIELD] != null && data[TEXT_FIELD] != null ) {
                            val sb = Snackbar.make(findViewById(R.id.mlayout),"${data[NAME_FIELD]} sent you ${data[TEXT_FIELD]} gold coins", Snackbar.LENGTH_LONG)
                                    .setAction("Dismiss"){}
                            sb.show()
                            goldSent = data[TEXT_FIELD] as String
                        }

                    }
                }
            }
        }

        var newGold: BigDecimal

        if(goldSent!=null){
            readData(object : GoldCallBack{
                override fun onCallBack(result: String) {
                    val oldGold: BigDecimal = result.toBigDecimal().setScale(3, RoundingMode.CEILING)
                    Log.d(tag, "Old Gold in: $oldGold")
                    newGold = oldGold + goldSent!!.toBigDecimal().setScale(3, RoundingMode.CEILING)
                    newGold = newGold.setScale(5, RoundingMode.CEILING)
                    Log.d(tag, "New Gold: $newGold")

                    FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser).update("gold", newGold.toString()).addOnSuccessListener {
                        Log.d(tag, "Spare Change updated")
                    }

                    prefs!!.goldcoins = newGold.toString()

                    FirebaseFirestore.getInstance().collection("Leaderboards").document(prefs!!.currentUser).set(mapOf( "name" to prefs!!.currentUserName,
                            "score" to newGold.toString()))
                }
            }, "gold")

        }

        val updates = HashMap<String, Any>()
        updates[NAME_FIELD] = FieldValue.delete()
        updates[TEXT_FIELD] = FieldValue.delete()
        firestoreChat.document(prefs!!.currentUserName).update(updates).addOnSuccessListener {
            Log.d(tag, "The message deleted")
        }.addOnFailureListener {
            Log.d(tag, "Database update failed")
        }


    }

    /**
     * reaData function reads the gold and sparechange value of a user according to the key
     *
     * @param myCallBack The GoldCallBack interface object to help with the callback wrap up
     * @param key the key to read gold/sparechange
     */
    private fun readData(myCallBack: GoldCallBack, key: String?) {
        var gold:Any?
        var sparechange: Any?
        if (key.equals("gold")){
            FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser).get().addOnSuccessListener {
                gold = it.get("gold")
                if (gold == null){
                    gold = "0"
                }
                Log.d(tag, "Old Gold$gold")
                myCallBack.onCallBack(gold as String)
            }
        } else {
            FirebaseFirestore.getInstance().collection("UsersWallet").document(prefs!!.currentUser).get().addOnSuccessListener {
                sparechange = it.get("sparechange")
                if (sparechange == null){
                    sparechange = "0"
                }
                Log.d(tag, "Old sparechange $sparechange")
                myCallBack.onCallBack(sparechange as String)
            }



        }


    }


}


