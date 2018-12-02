package com.apps.likhithasai.coinz
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

interface MyCallBack {
    fun onCallBack(valuesWallet: Set<String>, coins: MutableMap<String, Any>);
}