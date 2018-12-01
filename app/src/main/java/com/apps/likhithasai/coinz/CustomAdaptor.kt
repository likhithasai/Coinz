package com.apps.likhithasai.coinz

import android.support.v7.widget.RecyclerView;
import android.util.Log
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.rv_item.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.activity_wallet.*
import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent




class CustomAdaptor(val wallet: MutableSet<String>?, val user: String, val shil_rate:String, val peny_rate:String, val dolr_rate:String, val quid_rate:String): RecyclerView.Adapter<CustomViewHolder>() {
    private val tag="CustomAdaptor"
    var dbRef = FirebaseDatabase.getInstance().reference

    override fun getItemCount(): Int {
        var count:Int = 0
        count = wallet?.size!!
        return count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.rv_item, parent, false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currency = wallet?.elementAt(position)?.substring(0,4)
        val value = wallet?.elementAt(position)?.substring(4)
        Log.d(tag, "Currency: $currency Value: $value")
        holder.view.currency.text = currency
        holder.view.value.text = value

        holder.view.deposit.setOnClickListener {
            //Log.d(tag,"Currency: ${holder.view.currency.text} Value: ${holder.view.value.text} ")

//            val intent1 = Intent(context, WalletActivity::class.java)
//            intent1.putExtra("currency", holder.view.currency.text!!)
//            intent1.putExtra("text", holder.view.value.text!!)
//            context.startActivity(intent1)
//            (context as Activity).finish()

            val ref = FirebaseFirestore.getInstance().collection("UsersWallet").document(user!!)
            val ref2 = FirebaseFirestore.getInstance().collection("UsersWallet")


            var goldcoins:String = "0"
            var newGold:BigDecimal = BigDecimal("0")

            readData(object : GoldCallBack{
                override fun onCallBack(gold: String) {
                    goldcoins = gold
                    when (currency) {
                        "PENY" -> {
                            var oldGold:BigDecimal = goldcoins.toBigDecimal().setScale(3, RoundingMode.CEILING)
                            Log.d(tag,"Old Gold in: $oldGold")
                            newGold = oldGold + ((value!!.toBigDecimal().setScale(3, RoundingMode.CEILING))* (peny_rate!!.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                            newGold = newGold.setScale(5, RoundingMode.CEILING)
                            Log.d(tag,"New Gold: $newGold")
                        }
                        "SHIL" -> {
                            var oldGold:BigDecimal = goldcoins.toBigDecimal().setScale(3, RoundingMode.CEILING)
                            Log.d(tag,"Old Gold in: $oldGold")
                            newGold = oldGold + ((value!!.toBigDecimal().setScale(3, RoundingMode.CEILING))* (shil_rate!!.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                            newGold = newGold.setScale(5, RoundingMode.CEILING)
                            Log.d(tag,"New Gold: $newGold")

                        }
                        "DOLR" -> {
                            var oldGold:BigDecimal = goldcoins.toBigDecimal().setScale(3, RoundingMode.CEILING)
                            Log.d(tag,"Old Gold in: $oldGold")
                            newGold= oldGold + ((value!!.toBigDecimal().setScale(3, RoundingMode.CEILING))* (dolr_rate!!.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                            newGold = newGold.setScale(5, RoundingMode.CEILING)
                            Log.d(tag,"New Gold: $newGold")
                        }
                        "QUID" -> {
                            var oldGold:BigDecimal = goldcoins.toBigDecimal().setScale(3, RoundingMode.CEILING)
                            Log.d(tag,"Old Gold in: $oldGold")
                            newGold = oldGold+ ((value!!.toBigDecimal().setScale(3, RoundingMode.CEILING))* (quid_rate!!.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                            newGold = newGold.setScale(5, RoundingMode.CEILING)
                            Log.d(tag,"New Gold: $newGold")

                        }
                    }

                    ref.update("gold", newGold.toString())
                }
            })





//            ref.get().addOnSuccessListener {
//                gold = it.get("gold") as String
//                Log.d(tag, "Old Gold" + gold)
//            }


            Log.d(tag, "Old Gold: " + goldcoins)

            val userdata = User("m.likhi", newGold.toString())

            dbRef.child("goldcoins").child(user)
                    .setValue(userdata)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(tag, "Gold coins added")
                        } else {
                            Log.d(tag, "Gold coins addition failed")
                        }
                    }

    //        ref.update("gold", newGold.toString())

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, wallet!!.size)
        }


    }

    private fun readData(myCallBack: GoldCallBack) {
        var gold:Any ?= null
        FirebaseFirestore.getInstance().collection("UsersWallet").document(user!!).get().addOnSuccessListener {
            gold = it.get("gold")
            if (gold == null){
                gold = "0"
            }
            Log.d(tag, "Old Gold" + gold)
            myCallBack.onCallBack(gold as String)
        }
        //The realtime datbase stuff.
//        dbRef.child("goldcoins").child(user).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val user:User = dataSnapshot.getValue(User::class.java)!!
//                if (user!!.name.equals("m.likhi")) {
//                    gold = user.score
//                }
//
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.d(tag, "Error trying to get user for update " +
//                        "" + databaseError)
//            }
//        })

    }
}

class CustomViewHolder(val view:View): RecyclerView.ViewHolder(view){


}

