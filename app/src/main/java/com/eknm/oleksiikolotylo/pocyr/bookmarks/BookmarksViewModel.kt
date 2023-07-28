package com.eknm.oleksiikolotylo.pocyr.bookmarks

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.eknm.oleksiikolotylo.pocyr.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(private val bookmarksProvider: BookmarksProvider) :
    ViewModel() {

    private var bookmarksPending = listOf<BookMarksRecyclerAdapter.ManipulatedBookmark>()
        set(value) {
            field = value
            _tempBookmarksLiveData.value = value
        }
    private val _tempBookmarksLiveData =
        MutableLiveData<List<BookMarksRecyclerAdapter.ManipulatedBookmark>>()
    val bookmarksLiveData: LiveData<List<BookMarksRecyclerAdapter.ManipulatedBookmark>>
        get() = MediatorLiveData<List<BookMarksRecyclerAdapter.ManipulatedBookmark>>().apply {
            addSource(bookmarksProvider.bookmarksLiveData) {
                value = it.map {
                    BookMarksRecyclerAdapter.ManipulatedBookmark(it, true)
                }
            }

        }

    fun removeBookmark(bookmark: Bookmark): LiveData<Boolean> {
        val removeBookmarkLiveData = MutableLiveData(true)
        val disposable = Single.fromCallable {
            bookmarksProvider.removeBookmark(bookmark)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    removeBookmarkLiveData.value = true
                }, {
                    removeBookmarkLiveData.value = false
                }
            )
        return removeBookmarkLiveData
    }
}