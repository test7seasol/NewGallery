package com.gallery.photos.editpic.Repository

import android.content.Context
import android.provider.MediaStore
import com.gallery.photos.editpic.Extensions.DATE_LIMIT
import com.gallery.photos.editpic.Model.MediaModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecentPictureRepository(private val context: Context) {

    /*  suspend fun getAllMedia(): List<MediaModel> = withContext(Dispatchers.IO) {
          val mediaList = mutableListOf<MediaModel>()

          val projection = arrayOf(
              MediaStore.Files.FileColumns._ID,
              MediaStore.Files.FileColumns.DISPLAY_NAME,
              MediaStore.Files.FileColumns.DATA,
              MediaStore.Files.FileColumns.MIME_TYPE,
              MediaStore.Files.FileColumns.SIZE,
              MediaStore.Files.FileColumns.DATE_ADDED
          )

          val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"
          val selectionArgs = arrayOf(
              MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
              MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
          )

          val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

          context.contentResolver.query(
              MediaStore.Files.getContentUri("external"),
              projection,
              selection,
              selectionArgs,
              sortOrder
          )?.use { cursor ->
              val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
              val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
              val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
              val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
              val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
              val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)

              while (cursor.moveToNext()) {
                  val mediaId = cursor.getLong(idColumn)
                  val mediaName = cursor.getString(nameColumn) ?: "Unknown"
                  val mediaPath = cursor.getString(pathColumn) ?: ""
                  val mediaMimeType = cursor.getString(mimeTypeColumn) ?: ""
                  val mediaSize = cursor.getLong(sizeColumn)
                  val mediaDateAdded = cursor.getLong(dateAddedColumn) // Unix timestamp

                  val isVideo = mediaMimeType.startsWith("video")
                  val displayDate = formatDate(mediaDateAdded)

                  mediaList.add(
                      MediaModel(
                          mediaId = mediaId,
                          mediaName = mediaName,
                          mediaPath = mediaPath,
                          mediaMimeType = mediaMimeType,
                          mediaSize = mediaSize,
                          mediaDateAdded = mediaDateAdded,
                          isVideo = isVideo,
                          displayDate = displayDate
                      )
                  )
              }
          }
          return@withContext mediaList
      }
  */
    suspend fun getAllMedia(limit: Int = DATE_LIMIT, offset: Int = 0): List<MediaModel> =
        withContext(Dispatchers.IO) {
            val mediaList = mutableListOf<MediaModel>()

            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_ADDED
            )

            val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"
            val selectionArgs = arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
            )

            val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

            context.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->

                if (offset > 0) cursor.moveToPosition(offset - 1) // Skip initial records

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val mimeTypeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val dateAddedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)

                var count = 0
                while (cursor.moveToNext() && count < limit) { // Read only `limit` items
                    val mediaId = cursor.getLong(idColumn)
                    val mediaName = cursor.getString(nameColumn) ?: "Unknown"
                    val mediaPath = cursor.getString(pathColumn) ?: ""
                    val mediaMimeType = cursor.getString(mimeTypeColumn) ?: ""
                    val mediaSize = cursor.getLong(sizeColumn)
                    val mediaDateAdded =
                        cursor.getLong(dateAddedColumn) * 1000 // Convert to milliseconds

                    val isVideo = mediaMimeType.startsWith("video")

                    mediaList.add(
                        MediaModel(
                            mediaId = mediaId,
                            mediaName = mediaName,
                            mediaPath = mediaPath,
                            mediaMimeType = mediaMimeType,
                            mediaSize = mediaSize,
                            mediaDateAdded = mediaDateAdded,
                            isVideo = isVideo,
                            displayDate = formatDate(mediaDateAdded)
                        )
                    )

                    count++
                }
            }
            return@withContext mediaList
        }

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        calendar.timeInMillis = timestamp

        return when {
            isSameDay(calendar, today) -> "Today"
            isSameDay(calendar, yesterday) -> "Yesterday"
            else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
            Calendar.DAY_OF_YEAR
        )
    }
}