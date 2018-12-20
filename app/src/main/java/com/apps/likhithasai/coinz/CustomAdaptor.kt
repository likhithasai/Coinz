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
import com.google.firebase.firestore.FieldValue

/**
 * Adaptor for the wallet
 *
 * This adaptor makes use of a recycler view to show the coins. Each of the coins is a
 * card with a 'deposit'-that-coin button.
 *
 * @property wallet Set of strings of the coin values for the recycler view
 * @property walletdb Map to help delete the coin in the database once deposited
 * @property context the application context to help access shared preferences
 * @property user the current user to access the database documents
 * @property shil_rate the conversion rate for shil
 * @property dolr_rate the conversion rate for dolr
 * @property quid_rate the conversion rate for quid
 * @property peny_rate tthe conversion rate for peny
 */
@Suppress("NAME_SHADOWING")
class CustomAdaptor(private val wallet: MutableSet<String>?, private val walletdb: MutableMap<String, Any>, val context:Context, val user: String, val shil_rate:String, val peny_rate:String, val dolr_rate:String, val quid_rate:String): RecyclerView.Adapter<CustomViewHolder>() {
    private val tag="CustomAdaptor"
    private var prefs:SharedPrefs ?= null

    override fun getItemCount(): Int {
        return wallet?.size!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rv_item, parent, false)

        prefs = SharedPrefs(context)
        
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

            val updates = HashMap<String, Any>()

            for ((key, value) in walletdb) {
                if (value == valueToDelete){
                    updates[key] = FieldValue.delete()
                } else{
                    Log.d(tag,"Value to delete: $value and it's key: $key" )
                }
            }


            if (prefs!!.depositLimit <= 25){
                val ref = FirebaseFirestore.getInstance().collection("UsersWallet").document(user)
                var goldcoins = "0"
                var newGold = BigDecimal("0")

                readData(object : GoldCallBack{
                    override fun onCallBack(result: String) {
                        goldcoins = result
                        when (currency) {
                            "PENY" -> {
                                val oldGold:BigDecimal = goldcoins.toBigDecimal().setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold = oldGold + ((value!!.toBigDecimal().setScale(5, RoundingMode.CEILING))* (peny_rate.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")
                            }
                            "SHIL" -> {
                                val oldGold:BigDecimal = goldcoins.toBigDecimal().setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold = oldGold + ((value!!.toBigDecimal().setScale(5, RoundingMode.CEILING))* (shil_rate.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")

                            }
                            "DOLR" -> {
                                val oldGold:BigDecimal = goldcoins.toBigDecimal().setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold= oldGold + ((value!!.toBigDecimal().setScale(5, RoundingMode.CEILING))* (dolr_rate.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")
                            }
                            "QUID" -> {
                                val oldGold:BigDecimal = goldcoins.toBigDecimal().setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold = oldGold+ ((value!!.toBigDecimal().setScale(5, RoundingMode.CEILING))* (quid_rate.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")

                            }
                        }

                        ref.update("gold", newGold.toString())
                        prefs!!.goldcoins = newGold.toString()

                        prefs!!.depositLimit = prefs!!.depositLimit + 1


                        FirebaseFirestore.getInstance().collection("Leaderboards").document(prefs!!.currentUser).set(mapOf( "name" to prefs!!.currentUserName,
                                "score" to newGold.toInt()))

                        ref.update(updates).addOnCompleteListener { it ->
                            if (it.isSuccessful){
                                Log.d(tag, "Database updated after the deposit")
                                if (context is WalletActivity) {
                                    context.displaytextgold()
                                }
                            }
                        }
                    }
                }, "gold")


                Log.d(tag, "Old Gold: $goldcoins")
            } else
            {
                val ref = FirebaseFirestore.getInstance().collection("UsersWallet").document(user)
                var sparechange: String
                var newGold = BigDecimal("0")

                readData(object : GoldCallBack{
                    override fun onCallBack(result: String) {
                        sparechange = result
                        when (currency) {
                            //Calculate new amount of gold and update database
                            "PENY" -> {
                                val oldGold:BigDecimal = sparechange.toBigDecimal().setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold = oldGold + ((value!!.toBigDecimal().setScale(5, RoundingMode.CEILING))* (peny_rate.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")
                            }
                            "SHIL" -> {
                                val oldGold:BigDecimal = sparechange.toBigDecimal().setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold = oldGold + ((value!!.toBigDecimal().setScale(5, RoundingMode.CEILING))* (shil_rate.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")

                            }
                            "DOLR" -> {
                                val oldGold:BigDecimal = sparechange.toBigDecimal().setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold= oldGold + ((value!!.toBigDecimal().setScale(5, RoundingMode.CEILING))* (dolr_rate.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")
                            }
                            "QUID" -> {
                                val oldGold:BigDecimal = sparechange.toBigDecimal().setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"Old Gold in: $oldGold")
                                newGold = oldGold+ ((value!!.toBigDecimal().setScale(5, RoundingMode.CEILING))* (quid_rate.toBigDecimal().setScale(3, RoundingMode.CEILING)))
                                newGold = newGold.setScale(5, RoundingMode.CEILING)
                                Log.d(tag,"New Gold: $newGold")

                            }
                        }

                        ref.update("sparechange", newGold.toString()).addOnSuccessListener {
                            Log.d(tag, "Spare Change updated")
                        }
                        prefs!!.spareChange = newGold.toString()


                        ref.update(updates).addOnCompleteListener {it ->
                            if (it.isSuccessful){
                                Log.d(tag, "Database updated after the deposit")
                                if (context is WalletActivity) {
                                    context.displaytextsparechange()
                                }
                            }
                        }

                    }
                }, "sparechange")
            }

            wallet!!.remove(valueToDelete)
            notifyDataSetChanged()
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
            FirebaseFirestore.getInstance().collection("UsersWallet").document(user).get().addOnSuccessListener {
                gold = it.get("gold")
                if (gold == null){
                    gold = "0"
                }
                Log.d(tag, "Old Gold $gold")
                myCallBack.onCallBack(gold as String)
            }
        } else {
            FirebaseFirestore.getInstance().collection("UsersWallet").document(user).get().addOnSuccessListener {
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

class CustomViewHolder(val view:View): RecyclerView.ViewHolder(view)

