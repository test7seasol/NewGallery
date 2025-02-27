package com.gallery.photos.editpic.callendservice

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Activity.MyApplicationClass
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.callendservice.adapter.CallerScreenAdapter
import com.gallery.photos.editpic.callendservice.interfaces.OnKeyboardOpenListener
import com.gallery.photos.editpic.callendservice.model.ContactCDO
import com.gallery.photos.editpic.callendservice.utils.AppUtils
import com.gallery.photos.editpic.callendservice.utils.CDOUtiler
import com.gallery.photos.editpic.callendservice.utils.ConstantsKt
import com.gallery.photos.editpic.callendservice.utils.Utils
import com.gallery.photos.editpic.databinding.ActivityMainCallBinding
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import com.gallery.photos.editpic.myadsworld.loadBanner
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
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

        loadBanner(
            binding.bannerShimmer, AdView(this), binding.framBanner, this, AdSize.MEDIUM_RECTANGLE
        )
        setThemeData()
        init()
        UIComponent()

        ConstantsKt.setKeyboardVisibilityListener(this) { isOpen ->
            if (MyApplicationClass.isConnected(this)) {
                Log.i("MAIN::KEY", "OPEN :: $isOpen")
                binding.cardAds.visibility = if (isOpen) View.GONE else View.VISIBLE
            }
        }

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
            intent?.let {
                val startTime = it.getLongExtra("StartTime", 0L)
                val endTime = it.getLongExtra("EndTime", 0L)
                time = getTimeDiff(startTime, endTime)
                number = it.getStringExtra("mobile_number").orEmpty()
                callStatus = it.getStringExtra("CallType").orEmpty()
                Log.d("TAG", "initnumber: $number")
                Log.d("TAG-Bundle", it.extras.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        if (!AppUtils.isEmptyString(number)) {
            val contact = Utils.getContact(this, number)
            if (contact == null) {
                binding.txtAppName.text = PhoneNumberUtils.formatNumber(number, "IN")
                binding.ImageView.visibility = View.VISIBLE
                binding.txtUserProName.visibility = View.GONE
                binding.callerAvatar.visibility = View.GONE
                binding.itemTvContactFirstLetter.visibility = View.GONE
            } else {
                contact1 = contact
                contactName = contact.nameSuffix
                contactId = contact.contactId.toString()
                binding.txtAppName.text = contact.nameSuffix
                binding.ImageView.visibility = View.GONE
                binding.itemTvContactFirstLetter.visibility = View.GONE
                binding.callerAvatar.visibility = View.GONE

                val contactPhotoUri = contact.contactPhotoUri
                if (!contactPhotoUri.isNullOrEmpty()) {
                    binding.callerAvatar.visibility = View.VISIBLE
                    binding.txtUserProName.visibility = View.GONE
                    Glide.with(this).load(contactPhotoUri).into(binding.callerAvatar)
                } else {
                    binding.txtUserProName.visibility = View.VISIBLE
                    binding.callerAvatar.visibility = View.GONE
                    binding.txtUserProName.text = Utils.firstStringer(contact.nameSuffix)
                }
            }
        }

        setAdapterData()

        binding.txtCalliInfo.text = time
        binding.txtCallStatus.text = callStatus
        binding.txtTime.text = SimpleDateFormat("hh:mm").format(Calendar.getInstance().time)

        binding.imgAppIcon.setOnClickListener {
            packageManager.getLaunchIntentForPackage(packageName)?.let {
                if (!AppUtils.isAppRunning) {
                    startActivity(it)
                    finish()
                } else {
                    finish()
                    CDOUtiler.isClickAppIcon = true
                }
            }
        }

        binding.imgCalliCall.setOnClickListener {
            Utils.openDialerPad(this, number)
            finishAndRemoveTask()
        }
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
        binding.cardAds.visibility = if (isOpen) View.GONE else View.VISIBLE
    }
}
