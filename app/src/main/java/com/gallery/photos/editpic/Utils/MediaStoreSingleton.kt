package com.gallery.photos.editpic.Utils

import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Model.VideoModel

// todo: For All ViewPager

object MediaStoreSingleton {
    var imageList: ArrayList<MediaModel> = arrayListOf()
    var selectedPosition: Int = 0
}

object DeleteMediaStoreSingleton {
    var deleteimageList: ArrayList<DeleteMediaModel> = arrayListOf()
    var deleteselectedPosition: Int = 0
}

object VideoMediaStoreSingleton {
    var videoimageList: ArrayList<VideoModel> = arrayListOf()
    var videoselectedPosition: Int = 0
}

object HideMediaStoreSingleton {
    var hideimageList: ArrayList<HideMediaModel> = arrayListOf()
    var hideselectedPosition: Int = 0
}

object FavouriteMediaStoreSingleton {
    var favouriteimageList: ArrayList<FavouriteMediaModel> = arrayListOf()
    var favouriteselectedPosition: Int = 0
}

object SelectionAlLPhotos {
    var selectionArrayList: ArrayList<String> = arrayListOf()
}