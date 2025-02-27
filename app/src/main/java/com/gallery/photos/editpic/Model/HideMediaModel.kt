package com.gallery.photos.editpic.Model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "hidemedia_table")
data class HideMediaModel(
    @PrimaryKey(autoGenerate = true) var randomMediaId: Long = 0,  // Auto-increment unique ID
    var mediaId: Long = 0,          // Unique ID of the media file
    var mediaName: String = "",      // Name of the media file
    var mediaPath: String = "",      // File path of the media
    var mediaMimeType: String = "",  // MIME type (image/jpeg, video/mp4, etc.)
    var mediaSize: Long = 0,        // File size in bytes
    var mediaDateAdded: Long = 0,   // Date added in Unix timestamp (seconds)
    var isVideo: Boolean = false,       // True if it's a video, False if an image
    var displayDate: String = "",    // Formatted date string (e.g., "Jan 01, 2024")
    var isSelect: Boolean = false // Selection state (default: not selected)
) : Parcelable
