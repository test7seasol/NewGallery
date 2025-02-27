package com.gallery.photos.editpic.Extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gallery.photos.editpic.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

var isAutoUpdate = true

@JvmField
var PREF_LANGUAGE_CODE = "PREF_LANGUAGE_CODE_L"
var ISONETIME = "isFirstTime"

fun String.html() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(
    this, Html.FROM_HTML_MODE_LEGACY
) else HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)

fun <T> T.tos(activity: Activity) = Toast.makeText(activity, "$this", Toast.LENGTH_SHORT).show()
fun <T> T.tosL(activity: Activity) = Toast.makeText(activity, "$this", Toast.LENGTH_LONG).show()

fun StringopenUri(uri: String, activity: Activity) =
    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))

fun Any.toGson() = Gson().toJson(this)

fun View.gone() {
    visibility = View.GONE
}

val Context.audioManager get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

@SuppressLint("ResourceType")
fun ImageView.loadImg(activity: Context, url: String) {
    Glide.with(activity)
        .load(url)
        .placeholder(ColorDrawable(Color.WHITE)) // Fix placeholder type
        .transition(DrawableTransitionOptions.withCrossFade()) // Add crossfade animation
        .into(this)
}

fun AppCompatActivity.handleBackPress(onBackPressed: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    })
}

fun View.visible() {
    visibility = View.VISIBLE
}

inline fun <reified T> String.fromJSON() = Gson().fromJson<T>(this, object : TypeToken<T>() {}.type)

fun View.invisible() {
    visibility = View.INVISIBLE
}


inline fun <reified T : Activity> Context.startActivityWithBundle(bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java).apply {
        bundle?.let { putExtras(it) }
    }
    startActivity(intent)
}

fun shareUs(activity: Activity) {
    val i = Intent(Intent.ACTION_SEND).putExtra(
        Intent.EXTRA_TEXT,
        "I'm using ${activity.getString(R.string.app_name)}! Get the app for free at http://play.google.com/store/apps/details?id=${activity.packageName}"
    )
    i.type = "text/plain"
    activity.startActivity(Intent.createChooser(i, "Share"))
}

fun rateUs(activity: Activity) {
    try {
        val marketUri = Uri.parse("market://details?bucketId=${activity.packageName}")
        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
        activity.startActivity(marketIntent)
    } catch (e: Exception) {
        val marketUri =
            Uri.parse("https://play.google.com/store/apps/details?id=${activity.packageName}")
        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
        activity.startActivity(marketIntent)
    }
}

fun getTimeAgo(pastTimeMillis: Long): String {
    val currentTimeMillis = System.currentTimeMillis()
    val differenceMillis = currentTimeMillis - pastTimeMillis

    val seconds = differenceMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "$seconds seconds ago"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        else -> "$days days ago"
    }
}

fun hasInternetConnect(activity: Activity): Boolean {
    var isWifiConnected = false
    var isMobileConnected = false
    val cm =
        activity.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (cm.defaultProxy != null) return false

    for (ni in cm.allNetworkInfo) {
        if (ni.typeName.equals("WIFI", ignoreCase = true)) if (ni.isConnected) isWifiConnected =
            true
        if (ni.typeName.equals("MOBILE", ignoreCase = true)) if (ni.isConnected) isMobileConnected =
            true
    }
    return isWifiConnected || isMobileConnected
}

fun isConnected(context: Context): Boolean {
    return try {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        nInfo != null && nInfo.isAvailable && nInfo.isConnected
    } catch (e: Exception) {
        Log.e("Connectivity Exception", e.message!!)
        false
    }
}

fun hideKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun shareText(context: Context, textToShare: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, textToShare)
    }

    // Start the share intent
    val chooser = Intent.createChooser(intent, "Share using")
    context.startActivity(chooser)
}

fun copyTextToClipboard(context: Context, textToCopy: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Copied Text", textToCopy)
    clipboardManager.setPrimaryClip(clipData)

    // Notify the user
    Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
}

fun Activity.onBackground(onNext: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        onNext.invoke()
    }
}


fun shareImageFromUrl(context: Context, imageUrl: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Download the image as Bitmap
            val bitmap = Glide.with(context).asBitmap().load(imageUrl).submit().get()

            // Save the Bitmap locally
            val file = saveBitmapToFile(context, bitmap)

            // Share the image using FileProvider
            file?.let { shareImage(context, it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun saveBitmapToFile(context: Context, bitmap: Bitmap): File? {
    return try {
        // Create a directory in the cache folder
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs() // Ensure the directory exists

        // Save the image to a file
        val file = File(cachePath, "shared_image.png")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun shareImage(context: Context, file: File) {
    // Get the content URI using FileProvider
    val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    // Create a sharing intent
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission to the receiving app
    }

    // Start the sharing activity
    context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
}


@SuppressLint("NewApi")
fun saveBitmapToDownloads(context: Context, bitmap: Bitmap, fileName: String) {
    val downloadsDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // For Android 10 and above
        MediaStore.Downloads.EXTERNAL_CONTENT_URI
    } else {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }

    try {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png") // Add extension
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri =
            context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) // Save as PNG
                Toast.makeText(context, "Saved to Downloads folder", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save image: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun downloadAndSaveBitmap(context: Context, imageUrl: String, fileName: String) {
    Glide.with(context).asBitmap().load(imageUrl)
        .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
            ) {
                saveBitmapToDownloads(context, resource, fileName)
            }

            override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                // Handle cleanup if needed
            }

            override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
            }
        })
}