package com.eknm.oleksiikolotylo.pocyr.translate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eknm.oleksiikolotylo.pocyr.bookmarks.Bookmark
import com.eknm.oleksiikolotylo.pocyr.bookmarks.BookmarksProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TranslationFragmentViewModel @Inject constructor(
    private val bookmarksProvider: BookmarksProvider,
) :
    ViewModel() {
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
}