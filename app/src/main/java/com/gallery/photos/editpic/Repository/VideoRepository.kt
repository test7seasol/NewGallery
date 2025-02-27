package com.gallery.photos.editpic.Repository

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.gallery.photos.editpic.Extensions.formatDate
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Model.VideoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoRepository(private val context: Context) {

    suspend fun getAllVideos(): List<VideoModel> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<VideoModel>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val videoId = cursor.getLong(idColumn)
                val videoName = cursor.getString(nameColumn) ?: "Unknown"
                val videoPath = cursor.getString(pathColumn) ?: ""
                val videoSize = cursor.getLong(sizeColumn)
                val videoDuration = cursor.getLong(durationColumn)
                val videoDateAdded = cursor.getLong(dateAddedColumn) * 1000 // Convert to milliseconds

                videoList.add(
                    VideoModel(
                        videoId,
                        videoName,
                        videoPath,
                        videoSize,
                        videoDuration,
                        videoDateAdded)
                )
            }
        }
        return@withContext videoList
    }


    suspend fun getAllVideoscall(): List<VideoModel> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<VideoModel>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC" // Sorting by latest first

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            var count = 0
            while (cursor.moveToNext() && count < 50) { // ✅ Limit results to 50
                val videoId = cursor.getLong(idColumn)
                val videoName = cursor.getString(nameColumn) ?: "Unknown"
                val videoPath = cursor.getString(pathColumn) ?: ""
                val videoSize = cursor.getLong(sizeColumn)
                val videoDuration = cursor.getLong(durationColumn)
                val videoDateAdded = cursor.getLong(dateAddedColumn) * 1000 // Convert to milliseconds

                videoList.add(
                    VideoModel(
                        videoId,
                        videoName,
                        videoPath,
                        videoSize,
                        videoDuration,
                        videoDateAdded
                    )
                )
                count++ // ✅ Increment count to enforce limit
            }
        }
        return@withContext videoList
    }



}
