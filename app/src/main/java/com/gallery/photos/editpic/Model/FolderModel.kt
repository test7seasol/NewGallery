package com.gallery.photos.editpic.Model

import androidx.annotation.Keep

@Keep
data class FolderModel(
    val bucketId: String,
    val bucketName: String,
    val folderName: String, // Folder name
    val thumbnail: String, // Latest media file as thumbnail
    var fileCount: Int, // Total number of media files
    var totalSize: Long ,// Total folder size in bytes
    var lastAdded: Long ,// Store last modified date
    var isSelect: Boolean = false // Store last modified date
)
