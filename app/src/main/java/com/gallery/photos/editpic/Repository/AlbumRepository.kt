package com.gallery.photos.editpic.Repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gallery.photos.editpic.Model.FolderModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumRepository(private val context: Context) {

    private val _folderLiveData = MutableLiveData<List<FolderModel>>()
    val folderLiveData: LiveData<List<FolderModel>> get() = _folderLiveData

    suspend fun loadMediaFolders() {
        withContext(Dispatchers.IO) {
            val folders = getAllMediaFolders()
            _folderLiveData.postValue(folders)
        }
    }

    private fun getAllMediaFolders(): ArrayList<FolderModel> {
        val folderMap = HashMap<String, FolderModel>()

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,                 // Added _ID for constructing media URI
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED
        )

        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        // Query Images
        queryMediaStore(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, sortOrder, folderMap)

        // Query Videos
        queryMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, sortOrder, folderMap)

        return ArrayList(folderMap.values.sortedByDescending { it.lastAdded })
    }

    private fun queryMediaStore(
        uri: android.net.Uri,
        projection: Array<String>,
        sortOrder: String,
        folderMap: HashMap<String, FolderModel>
    ) {
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, sortOrder)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val bucketIdColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
            val bucketNameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val mediaUri = Uri.withAppendedPath(uri, id.toString())

                val bucketId = it.getString(bucketIdColumn) ?: continue
                val bucketName = it.getString(bucketNameColumn) ?: "Unknown"
                val fileSize = it.getLong(sizeColumn)
                val dateAdded = it.getLong(dateAddedColumn) * 1000 // Convert to milliseconds

                if (!folderMap.containsKey(bucketId)) {
                    folderMap[bucketId] = FolderModel(
                        bucketId = bucketId,
                        bucketName = bucketName,
                        folderName = bucketName,
                        thumbnail = mediaUri.toString(), // Use mediaUri as thumbnail
                        fileCount = 0,
                        totalSize = 0,
                        lastAdded = dateAdded
                    )
                }

                val folder = folderMap[bucketId]!!
                folder.fileCount += 1
                folder.totalSize += fileSize

                // Update last added date if a newer file is found
                if (dateAdded > folder.lastAdded) {
                    folder.lastAdded = dateAdded
                }
            }
        }
    }


}
