package com.gallery.photos.editpic.Activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.BuildConfig
import com.gallery.photos.editpic.callendservice.utils.CDOUtiler


open class BaseActivity : AppCompatActivity() {
    fun hideNavigationBar(activity: Activity) {
        try {
            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility or 2 or 4096
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        if (!BuildConfig.DEBUG)
        try {
            CDOUtiler.hideNavigationBar(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun onResume() {
        super.onResume()
        if (!BuildConfig.DEBUG)
        try {
            CDOUtiler.hideNavigationBar(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onWindowFocusChanged(z: Boolean) {
        super.onWindowFocusChanged(z)
        if (!BuildConfig.DEBUG)
        if (z) {
            try {
                CDOUtiler.hideNavigationBar(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun hideBottomNavigationBar(statusBarColor: Int) {


        /*
                window.statusBarColor = ContextCompat.getColor(this, statusBarColor)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.setDecorFitsSystemWindows(false)
                    val controller = window.insetsController
                    controller?.hide(WindowInsets.Type.navigationBars())

                    // Ensure content adjusts with system insets
                    findViewById<View>(android.R.id.content).setOnApplyWindowInsetsListener { v, insets ->
                        val navBarInsets = insets.getInsets(WindowInsets.Type.navigationBars())
                        v.setPadding(0, 0, 0, navBarInsets.bottom) // Apply correct bottom padding
                        insets
                    }
                } else {
                    window.decorView.systemUiVisibility =
                        (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                }*/
    }
}