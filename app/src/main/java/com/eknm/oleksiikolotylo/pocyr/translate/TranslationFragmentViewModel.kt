package com.eknm.oleksiikolotylo.pocyr.translate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eknm.oleksiikolotylo.pocyr.bookmarks.Bookmark
import com.eknm.oleksiikolotylo.pocyr.bookmarks.BookmarksProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TranslationFragmentViewModel @Inject constructor(
    private val bookmarksProvider: BookmarksProvider,
) :
    ViewModel() {
    private var disposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }
    private val translator: TextTranslator = PoCyrTranslator
    var textToTranslate = ""
        set(value) {
            if (field != value) {
                if (field != value) {
                    field = value
                    isLoading = false
                    _translatedTextLiveData.value = translator.translate(field)
                }

            }
        }
    private var isLoading = false
        set(value) {
            field = value
            textAndIsLoadingLiveData.value = Pair(textToTranslate, field)
        }
    private val textAndIsLoadingLiveData =
        MutableLiveData(Pair(textToTranslate, isLoading))

    val translatedTextLiveData: LiveData<String>
        get() = _translatedTextLiveData
    private val _translatedTextLiveData = MutableLiveData<String>()

    val bookmarkStateLiveData: LiveData<BookmarkState>
        get() = combineLatest(
            textAndIsLoadingLiveData,
            bookmarksProvider.bookmarksLiveData,
            { (text, isLoading), bookmark ->
                when {
                    bookmark.map { it.originalText }.contains(text) -> BookmarkState.Saved
                    isLoading -> BookmarkState.Loading
                    else -> BookmarkState.NotSaved
                }
            }
        )

    fun makeBookmark() {
        if (!isLoading) {
            isLoading = true
            disposable = Single.fromCallable {
                bookmarksProvider.saveBookmark(
                    Bookmark(
                        textToTranslate,
                        translatedTextLiveData.value!!
                    )
                )
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        isLoading = false
                    }, {
                        isLoading = false
                    }
                )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable = null
    }
}

enum class BookmarkState {
    NotSaved,
    Loading,
    Saved,
}

fun <T, S1, S2> combineLatest(
    sourceOne: LiveData<S1>,
    sourceTwo: LiveData<S2>,
    combineFn: (S1, S2) -> T,
    default: T? = null
): LiveData<T> {
    return MediatorLiveData<T>().apply {
        if (default != null) {
            value = default
        }
        var s1: S1? = null
        var s2: S2? = null
        val fn = {
            if (s1 != null && s2 != null) {
                value = combineFn(s1!!, s2!!)
            }
        }
        addSource(sourceOne) {
            s1 = it
            fn()
        }
        addSource(sourceTwo) {
            s2 = it
            fn()
        }
    }
}