package com.apps.likhithasai.coinz

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext
import android.content.SharedPreferences
import android.R.id.edit
import java.math.BigDecimal
import java.math.BigInteger

class SharedPrefs(applicationContext: Context?) {

    var pref = applicationContext?.getApplicationContext()?.getSharedPreferences("MyPref", MODE_PRIVATE)
    var editor = pref?.edit()

    var currentUser : String
        get() = pref!!.getString("current user", "")
        set(value) = pref!!.edit()!!.putString("current user", value).apply()

    var coinsCollected: MutableSet<String>?
        get() = pref!!.getStringSet("List", emptySet())
        set(set) = editor!!.putStringSet("List", set).apply()

    var peny : String
        get() = pref!!.getString("$currentUser total peny", "0")
        set(value) = editor!!.putString("$currentUser total peny", value).apply()

    var dolr : String
        get() = pref!!.getString("$currentUser total dolr", "0")
        set(value) = editor!!.putString("$currentUser total dolr", value).apply()

    var quid : String
        get() = pref!!.getString("$currentUser total quid", "0")
        set(value) = editor!!.putString("$currentUser total quid", value).apply()

    var shil : String
        get() = pref!!.getString("$currentUser total shil", "0")
        set(value) = editor!!.putString("$currentUser total shil", value).apply()

    //Rates for the day

    var peny_rate : String
        get() = pref!!.getString("$currentUser peny rate", "0")
        set(value) = editor!!.putString("$currentUser peny rate", value).apply()

    var dolr_rate : String
        get() = pref!!.getString("$currentUser dolr rate", "0")
        set(value) = editor!!.putString("$currentUser dolr rate", value).apply()

    var quid_rate : String
        get() = pref!!.getString("$currentUser quid rate", "0")
        set(value) = editor!!.putString("$currentUser quid rate", value).apply()

    var shil_rate : String
        get() = pref!!.getString("$currentUser shil rate", "0")
        set(value) = editor!!.putString("$currentUser shil rate", value).apply()


}
