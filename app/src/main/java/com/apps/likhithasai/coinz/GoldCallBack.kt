package com.apps.likhithasai.coinz

/**
 * Callback for gold download
 *
 * This interface helps in getting the string after obtaining the gold from the database
 *
 * @property result String that is returned upon the download completion
 */
interface GoldCallBack {
    fun onCallBack(result: String)
}