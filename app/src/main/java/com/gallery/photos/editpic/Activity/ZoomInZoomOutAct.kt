package com.gallery.photos.editpic.Activity

import android.os.Bundle
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
        setContentView(bind.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

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
}
