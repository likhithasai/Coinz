package com.apps.likhithasai.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_message.*
import java.math.RoundingMode
import kotlin.math.roundToInt

class MessageActivity : AppCompatActivity() {

    private val tag = "MessageActivity"

    var prefs:SharedPrefs ?= null


    companion object {
        const val COLLECTION_KEY = "Chat"
        const val DOCUMENT_KEY = "Message"
        const val NAME_FIELD = "Name"
        const val TEXT_FIELD = "Text"
    }

    private val firestoreChat by lazy {
        FirebaseFirestore.getInstance().collection(COLLECTION_KEY).document(DOCUMENT_KEY)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        prefs = SharedPrefs(applicationContext)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("None", "Top", "Bottom"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        positionSpinner.adapter = adapter

        positionSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // either one will work as well
                // val item = parent.getItemAtPosition(position) as String
                val item = adapter.getItem(position)
            }
        }
        realtimeUpdateListener()
        btnSend.setOnClickListener{ sendMessage() }

    }


    private fun sendMessage() {
        val newMessage = mapOf(
                NAME_FIELD to userSend.text.toString(),
                TEXT_FIELD to amtSend.text.toString())
        firestoreChat.set(newMessage)
                .addOnSuccessListener( {
                    Toast.makeText(this@MessageActivity, "Message Sent", Toast.LENGTH_SHORT).show()
                })
                .addOnFailureListener { e -> Log.e("ERROR", e.message) }
    }

    private fun realtimeUpdateListener() {
        firestoreChat.addSnapshotListener { documentSnapshot, e ->
            when {
                e != null -> Log.e("ERROR", e.message)
                documentSnapshot != null && documentSnapshot.exists() -> {
                    with(documentSnapshot) {
                        txtDisp.text = "${data[NAME_FIELD]}:${data[TEXT_FIELD]}"
                    }
                }
            }
        }
    }


}
