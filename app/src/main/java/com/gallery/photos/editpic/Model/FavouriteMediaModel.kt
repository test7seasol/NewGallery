package com.gallery.photos.editpic.Model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "favourite_media_table")
data class FavouriteMediaModel(
    @PrimaryKey(autoGenerate = true) var randomMediaId: Long = 0,
    var mediaId: Long = 0,
    var mediaName: String = "",
    var mediaPath: String = "",
    var mediaMimeType: String = "",
    var mediaSize: Long = 0,
    var mediaDateAdded: Long = 0,
    var isVideo: Boolean = false,
    var displayDate: String = "",
    var isSelect: Boolean = false,
    var isFav: Boolean = false
) : Parcelable
