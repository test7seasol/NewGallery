package com.gallery.photos.editpic.PopupDialog

import android.annotation.SuppressLint
import android.app.Activity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.PictureRecentBottomPopupBinding
import com.labo.kaji.relativepopupwindow.RelativePopupWindow

class PicturesBottomPopup(
    private val activity: Activity, private val selectedSize: Boolean, var onClick: (String) -> Unit
) {
    private var popupWindow: RelativePopupWindow? = null
    var binding: PictureRecentBottomPopupBinding =
        PictureRecentBottomPopupBinding.inflate(LayoutInflater.from(activity))

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

            if (selectedSize) {
                tvSelect.text = activity.getString(R.string.select_all)
            } else {
                tvSelect.text = activity.getString(R.string.deselect_all)
            }

            llCopy.onClick {
                onClick.invoke("copytoid")
                popupWindow!!.dismiss()
            }
            llFavourite.onClick {
                onClick.invoke("llFavourite")
                popupWindow!!.dismiss()
            }
            llMove.onClick {
                onClick.invoke("movetoid")
                popupWindow!!.dismiss()
            }
            llAddTohide.onClick {
                onClick.invoke("llAddTohide")
                popupWindow!!.dismiss()
            }
            llSelectAll.onClick {
                if (selectedSize) {
                    onClick.invoke("selectallid")
                } else {
                    onClick.invoke("deselectall")
                }
                popupWindow!!.dismiss()
            }
        }
    }

    fun dismiss() {
        popupWindow?.dismiss()
    }
}
