package com.gallery.photos.editpic.Extensions

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContextCompat
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

var PIN_LOCK = "PIN_LOCK"
var SECURITY_ADD = "SECURITY_ADD"
var QUESATION = "QUESATION"
var ANSWER = "ANSWER"
var isDialogShowing = false


fun isVideoFile(filePath: String): Boolean {
    val videoExtensions = listOf("mp4", "mkv", "avi", "mov", "wmv", "flv", "3gp", "webm")
    return videoExtensions.any { filePath.endsWith(".$it", ignoreCase = true) }
}

fun hasMediaPermissions(activity: Activity): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
        ContextCompat.checkSelfPermission(
            activity, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        ) == PackageManager.PERMISSION_GRANTED
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
        ContextCompat.checkSelfPermission(
            activity, Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity, Manifest.permission.READ_MEDIA_VIDEO
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            activity, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun hasAllFilesAccessAs(activity: Activity): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        ContextCompat.checkSelfPermission(
            activity, READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}


fun getMimeTypeFromPath(filePath: String): String? {
    val file = File(filePath)
    val extension = file.extension.lowercase()

    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
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

fun getUriFromFilePath(context: Context, filePath: String?): Uri? {
    if (filePath.isNullOrEmpty()) {
        Log.e("getUriFromFilePath", "❌ filePath is NULL or EMPTY")
        return null
    }

    val file = File(filePath)

    if (!file.exists()) {
        Log.e("getUriFromFilePath", "❌ File does NOT exist: $filePath")
        return null
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getMediaStoreUri(context, filePath)
    } else {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }
}


fun getMediaUri(context: Context, fileName: String): Uri? {
    val projection = arrayOf(MediaStore.Files.FileColumns._ID)
    val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ?"
    val selectionArgs = arrayOf(fileName)

    context.contentResolver.query(
        MediaStore.Files.getContentUri("external"),
        projection, selection, selectionArgs, null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(0)
            return ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
        }
    }

    Log.e("MediaStore", "❌ File not found in MediaStore: $fileName")
    return null
}

fun shareFile(context: Context, fileIdentifier: String) {
    // Determine if the input is already a content URI or a file path
    val fileUri = if (fileIdentifier.startsWith("content://")) {
        // It's already a content URI
        Uri.parse(fileIdentifier)
    } else {
        // It's a file path - try to get MediaStore URI or fallback to FileProvider
        getMediaStoreUri(context, fileIdentifier) ?: getFileProviderUri(context, fileIdentifier)
    }

    if (fileUri == null) {
        Log.e("ShareFile", "❌ Could not get URI for file: $fileIdentifier")
        Toast.makeText(context, "Could not share file", Toast.LENGTH_SHORT).show()
        return
    }

    // Check if file exists and is accessible
    if (!isFileAccessible(context, fileUri)) {
        Log.e("ShareFile", "❌ File not accessible: $fileUri")
        Toast.makeText(context, "File not found or inaccessible", Toast.LENGTH_SHORT).show()
        return
    }

    // Get MIME type
    val mimeType = context.contentResolver.getType(fileUri) ?: "*/*"

    // Create share intent
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // Grant temporary permissions to all packages that can handle the intent
    val resInfoList =
        context.packageManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
    for (resolveInfo in resInfoList) {
        context.grantUriPermission(
            resolveInfo.activityInfo.packageName,
            fileUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    try {
        context.startActivity(Intent.createChooser(shareIntent, "Share File via"))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app available to share file", Toast.LENGTH_SHORT).show()
    }
}

// Helper function to check file accessibility
private fun isFileAccessible(context: Context, uri: Uri): Boolean {
    return try {
        context.contentResolver.openInputStream(uri)?.use { true } ?: false
    } catch (e: Exception) {
        Log.e("FileAccess", "Error checking file accessibility", e)
        false
    }
}

// Get MediaStore URI from file path
fun getMediaStoreUri(context: Context, filePath: String): Uri? {
    if (filePath.startsWith("content://")) {
        return Uri.parse(filePath)
    }

    val projection = arrayOf(MediaStore.MediaColumns._ID)
    val selection = "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs = arrayOf(filePath)

    val collection = if (isVideoFile(filePath)) {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
            return ContentUris.withAppendedId(collection, id)
        }
    }
    return null
}

// Fallback to FileProvider for direct file access
private fun getFileProviderUri(context: Context, filePath: String): Uri? {
    if (filePath.startsWith("content://")) {
        return Uri.parse(filePath)
    }

    val file = File(filePath)
    if (!file.exists()) return null

    return try {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    } catch (e: Exception) {
        Log.e("FileProvider", "Error getting FileProvider URI", e)
        null
    }
}


fun shareMultipleFiles(filePaths: List<MediaModel>, context: Context) {
    val fileUris = ArrayList<Uri>()

    filePaths.forEach { media ->
        val file = File(media.mediaPath)
        if (file.exists()) {
            try {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ (Use MediaStore URI if possible)
                    getMediaStoreUri(context, file)
                } else {
                    // Use FileProvider URI for older versions
                    FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                }

                if (uri != null) fileUris.add(uri)
            } catch (e: Exception) {
                Log.e("ShareFiles", "Error generating URI for: ${file.absolutePath}", e)
            }
        }
    }

    if (fileUris.isNotEmpty()) {
        val mimeType = getCommonMimeType(filePaths.map { it.mediaMimeType })

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = mimeType
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Files"))
    } else {
        Log.e("ShareFiles", "No valid files to share.")
    }
}

fun getMediaStoreUri(context: Context, file: File): Uri? {
    val contentResolver = context.contentResolver
    val collectionUri = when {
        file.absolutePath.contains("/DCIM") || file.absolutePath.contains("/Pictures") ->
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        file.absolutePath.contains("/Movies") || file.absolutePath.contains("/Videos") ->
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        else -> null
    }

    collectionUri?.let { uri ->
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        val selectionArgs = arrayOf(file.absolutePath)

        contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val id = cursor.getLong(idColumn)
                return ContentUris.withAppendedId(uri, id)
            }
        }
    }
    return null
}


/**
 * Determines the MIME type for sharing based on file types.
 */
private fun getCommonMimeType(mimeTypes: List<String?>): String {
    return when {
        mimeTypes.all { it?.startsWith("image/") == true } -> "image/*"
        mimeTypes.all { it?.startsWith("video/") == true } -> "video/*"
        else -> "*/*"
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

