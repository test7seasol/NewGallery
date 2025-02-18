package com.gallery.photos.editpic.Activity

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class MyApplicationClass : Application() {
    companion object {
        fun isConnected(context: Context): kotlin.Boolean {
            return try {
                val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val nInfo = cm.activeNetworkInfo
                nInfo != null && nInfo.isAvailable && nInfo.isConnected
            } catch (e: Exception) {
                Log.e("Connectivity Exception", e.message!!)
                false
            }
        }

        lateinit var ctx: MyApplicationClass

        var sharedPreferences: SharedPreferences? = null

        fun getStoreBooleanValue(key: String?): Boolean {
            return sharedPreferences!!.getBoolean(key, false)
        }


        fun putString(key: String, value: String) {
            sharedPreferences!!.edit()?.putString(key, value)!!.apply()
        }

        fun putBoolean(key: String, value: Boolean) {
            sharedPreferences!!.edit()?.putBoolean(key, value)!!.apply()
        }

        fun getString(key: String) = sharedPreferences?.getString(key, "en")

        fun getBoolean(key: String) = sharedPreferences?.getBoolean(key, false)
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this

        try {
            ctx = this

            val masterKey =
                MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

            sharedPreferences = EncryptedSharedPreferences.create(
                this,
                "MyGalleryApp",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            sharedPreferences =
                getSharedPreferences("prefs_file_name-gallery", Context.MODE_PRIVATE)
            e.printStackTrace()
        }

    }
}