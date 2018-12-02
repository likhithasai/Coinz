package com.apps.likhithasai.coinz

interface MyCallBack {
    fun onCallBack(valuesWallet: Set<String>, coins: MutableMap<String, Any>)
}