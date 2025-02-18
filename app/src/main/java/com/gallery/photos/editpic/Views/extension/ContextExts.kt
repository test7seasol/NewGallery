package com.gallery.photos.editpic.Views.extension

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

internal fun Context.getCompatDrawable(@DrawableRes drawableId: Int) = ContextCompat.getDrawable(this, drawableId)
