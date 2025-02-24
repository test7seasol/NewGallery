package com.gallery.photos.editpic.Activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.BuildConfig
import com.gallery.photos.editpic.Extensions.PIN_LOCK
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.shareUs
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {
    lateinit var bind: ActivitySettingsBinding

    var luancher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultString = data?.getStringExtra("key") // Get returned data
                bind.switchLock.isChecked = resultString == "true"
                Log.d("TAG", "Result: $resultString")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.apply {

            bind.switchLock.isChecked = MyApplicationClass.getString(PIN_LOCK)?.isNotEmpty() == true

            ivBack.onClick { finish() }
            layoutPrivacyPolicy.onClick { startActivityWithBundle<PrivacyPolicyAct>() }
            layoutShareApp.onClick {
                shareUs(this@SettingsActivity)
            }

            llFeedBack.onClick {
                sendFeedbackEmail("fenillathiya.7seasol@gmail.com")
            }

            llLanguage.onClick {
                startActivityWithBundle<LanguageAct>()
            }
            llHidepassowrd.onClick {
                luancher.launch(
                    Intent(this@SettingsActivity, PatternAct::class.java)
                        .putExtra(
                            "isFromOn",
                            (MyApplicationClass.getString(PIN_LOCK)?.isNotEmpty() == true)
                        )
                )
                overridePendingTransition(0, 0);  // Disables the transition effect
            }
        }
    }

    fun sendFeedbackEmail(email: String) {
        val deviceInfo = ("""Device Model: ${Build.MODEL}
Manufacturer: ${Build.MANUFACTURER}
Android Version: ${Build.VERSION.RELEASE}
SDK Version: ${Build.VERSION.SDK_INT}
App Version: ${BuildConfig.VERSION_NAME}""".toString() + " (" + BuildConfig.VERSION_CODE).toString() + ")"

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.setType("message/rfc822")
        emailIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(email)
        ) // Replace with your email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback - " + Build.MODEL)
        emailIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Please describe your issue below:\n\n$deviceInfo\n\n"
        )

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No email app found!", Toast.LENGTH_SHORT).show()
        }
    }
}