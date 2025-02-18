package com.gallery.photos.editpic.Repository

import android.app.Application
import android.database.Cursor
import android.provider.MediaStore
import com.gallery.photos.editpic.Model.MediaModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/*
class PictureRepository(private val context: Context) {

    private val _mediaLiveData = MutableLiveData<List<MediaModel>>()
    val mediaLiveData: LiveData<List<MediaModel>> get() = _mediaLiveData

    // Fetch media inside a selected album (by bucketId)
    suspend fun loadMediaByBucketId(bucketId: String) {
        withContext(Dispatchers.IO) {
            val mediaFiles = getMediaFiles(bucketId)
            _mediaLiveData.postValue(mediaFiles)
        }
    }

    private fun getMediaFiles(bucketId: String): ArrayList<MediaModel> {
        val mediaList = ArrayList<MediaModel>()

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
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        queryMedia(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder, mediaList, false)
        queryMedia(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder, mediaList, true)

        return mediaList
    }

    private fun queryMedia(
        uri: android.net.Uri,
        projection: Array<String>,
        selection: String,
        selectionArgs: Array<String>,
        sortOrder: String,
        mediaList: ArrayList<MediaModel>,
        isVideo: Boolean
    ) {
        val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

            while (it.moveToNext()) {
                val mediaId = it.getLong(idColumn)
                val mediaName = it.getString(nameColumn) ?: "Unknown"
                val mediaPath = it.getString(pathColumn)
                val mediaMimeType = it.getString(mimeTypeColumn) ?: "unknown"
                val mediaSize = it.getLong(sizeColumn)
                val mediaDateAdded = it.getLong(dateColumn)
                val displayDate = MediaModel.formatDate(mediaDateAdded)

                mediaList.add(
                    MediaModel(
                        mediaId,
                        mediaName,
                        mediaPath,
                        mediaMimeType,
                        mediaSize,
                        mediaDateAdded,
                        isVideo,
                        displayDate
                    )
                )
            }
        }
    }
}
*/

class PictureRepository(private val application: Application) {

    fun getMediaFiles(bucketId: String): Flow<List<MediaModel>> = flow {
        val mediaList = mutableListOf<MediaModel>()
        val maxItems = 100 // Limit for smooth performance

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
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        // Query Images
        queryMediaStore(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder, mediaList)

        // Query Videos
        queryMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder, mediaList)

        emit(mediaList) // Emit final list
    }.flowOn(Dispatchers.IO) // Run in background thread

    private fun queryMediaStore(
        uri: android.net.Uri,
        projection: Array<String>,
        selection: String,
        selectionArgs: Array<String>,
        sortOrder: String,
        mediaList: MutableList<MediaModel>
    ) {
        val cursor: Cursor? = application.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val typeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

            while (it.moveToNext()) {
                val media = MediaModel(
                    mediaId = it.getLong(idColumn),
                    mediaName = it.getString(nameColumn) ?: "Unknown",
                    mediaPath = it.getString(pathColumn),
                    mediaMimeType = it.getString(typeColumn) ?: "",
                    mediaSize = it.getLong(sizeColumn),
                    mediaDateAdded = it.getLong(dateColumn),
                    isVideo = it.getString(typeColumn)?.startsWith("video") ?: false,
                    displayDate = it.getLong(dateColumn).toString()
                )
                mediaList.add(media)
            }
        }
    }
}