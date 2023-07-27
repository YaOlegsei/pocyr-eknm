package com.eknm.oleksiikolotylo.pocyr.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eknm.oleksiikolotylo.pocyr.bookmarks.Bookmark

@Database(entities = [Bookmark::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}
