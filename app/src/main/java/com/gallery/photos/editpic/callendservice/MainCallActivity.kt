package com.gallery.photos.editpic.callendservice

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Activity.MyApplicationClass
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.callendservice.adapter.CallerScreenAdapter
import com.gallery.photos.editpic.callendservice.adutils.AdsCachingUtils
import com.gallery.photos.editpic.callendservice.adutils.SetAdListener
import com.gallery.photos.editpic.callendservice.interfaces.OnKeyboardOpenListener
import com.gallery.photos.editpic.callendservice.model.ContactCDO
import com.gallery.photos.editpic.callendservice.utils.AppUtils
import com.gallery.photos.editpic.callendservice.utils.Utils
import com.gallery.photos.editpic.databinding.ActivityMainCallBinding
import com.gallery.photos.editpic.myadsworld.MyAddPrefs
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import com.google.android.gms.ads.LoadAdError
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Calendar

class MainCallActivity : BaseActivity(), OnKeyboardOpenListener {
    private lateinit var binding: ActivityMainCallBinding
    private var contact1: ContactCDO? = null
    private var number: String = ""
    private var contactName: String = ""
    private var contactId: String = ""
    private var time: String = "00:00"
    private var callStatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyApplicationClass.setStatuaryPadding(binding.root)

        loadNativeOrBannerAds()

        setThemeData()
        init()
        UIComponent()

       /* ConstantsKt.setKeyboardVisibilityListener(this) { isOpen ->
            if (MyApplicationClass.isConnected(this)) {
                Log.i("MAIN::KEY", "OPEN :: $isOpen")
                binding.cardAds.visibility = if (isOpen) View.GONE else View.VISIBLE
            }
        }*/

        setSecondPage()
//        applyStatusBarColor()
    }

    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.white, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.white, theme)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setThemeData() {
        try {
            if (Build.VERSION.SDK_INT != 26) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSecondPage() {
        binding.viewPager.setCurrentItem(0, true)
    }

    private fun init() {
        try {
            if (intent != null) {
                val longExtra = intent.getLongExtra("StartTime", 0L)
                this.time = getTimeDiff(longExtra, intent.getLongExtra("EndTime", 0L))
                this.number = java.lang.String.valueOf(intent.getStringExtra("mobile_number"))
                this.callStatus = java.lang.String.valueOf(intent.getStringExtra("CallType"))
            }
        } catch (e2: java.lang.Exception) {
            e2.printStackTrace()
        }
    }

    private fun setAdapterData() {
        val callerScreenAdapter = CallerScreenAdapter(this, number)
        callerScreenAdapter.setContactData(contactName, contactId, contact1)
        binding.viewPager.adapter = callerScreenAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val icons = listOf(
                R.drawable.ic_action_call_m,
                R.drawable.ic_action_msg_m,
                R.drawable.ic_action_notifi_m,
                R.drawable.ic_action_block_m
            )
            tab.setIcon(icons[position])
            tab.icon?.setColorFilter(
                ContextCompat.getColor(this, R.color.commonwhite), PorterDuff.Mode.SRC_IN
            )
        }.attach()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun UIComponent() {
        if (!AppUtils.isEmptyString(this.number)) {
            if (Utils.getContact(this, this.number) == null) {
                binding.txtAppName.text = PhoneNumberUtils.formatNumber(this.number, "IN")
                binding.ImageView.visibility = View.VISIBLE
                binding.txtUserProName.visibility = View.GONE
                binding.callerAvatar.visibility = View.GONE
                binding.itemTvContactFirstLetter.visibility = View.GONE
            } else {
                val contact = Utils.getContact(this, this.number)
                this.contact1 = contact
                this.contactName = contact.nameSuffix
                this.contactId = java.lang.String.valueOf(contact.contactId)
                binding.txtAppName.text = contact.nameSuffix
                binding.ImageView.visibility = View.GONE
                binding.itemTvContactFirstLetter.visibility = View.GONE
                binding.callerAvatar.visibility = View.GONE
                val contactPhotoUri = contact.contactPhotoUri
                var z = true
                if (!(contactPhotoUri == null || contactPhotoUri.isEmpty())) {
                    binding.callerAvatar.visibility = View.VISIBLE
                    binding.txtUserProName.visibility = View.GONE
                    val load = Glide.with(this as FragmentActivity).load(contact.contactPhotoUri)
                    load.into(binding.callerAvatar)
                } else {
                    val contactPhotoThumbUri = contact.contactPhotoThumbUri
                    if (contactPhotoThumbUri != null && contactPhotoThumbUri.length != 0) {
                        z = false
                    }
                    if (!z) {
                        binding.callerAvatar.visibility = View.VISIBLE
                        binding.txtUserProName.visibility = View.GONE
                        val load2 =
                            Glide.with(this as FragmentActivity).load(contact.contactPhotoThumbUri)
                        load2.into(binding.callerAvatar)
                    } else {
                        binding.txtUserProName.visibility = View.VISIBLE
                        binding.callerAvatar.visibility = View.GONE
                        binding.txtUserProName.text = Utils.firstStringer(contact.nameSuffix)
                    }
                }
            }
        }

        setAdapterData()

        binding.txtCalliInfo.text = "" + this.time
        binding.txtCallStatus.text = callStatus
        val time = Calendar.getInstance().time
        binding.txtTime.text = SimpleDateFormat("hh:mm").format(time).toString()
        binding.txtCalliInfo.text = "" + this.time

        binding.imgAppIcon.setOnClickListener {
            try {
                val launchIntentForPackage = packageManager.getLaunchIntentForPackage(
                    packageName
                )
                if (launchIntentForPackage != null) {
                    if (!AppUtils.isAppRunning) {
                        startActivity(launchIntentForPackage)
                        finish()
                    } else {
                        finish()
                    }
                }
            } catch (e: java.lang.Exception) {
            }
        }

        binding.imgCalliCall.setOnClickListener {
            Utils.openDialerPad(this@MainCallActivity, number)
            finishAndRemoveTask()
        }
    }

    private fun loadNativeOrBannerAds() {
//        CDOUtiler.initializeAllAdsConfigs(this);

        try {
            if (AdsCachingUtils.isCDOBannerAvailable()) {
                Log.e(
                    "package:mine FullScreenBannerAds",
                    "loadNativeOrBannerAds: isCDOBannerAvailable showFullScreenBannerAds"
                )
                showFullScreenBannerAds()
                return
            }
            Log.e(
                "FullScreenBannerAds",
                "loadNativeOrBannerAds: else " + AdsCachingUtils.isBannerCDOAdLoadProcessing + ' ' + AdsCachingUtils.isBannerCDOAdLoadFailed
            )
            if (AdsCachingUtils.isBannerCDOAdLoadProcessing && !AdsCachingUtils.isBannerCDOAdLoadFailed) {
                AdsCachingUtils.setAdListenerFullScreenBanner(object : SetAdListener {
                    override fun onAdLoad() {
                        Log.e("FullScreenBannerAds", "loadNativeOrBannerAds: onAdLoad ")
                        showFullScreenBannerAds()
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                        Log.e("FullScreenBannerAds", "loadNativeOrBannerAds: onAdFailedToLoad ")
                        if (AdsCachingUtils.isBannerCDOAdShow) {
                            return
                        }
                        loadAndShowSecondBannerAds()
                    }

                    override fun onAdImpression() {
                        Log.e("FullScreenBannerAds", "loadNativeOrBannerAds: onAdImpression ")
                    }
                })
                return
            }
            Log.e(
                "FullScreenBannerAds",
                "loadNativeOrBannerAds:  else else " + AdsCachingUtils.isBannerCDOAdLoadProcessing + ' ' + AdsCachingUtils.isBannerCDOAdLoadFailed
            )
            loadAndShowSecondBannerAds()
        } catch (e2: java.lang.Exception) {
            e2.printStackTrace()
        }

        /*  try {
            if (MyAllAdCommonClass.AM_SHOW_CALLENDNative && MyAllAdCommonClass.AM_SHOW_CALLEND_INLINE)
            {
                if (MyApplication.Companion.getStoreBooleanValue("isNativeShow"))
                {
                    MyApplication.Companion.setStoreBooleanValue("isNativeShow", false);
                    loadNative();
                }
                else {
                    MyApplication.Companion.setStoreBooleanValue("isNativeShow", true);
                    loadInlineBanner();
                }

            } else if (MyAllAdCommonClass.AM_SHOW_CALLENDNative)
            {
              loadNative();
            }
            else if (MyAllAdCommonClass.AM_SHOW_CALLEND_INLINE){
                loadInlineBanner();
            }
            else {
                binding.cardBottom.setVisibility(View.INVISIBLE);
                binding.shimmerViewContainer.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e2) {
            Log.d("TAG", "loadNativeOrBannerAd--catch---" + e2.getMessage());
            e2.printStackTrace();
        }*/
    }

    fun loadAndShowSecondBannerAds() {
        AdsCachingUtils.loadAndShowLargeBannerAdsWithRequest(
            this, MyAddPrefs(this).admInlineBannerId, binding.loutFullBannerAd,
            binding.viewForSpaceFull, binding.adContainerFullBanner
        )
    }

    fun showFullScreenBannerAds() {
        binding.viewForSpaceFull.setVisibility(View.GONE)
        binding.adContainerFullBanner.setVisibility(View.VISIBLE)
        if (AdsCachingUtils.mBannerCDOAd.getParent() != null) {
            val parent: ViewParent = AdsCachingUtils.mBannerCDOAd.getParent()
            (parent as ViewGroup).removeView(AdsCachingUtils.mBannerCDOAd)
        }
        binding.adContainerFullBanner.removeAllViews()
        binding.adContainerFullBanner.addView(AdsCachingUtils.mBannerCDOAd)
        AdsCachingUtils.isBannerCDOAdShow = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getTimeDiff(start: Long, end: Long): String {
        val diff = end - start
        val seconds = (diff / 1000) % 60
        val minutes = (diff / 60000) % 60
        return "${AppUtils.addExtraZero(minutes)}:${AppUtils.addExtraZero(seconds)}"
    }

    override fun onDestroy() {
        Log.i("FullScreenBannerAds", "onDestroy: ${MyAllAdCommonClass.isnativeload}")
        MyAllAdCommonClass.loadednative = null
        MyAllAdCommonClass.isnativeload = true
        super.onDestroy()
    }

    override fun onKeyBoardIsOpen(isOpen: Boolean) {
        Log.i("MAIN::KEY", "OPEN :: $isOpen")
//        binding.cardAds.visibility = if (isOpen) View.GONE else View.VISIBLE
    }
}
