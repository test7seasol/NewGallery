package com.gallery.photos.editpic.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.Keep
import com.gallery.photos.editpic.Adapter.RVLanguageAdapter
import com.gallery.photos.editpic.Extensions.ISONETIME
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ActivityLanguageBinding
import com.gallery.photos.editpic.myadsworld.MyAddPrefs
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import com.gallery.photos.editpic.myadsworld.MyAppOpenManager
import com.gallery.photos.editpic.myadsworld.nativetemplates.TemplateView
import com.google.android.gms.ads.appopen.AppOpenAd

@Keep
data class LanguageModel(
    var src: Int,
    var language_code: String,
    var language: String = "en",
    var sub_language: String = "English",
    var isSelected: Boolean = false
)

class LanguageAct : BaseActivity() {
    var list: ArrayList<LanguageModel> = arrayListOf()

    var selectedLanguage = "en"
    var isFrom = ""
    lateinit var bind: ActivityLanguageBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        bind = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(bind.root)

        isFrom = intent.extras?.getString("From").toString()

        list.add(LanguageModel(R.drawable.flag_en, "en", "English", "English", false))
        list.add(LanguageModel(R.drawable.flag_hi, "hi", "Hindi", "हिन्दी", false))
        list.add(LanguageModel(R.drawable.flag_es, "es", "Spanish", "Español", false))
        list.add(LanguageModel(R.drawable.flag_fr, "fr", "French", "Français", false))
        list.add(LanguageModel(R.drawable.flag_de, "de", "German", "Deutsch", false))
        list.add(LanguageModel(R.drawable.flag_af, "af", "Afrikaans", "Afrikaans", false))
        list.add(LanguageModel(R.drawable.flag_cs, "cs", "West Slavic", "Čeština", false))
        list.add(LanguageModel(R.drawable.flag_da, "da", "Danish", "Dansk", false))
        list.add(LanguageModel(R.drawable.flag_fi, "fi", "Finnish", "Suomi", false))
        list.add(LanguageModel(R.drawable.flag_it, "it", "Italian", "Italiano", false))
        list.add(LanguageModel(R.drawable.flag_ja, "ja", "Japanese", "日本語", false))
        list.add(LanguageModel(R.drawable.flag_ko, "ko", "Korean", "한국어", false))
        list.add(LanguageModel(R.drawable.flag_ms, "ms", "Malay", "Melayu", false))
        list.add(LanguageModel(R.drawable.flag_nl, "nl", "Netherlands", "Nederlands", false))
        list.add(LanguageModel(R.drawable.flag_pt, "pt", "Portuguese", "Português", false))
        list.add(LanguageModel(R.drawable.flag_tr, "tr", "Turkish", "Türkçe", false))
        list.add(LanguageModel(R.drawable.flag_uk, "uk", "Ukrainian", "Українська", false))
        list.add(LanguageModel(R.drawable.flag_vi, "vi", "Vietnamese", "Tiếng Việt", false))

        MyAllAdCommonClass.showNativeAdsId(
            this,
            findViewById<View>(R.id.my_template2) as TemplateView,
            bind.shimmerViewContainer.root,
            MyAddPrefs(this).admNativeId
        )

        bind.apply {
            bind.donebtnid.gone()

            rvlanguageid.adapter = RVLanguageAdapter(this@LanguageAct, list) {
                MyAppOpenManager.appOpenAd = null

                selectedLanguage = it.language_code
                setLanguageCode(this@LanguageAct, selectedLanguage)
                MyApplicationClass.putBoolean(ISONETIME, true)

                if (!Settings.canDrawOverlays(this@LanguageAct)) {
                    val intent = Intent(this@LanguageAct, PermissionActivity::class.java)

                    startActivity(intent)
                    finish()
                } else {
                    ("Select Language code: ${it.language}").log()

                    startActivityWithBundle<MainActivity>()
                    finishAffinity()
                }

//                bind.donebtnid.visible()
//                rvlanguageid.adapter?.notifyDataSetChanged()
            }

            tvbackid.onClick {
                finish()
            }

            donebtnid.onClick {
                MyAppOpenManager.appOpenAd = null
                setLanguageCode(this@LanguageAct, selectedLanguage)
                MyApplicationClass.putBoolean(ISONETIME, true)
                if (!Settings.canDrawOverlays(this@LanguageAct)) {
                    val intent = Intent(this@LanguageAct, PermissionActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    ("Select Language code: $").log()
                    startActivityWithBundle<MainActivity>()
                    finishAffinity()
                }

                /*  MyAllAdCommonClass.AdShowdialogFirstActivityQue(
                      this@LanguageAct
                  ) {
                      MyApplicationClass.putBoolean(ISONETIME, true)
                      setLanguageCode(this@LanguageAct, selectedLanguage)
                      ("Select Language code: $").log()
                      startActivityWithBundle<MainActivity>()
                      finishAffinity()
                  }*/
            }
        }
    }
}
