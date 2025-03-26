package com.gallery.photos.editpic.Activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.databinding.ZoomlayoutLayoutBinding

class ZoomInZoomOutAct : AppCompatActivity() {
    lateinit var bind: ZoomlayoutLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ZoomlayoutLayoutBinding.inflate(layoutInflater)
        setContentView(bind.root) // Set content view first

        // Apply full-screen mode after content view is set
        setFullScreen()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MyApplicationClass.putBoolean("isAOne", true)
                finish()
            }
        })

        bind.apply {
            tvGotIt.onClick {
                MyApplicationClass.putBoolean("isAOne", true)
                finish()
            }
        }
    }

    private fun setFullScreen() {
        when {
            // Android 8.0 (API 26) and 8.1 (API 27) - Special handling if needed
            Build.VERSION.SDK_INT == Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1 -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            // Android 11+ (API 30+) - Use WindowInsetsController for modern full-screen
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.let {
                    it.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                    it.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
                window.statusBarColor = getColor(android.R.color.transparent)
                window.navigationBarColor = getColor(android.R.color.transparent)
            }
            // Android 5.0 (API 21) to 10 (API 29) - Use flags for older full-screen
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
                window.statusBarColor = getColor(android.R.color.transparent)
                window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
            // Pre-Lollipop (API < 21) - Basic full-screen
            else -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }
    }
}