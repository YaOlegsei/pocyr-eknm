package com.eknm.oleksiikolotylo.pocyr.translate

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.eknm.oleksiikolotylo.pocyr.MainViewModel
import com.eknm.oleksiikolotylo.pocyr.bookmarks.Bookmark
import com.eknm.oleksiikolotylo.pocyr.bookmarks.BookmarksProvider
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TranslationFragmentViewModel(private val bookmarksProvider: BookmarksProvider) : ViewModel() {

    private val translator: TextTranslator = PoCyrTranslator
    val translatedTextLiveData: LiveData<String>
        get() = _translatedTextLiveData
    private val _translatedTextLiveData = MutableLiveData<String>()

    fun translateText(text: String) {
        _translatedTextLiveData.value = translator.translate(text)
    }

    fun makeBookmark(bookmark: Bookmark): LiveData<Boolean> {
        val saveBookmarkLiveData = MutableLiveData(true)
        val disposable = Single.fromCallable {
            bookmarksProvider.saveBookmark(bookmark)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    saveBookmarkLiveData.value = true
                }, {
                    saveBookmarkLiveData.value = false
                }
            )
        return saveBookmarkLiveData
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
                TranslationFragmentViewModel(this) as T
            }
        }
    }
}