package com.gallery.photos.editpic.Extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.gallery.photos.editpic.Activity.MyApplicationClass
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.RoomDB.MediaDatabase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


val DATE_LIMIT = 1000
var FOLDER_MAKER_NAME = ""

fun isVideoFile(filePath: String): Boolean {
    val videoExtensions = listOf("mp4", "mkv", "avi", "mov", "wmv", "flv", "3gp", "webm")
    return videoExtensions.any { filePath.endsWith(".$it", ignoreCase = true) }
}


fun setLanguageCode(activity: Activity, languageCode: String) {
    MyApplicationClass.putString(PREF_LANGUAGE_CODE, languageCode)
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val configuration = Configuration()
    configuration.setLocale(locale)
    activity.resources.updateConfiguration(configuration, activity.resources.displayMetrics)
}


fun notifyGalleryRoot(activity: Activity, filePath: String) {
    MediaScannerConnection.scanFile(
        activity, arrayOf(filePath), null
    ) { path, uri ->
        Log.d("GalleryUpdate", "File $path was scanned successfully: $uri")
    }
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())  // Format: day-month-year
    return format.format(date)
}

fun shareFile(context: Context, filePath: String) {
    val file = File(filePath)
    if (!file.exists()) {
        // Show a toast or log that the file doesn't exist
        return
    }

    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",  // Replace with your file provider authority
        file
    )

    val mimeType = when {
        filePath.endsWith(".mp4", true) || filePath.endsWith(".mkv", true) -> "video/*"
        filePath.endsWith(".jpg", true) || filePath.endsWith(
            ".jpeg",
            true
        ) || filePath.endsWith(".png", true) -> "image/*"

        else -> "*/*"  // Fallback for other file types
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share File via"))
}

fun shareMultipleFiles(filePaths: List<MediaModel>, context: Context) {
    val fileUris = ArrayList<Uri>()

    filePaths.forEach { path ->
        val file = File(path.mediaPath)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            fileUris.add(uri)
        }
    }

    if (fileUris.isNotEmpty()) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "*/*"  // Use "image/*" for only images or "video/*" for only videos
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Files"))
    } else {
        Log.e("ShareFiles", "No valid files to share.")
    }
}

fun shareMultipleFilesFavourite(filePaths: List<FavouriteMediaModel>, context: Context) {
    val fileUris = ArrayList<Uri>()

    filePaths.forEach { path ->
        val file = File(path.mediaPath)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            fileUris.add(uri)
        }
    }

    if (fileUris.isNotEmpty()) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "*/*"  // Use "image/*" for only images or "video/*" for only videos
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Files"))
    } else {
        Log.e("ShareFiles", "No valid files to share.")
    }
}

fun shareMultipleFilesHide(filePaths: List<HideMediaModel>, context: Context) {
    val fileUris = ArrayList<Uri>()

    filePaths.forEach { path ->
        val file = File(path.mediaPath)
        if (file.exists()) {
            val uri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            fileUris.add(uri)
        }
    }

    if (fileUris.isNotEmpty()) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "*/*"  // Use "image/*" for only images or "video/*" for only videos
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Files"))
    } else {
        Log.e("ShareFiles", "No valid files to share.")
    }
}

fun shareMultipleFilesVideo(filePaths: List<VideoModel>, context: Context) {
    val fileUris = ArrayList<Uri>()

    filePaths.forEach { path ->
        val file = File(path.videoPath)
        if (file.exists()) {
            val uri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            fileUris.add(uri)
        }
    }

    if (fileUris.isNotEmpty()) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "*/*"  // Use "image/*" for only images or "video/*" for only videos
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Files"))
    } else {
        Log.e("ShareFiles", "No valid files to share.")
    }
}


object name {
    fun getMediaDatabase(activity: Activity): MediaDatabase {
        return MediaDatabase.getDatabase(activity)
    }
}

