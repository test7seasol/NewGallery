package com.gallery.photos.editpic.Activity

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.callendservice.overlayscreen.Autostart
import com.gallery.photos.editpic.callendservice.overlayscreen.OverlayUtil
import com.gallery.photos.editpic.callendservice.overlayscreen.XiomiGuideActivity
import com.gallery.photos.editpic.databinding.ActivityPermissionBinding
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import com.gallery.photos.editpic.myadsworld.MyAppOpenManager
import java.util.Timer
import java.util.TimerTask

class PermissionActivity : AppCompatActivity() {

    lateinit var binding: ActivityPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MyApplicationClass.setStatuaryPadding(binding.root)

        shineAnimation()

        mPermissionForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // Handle the result of the activity here
            }

        readPhoneStatePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    askOverlayPermission()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                        // Show rationale and ask again
                        showPermissionRationaleDialog()
                    } else {
                        // Permission denied twice, open settings
                        openAppSettings()
                    }
                }
            }

        binding.btnGrant.setOnClickListener {
            readPhoneStatePermissionLauncher?.launch(Manifest.permission.READ_PHONE_STATE)
        }

    }

    private var readPhoneStatePermissionLauncher: ActivityResultLauncher<String>? = null

    private fun checkReadPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED -> {
                    askOverlayPermission()  // If permission is granted, proceed
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE) -> {
                    // Show rationale dialog before requesting again
                    showPermissionRationaleDialog()
                }
                else -> {
                    // If permission is permanently denied (denied twice), open App Settings
                    openAppSettings()
                }
            }
        } else {
            askOverlayPermission()  // No need to ask on lower Android versions
        }
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Toast.makeText(this, "Enable permission from Settings", Toast.LENGTH_LONG).show()
    }


    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This permission is required to detect calls. Please grant it.")
            .setPositiveButton("Try Again") { _, _ ->
                readPhoneStatePermissionLauncher?.launch(Manifest.permission.READ_PHONE_STATE)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun shineAnimation() {
        // attach the animation layout Using AnimationUtils.loadAnimation
        val anim = AnimationUtils.loadAnimation(this, R.anim.left_right)
        binding.shine.startAnimation(anim)
        // override three function There will error
        // line below the object
        // click on it and override three functions
        anim.setAnimationListener(object : Animation.AnimationListener {
            // This function starts the
            // animation again after it ends
            override fun onAnimationEnd(p0: Animation?) {
                binding.shine.startAnimation(anim)
            }

            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationRepeat(p0: Animation?) {}

        })
    }

    private var mPermissionForResult: ActivityResultLauncher<Intent>? = null

    private var overlayPermissionTimer: Timer? = null

    override fun onPause() {
        super.onPause()
    }


    private fun askOverlayPermission() {
        MyAllAdCommonClass.isInterOpen = true

        // Check if the app already has the overlay permission
        if (Settings.canDrawOverlays(this)) {
            gonext()
            return
        }

        // Handle overlay permission based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 (API 29) and above
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    mPermissionForResult?.launch(intent)
                } catch (e: Exception) {
                    Log.e("TAG", "askOverlayPermission: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                // Android 6.0 (API 23) to Android 9 (API 28)
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    mPermissionForResult?.launch(intent)
                } catch (e: Exception) {
                    Log.e("TAG", "askOverlayPermission: ${e.message}")
                    e.printStackTrace()
                }
            }
            startActivityWithBundle<MyTranslucentActivity>()
        } else {
            // Android 5.1 (API 22) and below - No need to request overlay permission
            gonext()
        }

        // Start a TimerTask to periodically check if the permission is granted
        overlayPermissionTimer = Timer()
        overlayPermissionTimer?.schedule(object : TimerTask() {
            override fun run() {
                ("Run Run ").log()
                if (Settings.canDrawOverlays(this@PermissionActivity)) {
                    // Permission granted, navigate to the next screen
                    runOnUiThread {
                        gonext()
                    }
                    overlayPermissionTimer?.cancel() // Stop the timer
                }
            }
        }, 0, 1000) // Check every 1 second
    }

    private val launcherOpenSettingPopUP =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Autostart.isAutoStartEnabled(this, true)) {
                openMiAutoStartSettings(context = this)
            }
        }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Autostart.isAutoStartEnabled(this, true)) {
                openMiAutoStartSettings(context = this)
            }

        }

    fun openMiAutoStartSettings(context: Context) {
        try {
            val intent = Intent()
            intent.component = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
            launcher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context, "Auto Start settings not found on this device", Toast.LENGTH_SHORT
            ).show()
            gonext()
        }
    }

    private fun openXiaomiSettings() {
        MyAllAdCommonClass.isInterOpen = true

//        MyAllAdCommonClass.isAppOpenshowornot = true
        try {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            intent.putExtra("extra_pkgname", packageName)
            launcherOpenSettingPopUP.launch(intent)
        } catch (e: java.lang.Exception) {
            val tagName: String = OverlayUtil.getTagName(this)
            val sb =
                StringBuilder("Error XIAOMI permissions com.miui.security center PermissionsEditorActivity: ")
            e.printStackTrace()
            Log.w(tagName, sb.append(Unit).toString())
            try {
                val intent2 = Intent("miui.intent.action.APP_PERM_EDITOR")
                intent2.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                )
                intent2.putExtra("extra_pkgname", packageName)
                launcherOpenSettingPopUP.launch(intent2)
            } catch (e2: java.lang.Exception) {
                val tagName2: String = OverlayUtil.getTagName(this)
                val sb2 =
                    StringBuilder("Error XIAOMI permissions com.miui.security center AppPermissionsEditorActivity: ")
                e2.printStackTrace()
                Log.w(tagName2, sb2.append(Unit).toString())
                try {
                    val intent3 = Intent("miui.intent.action.APP_PERM_EDITOR")
                    intent3.setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionAdditionalActivity"
                    )
                    intent3.putExtra("extra_pkgname", packageName)
                    launcherOpenSettingPopUP.launch(intent3)
                } catch (e3: java.lang.Exception) {
                    val tagName3: String = OverlayUtil.getTagName(this)
                    val sb3 =
                        StringBuilder("Error XIAOMI permissions com.miui.security center PermissionAdditionalActivity: ")
                    e3.printStackTrace()
                    Log.w(tagName3, sb3.append(Unit).toString())
                    try {
                        val intent4 = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
                        intent4.setData(Uri.parse("package:" + packageName))
                        launcherOpenSettingPopUP.launch(intent4)
                    } catch (e4: java.lang.Exception) {
                        val tagName4: String = OverlayUtil.getTagName(this)
                        val sb4 =
                            StringBuilder("Error XIAOMI permissions ACTION_APPLICATION_DETAILS_SETTINGS: ")
                        e4.printStackTrace()
                        Integer.valueOf(Log.w(tagName4, sb4.append(Unit).toString()))
                    }
                }
            }
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(
                    Intent(
                        this@PermissionActivity,
                        XiomiGuideActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .putExtra("autostart", getString(R.string.allow_overlay_access))
                )
            }
        }, 100L)
    }

    override fun onResume() {
        super.onResume()

        if (OverlayUtil.isManufacturerXiaomi()) {
            if (OverlayUtil.isBackgroundStartActivityPermissionGranted(this) && Autostart.isAutoStartEnabled(
                    this, true
                )
            ) {
                gonext()
            }
        }
    }

    fun gonext() {
        MyApplicationClass.putBoolean("isSHowCallerID", true)
        MyAppOpenManager.appOpenAd = null

        startActivity(
            Intent(
                this@PermissionActivity, LanguageAct::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })

    }
}