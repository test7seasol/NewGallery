package com.gallery.photos.editpic.Views.extension

import android.view.View
import androidx.core.view.ViewCompat

internal val View.compatPaddingStart get() = ViewCompat.getPaddingStart(this)
