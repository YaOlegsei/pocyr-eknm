package com.eknm.oleksiikolotylo.pocyr

import androidx.lifecycle.ViewModel
import com.eknm.oleksiikolotylo.pocyr.bookmarks.BookmarksProvider

class MainViewModel : ViewModel() {
    companion object{
        var BOOKMARKS_PROVIDER: BookmarksProvider? = null
    }
}