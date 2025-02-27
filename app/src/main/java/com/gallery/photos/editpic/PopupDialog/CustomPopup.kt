package com.gallery.photos.editpic.PopupDialog

import android.annotation.SuppressLint
import android.app.Activity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.databinding.RecentMenuPopupBinding
import com.labo.kaji.relativepopupwindow.RelativePopupWindow

class CustomPopup(
    private val activity: Activity,
    private val isTouchableOutside: Boolean = true, // Whether the popup can be dismissed by touching outside
    private val isFocusable: Boolean = true,
    var onClick: (String) -> Unit
) {
    private var popupWindow: RelativePopupWindow? = null
    var binding: RecentMenuPopupBinding =
        RecentMenuPopupBinding.inflate(LayoutInflater.from(activity))

    @SuppressLint("SetTextI18n")
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

//        popupWindow?.showAsDropDown(anchorView, xOffsetPx, yOffsetPx)

        popupWindow?.showOnAnchor(
            anchorView,
            RelativePopupWindow.VerticalPosition.ABOVE,
            RelativePopupWindow.HorizontalPosition.RIGHT,
            xOffsetDp,
            yOffsetDp,
            true
        )

        binding.apply {

            copytoid.onClick {
                onClick.invoke("copytoid")
                popupWindow!!.dismiss()
            }
            movetoid.onClick {
                onClick.invoke("movetoid")
                popupWindow!!.dismiss()
            }
            selectallid.onClick {
                onClick.invoke("selectallid")
                popupWindow!!.dismiss()
            }
        }
    }

    fun dismiss() {
        popupWindow?.dismiss()
    }
}
