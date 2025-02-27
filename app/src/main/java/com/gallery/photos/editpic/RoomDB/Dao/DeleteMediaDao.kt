package com.gallery.photos.editpic.RoomDB.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel

@Dao
interface DeleteMediaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedia(media: DeleteMediaModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMedia(mediaList: List<DeleteMediaModel>)

    @Query("SELECT * FROM media_table ORDER BY mediaDateAdded DESC")
    fun getAllMediaLive(): LiveData<List<DeleteMediaModel>>

    @Query("SELECT * FROM media_table WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getMediaById(mediaId: Long): DeleteMediaModel?

    @Delete
    suspend fun deleteMedia(media: DeleteMediaModel)

    @Query("DELETE FROM media_table")
    suspend fun deleteAllMedia()
}

@Dao
interface HideMediaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedia(media: HideMediaModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMedia(mediaList: List<HideMediaModel>)

    @Query("SELECT * FROM hidemedia_table WHERE mediaPath = :mediaPath LIMIT 1")
    suspend fun getMediaByPath(mediaPath: String): HideMediaModel?

    @Query("SELECT * FROM hidemedia_table ORDER BY mediaDateAdded DESC")
    fun getAllMediaLive(): LiveData<List<HideMediaModel>>

    @Query("SELECT * FROM hidemedia_table WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getMediaById(mediaId: Long): HideMediaModel?

    @Delete
    suspend fun deleteMedia(media: HideMediaModel)

    @Query("DELETE FROM hidemedia_table")
    suspend fun deleteAllMedia()
}

@Dao
interface FavouriteMediaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedia(media: FavouriteMediaModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMedia(mediaList: List<FavouriteMediaModel>)

    @Query("SELECT * FROM favourite_media_table WHERE mediaPath = :mediaPath LIMIT 1")
    suspend fun getMediaByPath(mediaPath: String): FavouriteMediaModel?

    @Query("SELECT * FROM favourite_media_table ORDER BY mediaDateAdded DESC")
    fun getAllMediaLive(): LiveData<List<FavouriteMediaModel>>

    @Query("SELECT * FROM favourite_media_table WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getMediaById(mediaId: Long): FavouriteMediaModel?

    @Delete
    suspend fun deleteMedia(media: FavouriteMediaModel)

    @Query("DELETE FROM favourite_media_table")
    suspend fun deleteAllMedia()

    // New function to check if media is a favorite
    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM favourite_media_table WHERE mediaId = :mediaId AND isFav = 1")
    suspend fun isMediaFavorite(mediaId: Long): Boolean
}
