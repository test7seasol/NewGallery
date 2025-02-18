package com.gallery.photos.editpic.Utils

import android.app.Activity
import android.content.ContentUris
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.gallery.photos.editpic.Model.FolderModelItem
import com.gallery.photos.editpic.Model.MediaModelItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object MediaServices {
    fun getAllMedia(activity: Activity): ArrayList<MediaModelItem> {
        val mediaList = ArrayList<MediaModelItem>()

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED
        )

        val sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC"

        // Query for Images, including GIFs (gif has mimeType = image/gif)
        val imageCursor = activity.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, sortOrder
        )

        imageCursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val mimeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

            while (it.moveToNext()) {
                val mediaId = it.getLong(idColumn)
                val mediaName = it.getString(nameColumn)
                val mediaPath = it.getString(dataColumn)
                val mediaMimeType = it.getString(mimeColumn)
                val mediaSize = it.getLong(sizeColumn)
                val mediaDateAdded = it.getLong(dateColumn)

                // Check for GIF type and add it as image or video
                val isGif = mediaMimeType.equals("image/gif", ignoreCase = true)
                mediaList.add(
                    MediaModelItem(
                        mediaId,
                        mediaName,
                        mediaPath,
                        mediaMimeType,
                        mediaSize,
                        mediaDateAdded,
                        isVideo = isGif.not()
                    )
                )
            }
        }

        // Query for Videos
        val videoCursor = activity.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, sortOrder
        )

        videoCursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val mimeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

            while (it.moveToNext()) {
                val mediaId = it.getLong(idColumn)
                val mediaName = it.getString(nameColumn)
                val mediaPath = it.getString(dataColumn)
                val mediaMimeType = it.getString(mimeColumn)
                val mediaSize = it.getLong(sizeColumn)
                val mediaDateAdded = it.getLong(dateColumn)

                mediaList.add(
                    MediaModelItem(
                        mediaId,
                        mediaName,
                        mediaPath,
                        mediaMimeType,
                        mediaSize,
                        mediaDateAdded,
                        isVideo = true
                    )
                )
            }
        }

        return mediaList
    }

    fun copySelectedFiles(selectedList: List<MediaModelItem>, targetFolderName: String) {
        // Create the public folder if it doesn't exist
        val publicFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            targetFolderName
        )
        if (!publicFolder.exists()) {
            publicFolder.mkdirs() // Create folder if it doesn't exist
        }

        // Launch a coroutine to perform the file copy operation on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            selectedList.filter { it.isSelect }.forEach { media ->
                val sourceFile = File(media.path)
                val newFile = File(publicFolder, sourceFile.name)

                try {
                    // Perform file copy operation
                    FileInputStream(sourceFile).use { input ->
                        FileOutputStream(newFile).use { output ->
                            input.copyTo(output) // Copy file
                        }
                    }
                    println("File copied: ${newFile.absolutePath}")

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun moveSelectedFiles(selectedList: List<MediaModelItem>, targetFolderName: String) {
        // Create the public folder if it doesn't exist
        val publicFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            targetFolderName
        )
        if (!publicFolder.exists()) {
            publicFolder.mkdirs() // Create folder if it doesn't exist
        }

        // Launch a coroutine to perform the file move operation on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            selectedList.filter { it.isSelect }.forEach { media ->
                val sourceFile = File(media.path)
                val newFile = File(publicFolder, sourceFile.name)

                try {
                    // Perform file copy operation
                    FileInputStream(sourceFile).use { input ->
                        FileOutputStream(newFile).use { output ->
                            input.copyTo(output) // Copy file
                        }
                    }

                    // Optionally delete the old file
                    sourceFile.delete()
                    println("File moved: ${newFile.absolutePath}")

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getMediaByBucketId(activity: Activity, bucketId: String): ArrayList<MediaModelItem> {
        val mediaList = ArrayList<MediaModelItem>()

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED
        )

        val selection = "${MediaStore.MediaColumns.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)
        val sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC"

        // Query Images
        val imageCursor = activity.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        imageCursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val mimeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

            while (it.moveToNext()) {
                val mediaId = it.getLong(idColumn)
                val mediaName = it.getString(nameColumn)
                val mediaPath = it.getString(dataColumn)
                val mediaMimeType = it.getString(mimeColumn)
                val mediaSize = it.getLong(sizeColumn)
                val mediaDateAdded = it.getLong(dateColumn)

                mediaList.add(
                    MediaModelItem(
                        mediaId,
                        mediaName,
                        mediaPath,
                        mediaMimeType,
                        mediaSize,
                        mediaDateAdded,
                        isVideo = false
                    )
                )
            }
        }

        // Query Videos
        val videoCursor = activity.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        videoCursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val mimeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

            while (it.moveToNext()) {
                val mediaId = it.getLong(idColumn)
                val mediaName = it.getString(nameColumn)
                val mediaPath = it.getString(dataColumn)
                val mediaMimeType = it.getString(mimeColumn)
                val mediaSize = it.getLong(sizeColumn)
                val mediaDateAdded = it.getLong(dateColumn)

                mediaList.add(
                    MediaModelItem(
                        mediaId,
                        mediaName,
                        mediaPath,
                        mediaMimeType,
                        mediaSize,
                        mediaDateAdded,
                        isVideo = true
                    )
                )
            }
        }
        return mediaList
    }

    fun getAllMediaFolders(activity: Activity): ArrayList<FolderModelItem> {
        val folderMap = HashMap<String, FolderModelItem>()

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,  // Use _ID to create URI for media files
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE
        )

        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        // Query Images
        queryMediaStore(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, sortOrder, folderMap)

        // Query Videos
        queryMediaStore(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, sortOrder, folderMap)

        return ArrayList(folderMap.values)
    }

    private fun queryMediaStore(
        activity: Activity,
        uri: Uri,
        projection: Array<String>,
        sortOrder: String,
        folderMap: HashMap<String, FolderModelItem>
    ) {
        val cursor = activity.contentResolver.query(uri, projection, null, null, sortOrder)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val bucketIdColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
            val bucketNameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val bucketId = it.getString(bucketIdColumn) ?: continue
                val bucketName = it.getString(bucketNameColumn) ?: "Unknown"
                val fileSize = it.getLong(sizeColumn)

                // Create a URI for the media item using its ID
                val mediaUri = ContentUris.withAppendedId(uri, id)

                if (!folderMap.containsKey(bucketId)) {
                    folderMap[bucketId] = FolderModelItem(bucketId, bucketName, mediaUri.toString(), 0, 0)
                }

                val folder = folderMap[bucketId]!!
                folder.fileCount += 1
                folder.totalSize += fileSize
            }
        }
    }
}
