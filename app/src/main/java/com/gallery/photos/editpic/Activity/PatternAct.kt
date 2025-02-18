package com.gallery.photos.editpic.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.gallery.photos.editpic.Dialogs.SecurityDialog
import com.gallery.photos.editpic.Extensions.PIN_LOCK
import com.gallery.photos.editpic.Extensions.SECURITY_ADD
import com.gallery.photos.editpic.Extensions.delayTime
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.databinding.ActivityPatternBinding

class PatternAct : AppCompatActivity() {
    lateinit var bind: ActivityPatternBinding

    var patternText = ""
    var isFromOn = false
    var isFromHide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPatternBinding.inflate(layoutInflater)
        setContentView(bind.root)
        applyStatusBarColor()

        isFromOn = intent.extras?.getBoolean("isFromOn", false) == true
        isFromHide = intent.extras?.getBoolean("isFromHide", false) == true


        if (MyApplicationClass.getBoolean(SECURITY_ADD) == false) {
            SecurityDialog(this@PatternAct) { q, answer ->

            }
        }

        bind.apply {


            bind.tvReseat.onClick {

                SecurityDialog(this@PatternAct, true) { q, answer ->
                    if (q == "SetPsd") {
                        tvPattern.text = "Enter Your New Pattern"
                        patternText = ""
                        isFromHide = false
                        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                        mPatternLockView.clearPattern()
                    }
                }
            }

            ivBack.onClick { finish() }

            val content = SpannableString("Reseat Password")
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            bind.tvReseat.text = content

            val mPatternLockViewListener: PatternLockViewListener =
                object : PatternLockViewListener {
                    override fun onStarted() {
                        Log.d(javaClass.name, "Pattern drawing started")
                    }

                    override fun onProgress(progressPattern: List<PatternLockView.Dot?>?) {
                        Log.d(
                            javaClass.name, "Pattern progress: " + PatternLockUtils.patternToString(
                                mPatternLockView, progressPattern
                            )
                        )
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onComplete(pattern: List<PatternLockView.Dot?>?) {
                        val lockPin = PatternLockUtils.patternToString(
                            mPatternLockView, pattern
                        )

                        if (isFromHide) {
                            (MyApplicationClass.getString(PIN_LOCK) + " | " + lockPin).log()
                            if (MyApplicationClass.getString(PIN_LOCK) == lockPin) {
                                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                                delayTime(800) {
                                    mPatternLockView.clearPattern()
                                }
                                setResult(
                                    Activity.RESULT_OK,
                                    Intent().putExtra("isFromHide", "true")
                                )
                                this@PatternAct.finish()
                            } else {
                                ("Wrong Patter").tos(this@PatternAct)
                                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                                delayTime(800) {
                                    mPatternLockView.clearPattern()
                                }
                            }
                        } else {
                            if (!isFromOn) {
                                if (patternText.isEmpty()) {
                                    patternText = lockPin
                                    tvPattern.text = "ReEnter the pattern"
                                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                                    delayTime(800) {
                                        mPatternLockView.clearPattern()
                                    }
                                } else {
                                    if (patternText == lockPin) {
                                        tvPattern.text = "Pattern set successfully"
                                        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                                        MyApplicationClass.putString(PIN_LOCK, lockPin)
                                        delayTime(800) {
                                            mPatternLockView.clearPattern()
                                        }
                                        setResult(
                                            Activity.RESULT_OK,
                                            Intent().putExtra("key", "true")
                                        )
                                        this@PatternAct.finish()
                                    } else {
                                        ("Wrong Patter").tos(this@PatternAct)
                                        tvPattern.text = "Enter Your Pattern"
                                        patternText = ""
                                        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                                        delayTime(800) {
                                            mPatternLockView.clearPattern()
                                        }
                                    }
                                }
                            } else {
                                if (MyApplicationClass.getString(PIN_LOCK) == lockPin) {
                                    tvPattern.text = "Pattern unlock successfully"
                                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                                    MyApplicationClass.putString(PIN_LOCK, "")
                                    delayTime(800) {
                                        mPatternLockView.clearPattern()
                                    }
                                    setResult(Activity.RESULT_OK, Intent().putExtra("key", "false"))
                                    this@PatternAct.finish()
                                } else {
                                    ("Wrong Patter").tos(this@PatternAct)
                                    tvPattern.text = "Enter Your Pattern"
                                    patternText = ""
                                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                                    delayTime(800) {
                                        mPatternLockView.clearPattern()
                                    }
                                }
                            }
                        }
                        Log.d(
                            javaClass.name, lockPin
                        )
                    }

                    override fun onCleared() {
                        Log.d(javaClass.name, "Pattern has been cleared")
                    }
                }

            mPatternLockView.correctStateColor = Color.GREEN
            mPatternLockView.wrongStateColor = Color.RED
            mPatternLockView.addPatternLockListener(mPatternLockViewListener);
        }
    }

    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.black, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.black, theme)
    }
}