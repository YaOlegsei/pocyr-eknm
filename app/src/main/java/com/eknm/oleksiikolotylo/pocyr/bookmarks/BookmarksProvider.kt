package com.eknm.oleksiikolotylo.pocyr.bookmarks

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.eknm.oleksiikolotylo.pocyr.room.Database
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class BookmarksProvider(applicationContext: Context) {
    private val db: Database = Room.databaseBuilder(
        applicationContext,
        Database::class.java, "database-name"
    ).build()

    private val bookmarksDao
        get() = db.bookmarkDao()

    val bookmarksLiveData: LiveData<List<Bookmark>>
        get() = bookmarksDao.getBookmarks()

    fun saveBookmark(bookmark: Bookmark) {
        bookmarksDao.addBookmark(bookmark)
    }

    fun removeBookmark(bookmark: Bookmark) {
        bookmarksDao.removeBookmark(bookmark)
    }
}