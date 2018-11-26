package com.apps.likhithasai.coinz

import android.support.v7.widget.RecyclerView;
import android.util.Log
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast
import kotlinx.android.synthetic.main.rv_item.view.*


class CustomAdaptor(val wallet: MutableSet<String>?): RecyclerView.Adapter<CustomViewHolder>() {
    private val tag="CustomAdaptor"
    val titles = mutableListOf("PENY", "SHIL", "Fuck Coinz", "Se","sd","sad","sdfs","sdf","sdf","asd","sad")
    val titles2 = mutableListOf("75712", "1212", "12", "324","234","123","123","123","123","124","134")
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
        val currency = wallet?.elementAt(position)?.substring(10,14)
        val value = wallet?.elementAt(position)?.substring(14)
        Log.d(tag, "Currency: $currency Value: $value")

        holder.view.currency.text = currency
        holder.view.value.text = value

        holder.view.deposit.setOnClickListener {
            //Log.d(tag,"Currency: ${holder.view.currency.text} Value: ${holder.view.value.text} ")


            notifyItemRemoved(position)
            notifyItemRangeChanged(position, titles.size);
        }

    }
}

class CustomViewHolder(val view:View): RecyclerView.ViewHolder(view){


}