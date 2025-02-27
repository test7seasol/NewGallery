package com.gallery.photos.editpic.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallery.photos.editpic.Extensions.DATE_LIMIT
import com.gallery.photos.editpic.Model.MediaListItem
import com.gallery.photos.editpic.Repository.RecentPictureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RecentPictureViewModel(private val repository: RecentPictureRepository) : ViewModel() {

//    private val _mediaLiveData = MutableLiveData<List<MediaListItem>>()
//    val mediaLiveData: LiveData<List<MediaListItem>> = _mediaLiveData

   /* fun loadRecentMedia() {
        viewModelScope.launch(Dispatchers.IO) {
            val mediaList = repository.getAllMedia()  // Fetch all pictures/videos

            // Group media by date
            val groupedMedia = mediaList.groupBy { it.displayDate }

            val finalList = mutableListOf<MediaListItem>()

            for ((date, mediaItems) in groupedMedia) {
                finalList.add(MediaListItem.Header(date))  // Add Date Header
                finalList.addAll(mediaItems.map { MediaListItem.Media(it) }) // Add Media Items
            }

            _mediaLiveData.postValue(finalList)
        }
    }*/

    private val _mediaLiveData = MutableLiveData<List<MediaListItem>>()
    val mediaLiveData: LiveData<List<MediaListItem>> = _mediaLiveData

    fun loadRecentMedia() {
        viewModelScope.launch(Dispatchers.IO) {
            val mediaList = repository.getAllMedia(limit = DATE_LIMIT)  // Load first batch

            val groupedMedia = mediaList.groupBy { it.displayDate }
            val finalList = mutableListOf<MediaListItem>()

            for ((date, mediaItems) in groupedMedia) {
                finalList.add(MediaListItem.Header(date))
                finalList.addAll(mediaItems.map { MediaListItem.Media(it) })
            }

            _mediaLiveData.postValue(finalList)

            // Load remaining media in background (if more than 1000)
            val remainingMedia = repository.getAllMedia(limit = Int.MAX_VALUE, offset = DATE_LIMIT)
            val allMedia = mediaList + remainingMedia

            val groupedAllMedia = allMedia.groupBy { it.displayDate }
            val finalAllList = mutableListOf<MediaListItem>()

            for ((date, mediaItems) in groupedAllMedia) {
                finalAllList.add(MediaListItem.Header(date))
                finalAllList.addAll(mediaItems.map { MediaListItem.Media(it) })
            }
            _mediaLiveData.postValue(finalAllList)
        }
    }

    fun loadRecentMediacall() {
        viewModelScope.launch(Dispatchers.IO) {
            val mediaList = repository.getAllMediacall()

            if (mediaList.isEmpty()) {
                _mediaLiveData.postValue(emptyList()) // Ensure UI doesn't hang on empty state
                return@launch
            }

            // Grouping media by date
            val groupedMedia = mediaList.groupBy { it.displayDate }
            val finalList = mutableListOf<MediaListItem>()

            for ((date, mediaItems) in groupedMedia) {
                finalList.add(MediaListItem.Header(date)) // ✅ Add Date Header

                // ✅ Add media items below the header
                finalList.addAll(mediaItems.map { MediaListItem.Media(it) })
            }

            // ✅ Post the entire list once, instead of inside the loop
            _mediaLiveData.postValue(finalList)
        }
    }



}



