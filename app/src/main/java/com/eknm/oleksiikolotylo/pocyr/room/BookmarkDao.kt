package com.eknm.oleksiikolotylo.pocyr.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.eknm.oleksiikolotylo.pocyr.bookmarks.Bookmark

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM Bookmark")
    fun getBookmarks(): LiveData<List<Bookmark>>

    @Delete
    fun removeBookmark(bookmark: Bookmark)

    @Insert
    fun addBookmark(bookmark: Bookmark)
}