package com.gallery.photos.editpic.Dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.gallery.photos.editpic.Activity.MyApplicationClass
import com.gallery.photos.editpic.Extensions.ANSWER
import com.gallery.photos.editpic.Extensions.QUESATION
import com.gallery.photos.editpic.Extensions.SECURITY_ADD
import com.gallery.photos.editpic.Extensions.delayTime
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.PopupDialog.TopQuesationsCustomPopup
import com.gallery.photos.editpic.databinding.SecurityQuebottomSheetLayoutBinding


class SecurityDialog(
    private val activity: Activity,
    val isFromReseat: Boolean = false,
    val callback: (String, String) -> Unit
) {

    private var dialog: AlertDialog? = null
    private val binding = SecurityQuebottomSheetLayoutBinding.inflate(activity.layoutInflater)

    init {
        setupDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun setupDialog() {
        // Set the custom message
        // Build and show the dialog
        dialog = AlertDialog.Builder(activity).setView(binding.root) // Set the custom layout
            .create()
        dialog!!.setCancelable(false)

        binding.apply {
            binding.tvSecurity.visibility = if (isFromReseat) View.GONE else View.VISIBLE
            binding.ivReset.visibility = if (isFromReseat) View.GONE else View.VISIBLE
            binding.tvSave.text = if (isFromReseat) "Check" else "Save"

            if (isFromReseat) {
                tvQuestion.text = MyApplicationClass.getString(QUESATION)
                tvCancel.visible()
            } else {
                tvCancel.gone()
            }

            llQuesation.onClick {
                if (!isFromReseat) {
                    val secdialog = TopQuesationsCustomPopup(activity) {
                        tvQuestion.text = it.toString()
                    }
                    secdialog.show(llQuesation, 0, 0)
                }
            }

            tvCancel.onClick {
                dialog!!.dismiss()
            }

            tvSave.onClick {
                val edittext = edAnswer.text.toString().trim()

                if (isFromReseat) {
                    if (edittext == MyApplicationClass.getString(ANSWER).toString().trim()) {
                        callback("SetPsd", "SetPsd")
                        dialog?.dismiss()
                    } else {
                        edAnswer.setTextColor(Color.RED)
                        activity.delayTime(800) {
                            edAnswer.setText("")
                            edAnswer.setTextColor(Color.BLACK)
                        }
                        ("Wrong Answer").tos(activity)
                    }
                } else {

                    if (edittext.isNotEmpty()) {
                        MyApplicationClass.putBoolean(SECURITY_ADD, true)
                        MyApplicationClass.putString(QUESATION, tvQuestion.text.toString().trim())
                        MyApplicationClass.putString(ANSWER, edittext)
                        dialog?.dismiss()
                        callback(tvQuestion.text.toString().trim(), edittext.trim())
                    } else {
                        ("Need to add answer").tos(activity)
                    }
                }
            }

//            tvSkip.onClick {
//                dialog?.dismiss()
//                callback("tvSkip", "tvSkip")
//            }
        }

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> String.format("%d Bytes", bytes)
        }
    }
}