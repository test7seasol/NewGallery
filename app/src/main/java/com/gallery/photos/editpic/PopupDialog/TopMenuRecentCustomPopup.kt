package com.gallery.photos.editpic.PopupDialog

import android.app.Activity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.databinding.ToptoolHidepopupdialogBinding
import com.gallery.photos.editpic.databinding.ToptoolRecentpopupdialogBinding
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
class TopMenuRecentCustomPopup(
    private val activity: Activity, var onClick: (String) -> Unit
) {
    private var popupWindow: RelativePopupWindow? = null
    var binding: ToptoolRecentpopupdialogBinding =
        ToptoolRecentpopupdialogBinding.inflate(LayoutInflater.from(activity))

    fun show(anchorView: View, xOffsetDp: Int = 0, yOffsetDp: Int = 0) {
        if (popupWindow == null) {
            popupWindow = RelativePopupWindow(
                binding.root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                elevation = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 30f, activity.resources.displayMetrics
                )
                isOutsideTouchable = true  // Dismiss when touched outside
                setBackgroundDrawable(null)  // Required for touch outside to work
            }
        }

        val xOffsetPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, xOffsetDp.toFloat(), activity.resources.displayMetrics
        ).toInt()
        val yOffsetPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, yOffsetDp.toFloat(), activity.resources.displayMetrics
        ).toInt()

        popupWindow?.showOnAnchor(
            anchorView,
            RelativePopupWindow.VerticalPosition.BELOW,
            RelativePopupWindow.HorizontalPosition.RIGHT,
            xOffsetPx,
            yOffsetPx,
            true
        )

        binding.apply {
            tvChangeViewType.beGone()
            tvGroupBy.beGone()

            llEdit.onClick {
                onClick.invoke("llEdit")
                dismiss()
            }
            llSelectAll.onClick {
                onClick.invoke("llSelectAll")
                dismiss()
            }
            llStartSlide.onClick {
                onClick.invoke("llStartSlide")
                dismiss()
            }
            llSort.onClick {
                onClick.invoke("llSort")
                dismiss()
            }
        }
    }

    fun dismiss() {
        popupWindow?.dismiss()
    }
}
