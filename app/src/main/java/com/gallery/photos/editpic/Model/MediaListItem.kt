package com.gallery.photos.editpic.Model

import androidx.annotation.Keep

@Keep
sealed class MediaListItem {
    data class Header(val date: String) : MediaListItem()
    data class Media(val media: MediaModel) : MediaListItem()
}

@Keep
sealed class DeleteMediaListItem {
    data class DeleteHeader(val date: String) : DeleteMediaListItem()
    data class DeleteMedia(val media: DeleteMediaModel) : DeleteMediaListItem()
}
