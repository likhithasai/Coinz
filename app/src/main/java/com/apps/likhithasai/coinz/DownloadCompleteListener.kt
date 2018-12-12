package com.apps.likhithasai.coinz

/**
 * Functional interface for map download
 *
 * This interface helps in getting the string after completion of the download of map
 * and can be accessed through an object.
 *
 * @property result String that is returned upon the download completion
 */
interface DownloadCompleteListener {
    fun downloadComplete(result: String)
}