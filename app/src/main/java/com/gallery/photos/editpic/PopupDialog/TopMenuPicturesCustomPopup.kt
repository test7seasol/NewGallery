package com.gallery.photos.editpic.PopupDialog

import android.app.Activity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.databinding.ToptoolFavouritepopupdialogBinding
import com.gallery.photos.editpic.databinding.ToptoolPicturestpopupdialogBinding
import com.gallery.photos.editpic.databinding.ToptoolRecyclepopupdialogBinding
import com.labo.kaji.relativepopupwindow.RelativePopupWindow

class TopMenuPicturesCustomPopup(
    private val activity: Activity, var onClick: (String) -> Unit
) {
    private var popupWindow: RelativePopupWindow? = null
    var binding: ToptoolPicturestpopupdialogBinding =
        ToptoolPicturestpopupdialogBinding.inflate(LayoutInflater.from(activity))

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
            RelativePopupWindow.VerticalPosition.BELOW,
            RelativePopupWindow.HorizontalPosition.RIGHT,
            xOffsetDp,
            yOffsetDp,
            true
        )

        binding.apply {

            tvChangeViewType.beGone()
            tvGroupBy.beGone()

          /*  if (isFromHide) {
                tvHiddenTo.beGone()
            } else {
                tvHiddenTo.beVisible()
            }
*/
            tvGroupBy.onClick {
                onClick.invoke("groupby")
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

        }
    }

    fun dismiss() {
        popupWindow?.dismiss()
    }
}
