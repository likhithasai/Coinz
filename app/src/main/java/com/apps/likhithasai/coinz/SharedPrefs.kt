package com.apps.likhithasai.coinz

import android.content.Context
import android.content.Context.MODE_PRIVATE

class SharedPrefs(applicationContext: Context?) {

    private var pref = applicationContext?.getApplicationContext()?.getSharedPreferences("MyPref", MODE_PRIVATE)
    private var editor = pref?.edit()

    var mapfeat: String
        get() = pref!!.getString("map", "")
        set(value) = pref!!.edit()!!.putString("map", value).apply()

    var currentUser : String
        get() = pref!!.getString("current user", "")
        set(value) = pref!!.edit()!!.putString("current user", value).apply()

    var currentUserName : String
        get() = pref!!.getString("current username", "")
        set(value) = pref!!.edit()!!.putString("current username", value).apply()

    var coinsCollected: MutableSet<String>?
        get() = pref!!.getStringSet("$currentUser coins", emptySet())
        set(set) = editor!!.putStringSet("$currentUser coins", set).apply()

    var wallet: MutableSet<String>?
        get() = pref!!.getStringSet("$currentUser wallet", emptySet())
        set(set) = editor!!.putStringSet("$currentUser wallet", set).apply()

    var goldcoins : String
        get() = pref!!.getString("$currentUser total gold", "0")
        set(value) = editor!!.putString("$currentUser total gold", value).apply()

    var spareChange : String
        get() = pref!!.getString("$currentUser total spare change", "0")
        set(value) = editor!!.putString("$currentUser total spare change", value).apply()

    var coinConstraint: Int
        get() = pref!!.getInt("$currentUser coin constraint", 0)
        set(value) = pref!!.edit()!!.putInt("$currentUser coin constraint", value).apply()

    var depositLimit: Int
        get() = pref!!.getInt("$currentUser deposit limit", 0)
        set(value) = pref!!.edit()!!.putInt("$currentUser deposit limit", value).apply()

    var distanceWalked: Float
        get() = pref!!.getFloat("$currentUser distance walked", 0F)
        set(value) = pref!!.edit()!!.putFloat("$currentUser distance walked", value).apply()

//    var peny : String
//        get() = pref!!.getString("$currentUser total peny", "0")
//        set(value) = editor!!.putString("$currentUser total peny", value).apply()
//
//    var dolr : String
//        get() = pref!!.getString("$currentUser total dolr", "0")
//        set(value) = editor!!.putString("$currentUser total dolr", value).apply()
//
//    var quid : String
//        get() = pref!!.getString("$currentUser total quid", "0")
//        set(value) = editor!!.putString("$currentUser total quid", value).apply()
//
//    var shil : String
//        get() = pref!!.getString("$currentUser total shil", "0")
//        set(value) = editor!!.putString("$currentUser total shil", value).apply()

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

    var lastDownloadDate: String
        get() = pref!!.getString("lastDownloadDate", "")
        set(value) = editor!!.putString("lastDownloadDate", value).apply()

}
