package com.eknm.oleksiikolotylo.pocyr.bookmarks

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.eknm.oleksiikolotylo.pocyr.R
import com.eknm.oleksiikolotylo.pocyr.databinding.FragmentBookmarksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {

    private val clipboard get() = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val viewModel: BookmarksViewModel by viewModels()
    private lateinit var binding: FragmentBookmarksBinding
    private val bookMarksRecyclerAdapter = BookMarksRecyclerAdapter(onBookMarkClickAction = {},
        onBookMarkRemoveClickAction = {
            if (it.isEnabled) {
                viewModel.removeBookmark(it.bookmark)
            }
        },
        onBookMarkCopyClickAction = {
            val clip = ClipData.newPlainText("PoCyr translation", it.bookmark.translatedText)
            clipboard.setPrimaryClip(clip)
        },
        onBookMarkInputClickAction = {
            if (it.isEnabled) {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "bookmark",
                    it.bookmark.translatedText
                )
                navController.popBackStack()
            }
        })
    private val bookmarksRecycler
        get() = binding.bookmarksRecycler
    private val navController get() = binding.root.findNavController()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        bookmarksRecycler.adapter = bookMarksRecyclerAdapter
        viewModel.bookmarksLiveData.observe(viewLifecycleOwner) {
            bookMarksRecyclerAdapter.submitList(it.toMutableList())
        }
        return binding.root
    }
}