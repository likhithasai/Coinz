package com.apps.likhithasai.coinz

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext
import android.content.SharedPreferences
import android.R.id.edit

class SharedPrefs(applicationContext: Context?) {

    var pref = applicationContext?.getApplicationContext()?.getSharedPreferences("MyPref", MODE_PRIVATE)
    var editor = pref?.edit()

    var currentUser : String
        get() = pref!!.getString("current user", "")
        set(value) = pref!!.edit()!!.putString("current user", value).apply()

    var coinsCollected: MutableSet<String>?
        get() = pref!!.getStringSet("List", emptySet())
        set(set) = editor!!.putStringSet("List", set).apply()
}
