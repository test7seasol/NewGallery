package com.gallery.photos.editpic.Model


data class MediaModelItem(
    val bucketId: Long,
    val name: String,
    val path: String,
    val mimeType: String,
    val size: Long,
    val dateAdded: Long,
    val isVideo: Boolean,
    var isSelect: Boolean = false
)

data class FolderModelItem(
    val bucketId: String, val bucketName: String, val thumbnail: String, // Thumbnail path
    var fileCount: Int, // Total number of images + videos in the folder
    var totalSize: Long // Total size of the folder in bytes
)
