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
import android.os.Handler
import android.os.Looper
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

class PermissionActivity : AppCompatActivity() {

    lateinit var binding: ActivityPermissionBinding
    private val handler = Handler(Looper.getMainLooper())

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
                        showPermissionRationaleDialog()
                    } else {
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
                    askOverlayPermission()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE) -> {
                    showPermissionRationaleDialog()
                }
                else -> {
                    openAppSettings()
                }
            }
        } else {
            askOverlayPermission()
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
        val anim = AnimationUtils.loadAnimation(this, R.anim.left_right)
        binding.shine.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) {
                binding.shine.startAnimation(anim)
            }

            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationRepeat(p0: Animation?) {}
        })
    }

    private var mPermissionForResult: ActivityResultLauncher<Intent>? = null

    private val overlayPermissionRunnable = object : Runnable {
        override fun run() {
            ("Run Run ").log()
            if (Settings.canDrawOverlays(this@PermissionActivity)) {
                gonext()
                handler.removeCallbacks(this) // Stop the check
            } else {
                handler.postDelayed(this, 1000) // Check again after 1 second
            }
        }
    }

    private fun askOverlayPermission() {
        MyAllAdCommonClass.isInterOpen = true

        if (Settings.canDrawOverlays(this)) {
            gonext()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                mPermissionForResult?.launch(intent)
                startActivityWithBundle<MyTranslucentActivity>()
            } catch (e: Exception) {
                Log.e("TAG", "askOverlayPermission: ${e.message}")
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Failed to request overlay permission", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            gonext()
        }

        // Start periodic check on the main thread
        handler.post(overlayPermissionRunnable)
    }

    private val launcherOpenSettingPopUP =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Autostart.isAutoStartEnabled(this, true)) {
                openMiAutoStartSettings(this)
            }
        }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Autostart.isAutoStartEnabled(this, true)) {
                openMiAutoStartSettings(this)
            }
        }

    fun openMiAutoStartSettings(context: Context) {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            launcher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(context, "Auto Start settings not found on this device", Toast.LENGTH_SHORT).show()
            }
            gonext()
        }
    }

    private fun openXiaomiSettings() {
        MyAllAdCommonClass.isInterOpen = true

        try {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity"
                )
                putExtra("extra_pkgname", packageName)
            }
            launcherOpenSettingPopUP.launch(intent)
        } catch (e: Exception) {
            val tagName = OverlayUtil.getTagName(this)
            Log.w(tagName, "Error XIAOMI permissions PermissionsEditorActivity: ${e.message}")
            e.printStackTrace()
            try {
                val intent2 = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                    setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                    )
                    putExtra("extra_pkgname", packageName)
                }
                launcherOpenSettingPopUP.launch(intent2)
            } catch (e2: Exception) {
                Log.w(tagName, "Error XIAOMI permissions AppPermissionsEditorActivity: ${e2.message}")
                e2.printStackTrace()
                try {
                    val intent3 = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                        setClassName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.permissions.PermissionAdditionalActivity"
                        )
                        putExtra("extra_pkgname", packageName)
                    }
                    launcherOpenSettingPopUP.launch(intent3)
                } catch (e3: Exception) {
                    Log.w(tagName, "Error XIAOMI permissions PermissionAdditionalActivity: ${e3.message}")
                    e3.printStackTrace()
                    try {
                        val intent4 = Intent("android.settings.APPLICATION_DETAILS_SETTINGS").apply {
                            data = Uri.parse("package:$packageName")
                        }
                        launcherOpenSettingPopUP.launch(intent4)
                    } catch (e4: Exception) {
                        Log.w(tagName, "Error XIAOMI permissions ACTION_APPLICATION_DETAILS_SETTINGS: ${e4.message}")
                        e4.printStackTrace()
                    }
                }
            }
        }

        handler.postDelayed({
            startActivity(
                Intent(this@PermissionActivity, XiomiGuideActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    .putExtra("autostart", getString(R.string.allow_overlay_access))
            )
        }, 100L)
    }

    override fun onResume() {
        super.onResume()
        if (OverlayUtil.isManufacturerXiaomi()) {
            if (OverlayUtil.isBackgroundStartActivityPermissionGranted(this) && Autostart.isAutoStartEnabled(this, true)) {
                gonext()
            }
        }
    }

    fun gonext() {
        MyApplicationClass.putBoolean("isSHowCallerID", true)
        MyAppOpenManager.appOpenAd = null

        startActivity(
            Intent(this@PermissionActivity, LanguageAct::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(overlayPermissionRunnable) // Clean up to prevent leaks
    }
}