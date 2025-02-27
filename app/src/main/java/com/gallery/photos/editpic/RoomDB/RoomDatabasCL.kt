package com.gallery.photos.editpic.RoomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao

@Database(
    entities = [DeleteMediaModel::class, HideMediaModel::class, FavouriteMediaModel::class],  // Use FavouriteMediaModel, not FavouriteMediaDao
    version = 1,
    exportSchema = false
)
abstract class MediaDatabase : RoomDatabase() {

    abstract fun deleteMediaDao(): DeleteMediaDao
    abstract fun hideMediaDao(): HideMediaDao
    abstract fun favouriteMediaDao(): FavouriteMediaDao

    companion object {
        @Volatile
        private var INSTANCE: MediaDatabase? = null

        fun getDatabase(context: Context): MediaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediaDatabase::class.java,
                    "media_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
