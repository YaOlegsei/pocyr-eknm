package com.eknm.oleksiikolotylo.pocyr.bookmarks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bookmark(
    @PrimaryKey
    val originalText: String,
    val translatedText: String
)

