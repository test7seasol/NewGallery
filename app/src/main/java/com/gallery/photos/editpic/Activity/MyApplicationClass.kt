package com.gallery.photos.editpic.Activity

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.gallery.photos.editpic.BuildConfig
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.toGson
import com.gallery.photos.editpic.myadsworld.MyAESUTIL
import com.gallery.photos.editpic.myadsworld.MyAddPrefs
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass.JSON_URL
import com.gallery.photos.editpic.myadsworld.MyAppOpenManager
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.json.JSONException
import org.json.JSONObject

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

        fun getString(key: String) = sharedPreferences?.getString(key, "")

        fun getBoolean(key: String) = sharedPreferences?.getBoolean(key, false)
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this

        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        ABMyAddPrefs = MyAddPrefs(ctx)

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

    var ABMyAddPrefs: MyAddPrefs? = null

    fun getData(context: Context?) {
        Log.d(
            "TAG", "getDatassss: " + MyAESUTIL.encrypt(
                MyAllAdCommonClass.JSON_URL
            )
        )
        val requestQueue = Volley.newRequestQueue(context)
        val req = JsonObjectRequest(Request.Method.GET, MyAESUTIL.decrypt(
            JSON_URL
        ), null, { response ->
            try {
                val tutorialsObject = JSONObject(response.toString())
                ABMyAddPrefs?.admNativeId = tutorialsObject.getString("nativeId")
                ABMyAddPrefs?.admInterId = tutorialsObject.getString("interstialId")
                ABMyAddPrefs?.admBannerId = tutorialsObject.getString("bannerId")
                ABMyAddPrefs?.admAppOpenId = tutorialsObject.getString("appopenId")
                ABMyAddPrefs?.admShowclick =
                    Integer.parseInt(tutorialsObject.getString("afterClick"));

                //                    val tutorialsObject2 = tutorialsObject.getJSONObject("extraFields")
                //                    ABAddPrefs?.setSplashInterAppOpen(
                //                        tutorialsObject2.getString("splash_inter_appopen").toInt()
                //                    )

                tutorialsObject.toGson().log()
                MyAppOpenManager(ctx);

                // Load an ad.
                //                        loadAd();
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.e("FATZ", "onResponse: " + e.message)
            }
        }) { error -> Log.e("FATZ", "onErrorResponse: $error") }
        requestQueue.add(req)
    }
}