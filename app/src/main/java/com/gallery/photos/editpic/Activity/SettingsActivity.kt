package com.gallery.photos.editpic.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Extensions.PIN_LOCK
import com.gallery.photos.editpic.Extensions.onClick
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
        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.apply {

            bind.switchLock.isChecked = MyApplicationClass.getString(PIN_LOCK)?.isNotEmpty() == true

            ivBack.onClick { finish() }
            layoutPrivacyPolicy.onClick { startActivityWithBundle<PrivacyPolicyAct>() }
            layoutShareApp.onClick {
                shareUs(this@SettingsActivity)
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
            }
        }
    }
}