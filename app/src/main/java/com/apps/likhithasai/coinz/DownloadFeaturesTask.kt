package com.apps.likhithasai.coinz

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class DownloadFeaturesTask(private val caller: DownloadCompleteListener) : AsyncTask<String, Void, String>() {
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
            var result: String = ""
            try {
                do {
                    line = bufferReader.readLine()
                    if (line != null) {
                        result += line
                    }
                } while (line != null)
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