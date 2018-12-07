package com.apps.likhithasai.coinz

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.rv_item.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue


class CustomAdaptor(val wallet: MutableSet<String>?,val walletdb: MutableMap<String, Any>,val context:Context, val user: String, val shil_rate:String, val peny_rate:String, val dolr_rate:String, val quid_rate:String): RecyclerView.Adapter<CustomViewHolder>() {
    private val tag="CustomAdaptor"
    var dbRef = FirebaseDatabase.getInstance().reference
    private var prefs:SharedPrefs ?= null

    override fun getItemCount(): Int {
        var count:Int = 0
        count = wallet?.size!!
        return count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.rv_item, parent, false)
        prefs = SharedPrefs(context)

        prefs!!.coinConstraint = 28
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currency = wallet?.elementAt(position)?.substring(0,4)
        val value = wallet?.elementAt(position)?.substring(4)

        Log.d(tag, "Currency: $currency Value: $value")
        holder.view.currency.text = currency
        holder.view.value.text = value

        holder.view.deposit.setOnClickListener {

            val valueToDelete = currency + value

            val ref = FirebaseFirestore.getInstance().collection("UsersWallet").document(user!!)
            //val ref2 = FirebaseFirestore.getInstance().collection("UsersWallet")

            //var keytoDelete:String = "0"

            val updates = HashMap<String, Any>()

            for ((key, value) in walletdb) {
                if (value.equals(valueToDelete)){
                    updates[key] = FieldValue.delete()
                } else{
                    Log.d(tag,"Value to delete: $value and it's key: $key" )
                }
            }


            if (prefs!!.coinConstraint <= 25){
                val ref = FirebaseFirestore.getInstance().collection("UsersWallet").document(user!!)
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
                        prefs!!.goldcoins = newGold.toString()

                        //val userdata = User(prefs!!.currentUserName, newGold.toString())

                        FirebaseFirestore.getInstance().collection("Leaderboards").document(prefs!!.currentUser).set(mapOf( "name" to prefs!!.currentUserName,
                                "score" to newGold.toString()))


                        ref.update(updates).addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(tag, "Database updated after the deposit")
                            }
                        }
                    }
                }, "gold")


                Log.d(tag, "Old Gold: $goldcoins")
            } else
            {
                val ref = FirebaseFirestore.getInstance().collection("UsersWallet").document(user!!)
                var sparechange:String = "0"
                var newGold:BigDecimal = BigDecimal("0")

                readData(object : GoldCallBack{
                    override fun onCallBack(spareChange: String) {
                        sparechange = spareChange
                        when (currency) {
                            "PENY" -> {
                                var oldGold:BigDecimal = sparechange.toBigDecimal().setScale(3, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold = oldGold + ((value!!.toBigDecimal().setScale(3, RoundingMode.CEILING))* (peny_rate!!.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")
                            }
                            "SHIL" -> {
                                var oldGold:BigDecimal = sparechange.toBigDecimal().setScale(3, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold = oldGold + ((value!!.toBigDecimal().setScale(3, RoundingMode.CEILING))* (shil_rate!!.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")

                            }
                            "DOLR" -> {
                                var oldGold:BigDecimal = sparechange.toBigDecimal().setScale(3, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold= oldGold + ((value!!.toBigDecimal().setScale(3, RoundingMode.CEILING))* (dolr_rate!!.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")
                            }
                            "QUID" -> {
                                var oldGold:BigDecimal = sparechange.toBigDecimal().setScale(3, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold = oldGold+ ((value!!.toBigDecimal().setScale(3, RoundingMode.CEILING))* (quid_rate!!.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")

                            }
                        }

                        ref.update("sparechange", newGold.toString()).addOnSuccessListener {
                            Log.d(tag, "Spare Change updated")
                        }
                        prefs!!.spareChange = newGold.toString()


                        ref.update(updates).addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(tag, "Database updated after the deposit")
                            }
                        }

                    }
                }, "sparechange")
            }

            wallet!!.remove(valueToDelete)
            notifyDataSetChanged()
        }


    }


    private fun depositAsGold(currency: String?, value: String?) {


    }

    private fun readData(myCallBack: GoldCallBack, key: String?) {
        var gold:Any ?= null
        var sparechange:Any ?= null
        if (key.equals("gold")){
            FirebaseFirestore.getInstance().collection("UsersWallet").document(user!!).get().addOnSuccessListener {
                gold = it.get("gold")
                if (gold == null){
                    gold = "0"
                }
                Log.d(tag, "Old Gold" + gold)
                myCallBack.onCallBack(gold as String)
            }
        } else {
            FirebaseFirestore.getInstance().collection("UsersWallet").document(user!!).get().addOnSuccessListener {
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

class CustomViewHolder(val view:View): RecyclerView.ViewHolder(view){


}

