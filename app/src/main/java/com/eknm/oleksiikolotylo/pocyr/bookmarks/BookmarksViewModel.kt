package com.eknm.oleksiikolotylo.pocyr.bookmarks

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.eknm.oleksiikolotylo.pocyr.MainViewModel
import com.eknm.oleksiikolotylo.pocyr.translate.TranslationFragmentViewModel

class BookmarksViewModel(private val bookmarksProvider: BookmarksProvider) : ViewModel() {
    private var bookmarksTemp = listOf<BookMarksRecyclerAdapter.ManipulatedBookmark>()
        set(value) {
            field = value
            _tempBookmarksLiveData.value = value
        }
    private val _tempBookmarksLiveData = MutableLiveData<List<BookMarksRecyclerAdapter.ManipulatedBookmark>>()
    val bookmarksLiveData: LiveData<List<BookMarksRecyclerAdapter.ManipulatedBookmark>>
        get() = MediatorLiveData<List<BookMarksRecyclerAdapter.ManipulatedBookmark>>().apply {
            addSource(_tempBookmarksLiveData) {
                value = it
            }
            addSource(bookmarksProvider.bookmarksLiveData) {
                value = it.map {
                    BookMarksRecyclerAdapter.ManipulatedBookmark(it, true)
                }
            }

        }

    fun removeBookmark(bookmark: Bookmark) {
        /*bookmarksTemp = bookmarksTemp.map {
            if (it.bookmark == bookmark){
                it.copy(isEnabled = false)
            }
        }filter { it != bookmark }*/
        bookmarksProvider.removeBookmark(bookmark)
    }

    companion object {
        fun getFactory(applicationContext: Context) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T = if (MainViewModel.BOOKMARKS_PROVIDER != null) {
                MainViewModel.BOOKMARKS_PROVIDER!!
            }else{
                MainViewModel.BOOKMARKS_PROVIDER = BookmarksProvider(applicationContext)
                MainViewModel.BOOKMARKS_PROVIDER!!
            }.run {
                BookmarksViewModel(this) as T
            }
        }
    }
}