package com.apps.likhithasai.coinz

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Download Maps Class
 *
 * This class is responsible for thr download of the maps. It's an async task so that the download is done on a different
 * thread from the main thread. It essentially gets the data from the URL and converts it to a JSON string.
 *
 * @property caller An object of the DownloadCompleteListener interface to help access the String produced from download of the maps
 */
@Suppress("UNREACHABLE_CODE")
class DownloadFeaturesTask(private val caller: DownloadCompleteListener) : AsyncTask<String, Void, String>() {
        @SuppressLint("StaticFieldLeak")
        var context:Context ?= null
        private val tag = "DownloadFeaturesTask"



        override fun doInBackground(vararg urls: String): String = try {
            loadFileFromNetwork(urls[0])
        } catch (e: IOException) {
            "Unable to load content. Check your network connection"
        }



        private fun loadFileFromNetwork(urlString: String): String {
            Log.d(tag, "[onMapReady] Inside loadFileFromNetwork")
            val stream : InputStream = downloadUrl(urlString)

            val bufferReader = BufferedReader(InputStreamReader(stream))
            var line: String
            var result = ""
            try {
                do {
                    line = bufferReader.readLine()
                    if (line != null) {
                        result += line
                    }
                } while (true)
                stream.close()
            } catch (ex: Exception) {

            }
            return result
        }

        @Throws(IOException::class)
        private fun downloadUrl(urlString: String): InputStream {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            // Also available: HttpsURLConnection
            conn.readTimeout = 10000 // milliseconds
            conn.connectTimeout = 15000 // milliseconds
            conn.requestMethod = "GET"
            conn.doInput = true
            conn.connect() // Starts the query
            return conn.inputStream
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                caller.downloadComplete(result)
            }
            Log.d("DownloadFeaturesTask","Download Complete")
        }

}