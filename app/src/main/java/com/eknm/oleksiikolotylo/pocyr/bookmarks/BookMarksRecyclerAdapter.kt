package com.eknm.oleksiikolotylo.pocyr.bookmarks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eknm.oleksiikolotylo.pocyr.databinding.ItemBookmarkBinding

class BookMarksRecyclerAdapter(private val onBookMarkClickAction: (ManipulatedBookmark) -> Unit) :
    ListAdapter<BookMarksRecyclerAdapter.ManipulatedBookmark, BookMarksRecyclerAdapter.BookmarkViewHolder>(
        BOOKMARK_ITEM_CALLBACK
    ) {

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val bookmarkBinding =
            ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(bookmarkBinding)
    }

    override fun submitList(list: MutableList<ManipulatedBookmark>?) {
        super.submitList(list)
    }

    inner class BookmarkViewHolder(private val itemBookmarkBinding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(itemBookmarkBinding.root) {

        fun bind(bookmark: ManipulatedBookmark) {
            itemBookmarkBinding.removeBookmarkIcon.setOnClickListener {
                onBookMarkClickAction(bookmark)
            }
            itemBookmarkBinding.bookmarkTextView.text = bookmark.bookmark.savedText
        }
    }

    companion object {
        private val BOOKMARK_ITEM_CALLBACK = object : DiffUtil.ItemCallback<ManipulatedBookmark>() {
            override fun areContentsTheSame(
                oldItem: ManipulatedBookmark,
                newItem: ManipulatedBookmark
            ): Boolean =
                oldItem.isEnabled == newItem.isEnabled


            override fun areItemsTheSame(
                oldItem: ManipulatedBookmark,
                newItem: ManipulatedBookmark
            ): Boolean =
                oldItem.bookmark == newItem.bookmark
        }
    }

    data class ManipulatedBookmark(
        val bookmark: Bookmark,
        val isEnabled: Boolean,
    )
}