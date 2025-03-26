package com.gallery.photos.editpic.Activity

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.google.firebase.analytics.FirebaseAnalytics
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
        fun setStatuaryPadding(root: View?) {
            ViewCompat.setOnApplyWindowInsetsListener(root!!) { view: View, insets: WindowInsetsCompat ->
                val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                view.setPadding(
                    0,
                    statusBarHeight,
                    0,
                    navBarHeight
                ) // Adds padding so content starts below the status bar
                insets
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this

        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)

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

    fun getTestDeviceIdList(): List<String> {
        val arrayList = ArrayList<String>()
        try {
            arrayList.add("7F816931291832C442047C2FA281EA79")
            arrayList.add("04173C3D642EF3F97DD931279FEC32D9")
            arrayList.add("A4AE7EE024E1129EA7DF637BE76C3210")
            arrayList.add("DB413AEE6812CCEE0F60921013179773")
        }catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return arrayList
    }
    var ABMyAddPrefs: MyAddPrefs? = null

    fun getData(context: Context?, isCall: Boolean = true) {
        Log.d(
            "TAG", "getDatassss: " + MyAESUTIL.decrypt(
                MyAllAdCommonClass.JSON_URL
            )
        )
        val requestQueue = Volley.newRequestQueue(context)
        val req = JsonObjectRequest(
            Request.Method.GET, MyAESUTIL.decrypt(
                JSON_URL
            ), null, { response ->
                try {
                    val tutorialsObject = JSONObject(response.toString())
                    /* ABMyAddPrefs?.admNativeId =
                         if (BuildConfig.DEBUG) "/6499/example/native" else tutorialsObject.getString(
                             "nativeId"
                         )
                     ABMyAddPrefs?.admInterId =
                         if (BuildConfig.DEBUG) "/6499/example/interstitial" else tutorialsObject.getString(
                             "interstialId"
                         )
                     ABMyAddPrefs?.admBannerId =
                         if (BuildConfig.DEBUG) "/6499/example/banner" else tutorialsObject.getString(
                             "bannerId"
                         )
                     ABMyAddPrefs?.admAppOpenId =
                         if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/9257395921" else tutorialsObject.getString(
                             "appopenId"
                         )*/

                    ABMyAddPrefs?.admNativeId =
                        if (BuildConfig.DEBUG) "ive" else tutorialsObject.getString(
                            "nativeId"
                        )
                    ABMyAddPrefs?.admInterId =
                        if (BuildConfig.DEBUG) "titial" else tutorialsObject.getString(
                            "interstialId"
                        )
                    ABMyAddPrefs?.admBannerId =
                        if (BuildConfig.DEBUG) "nner" else tutorialsObject.getString(
                            "bannerId"
                        )
                    ABMyAddPrefs?.admAppOpenId =
                        if (BuildConfig.DEBUG) "395921" else tutorialsObject.getString(
                            "appopenId"
                        )

                    ABMyAddPrefs?.admShowclick =
                        Integer.parseInt(tutorialsObject.getString("afterClick"));
                    ABMyAddPrefs?.buttonColor = tutorialsObject.getString("addButtonColor")

                    val tutorialsObject2 = tutorialsObject.getJSONObject("extraFields")
                    ABMyAddPrefs?.admInlineBannerId =   if (BuildConfig.DEBUG) "/6499/example/banner" else tutorialsObject2.getString("inline_banner")
                    //                    ABAddPrefs?.setSplashInterAppOpen(
                    //                        tutorialsObject2.getString("splash_inter_appopen").toInt()
                    //                    )

                    tutorialsObject.toGson().log()
                    if (isCall) {
                        MyAppOpenManager(ctx);
                    }


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