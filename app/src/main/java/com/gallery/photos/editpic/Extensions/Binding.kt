package com.gallery.photos.editpic.Extensions

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

inline fun <T : ViewBinding> Activity.viewBinding(crossinline bindingInflater: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }



fun Activity.delayTime(long: Long, onNext: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        onNext.invoke()
    }, long)
}
