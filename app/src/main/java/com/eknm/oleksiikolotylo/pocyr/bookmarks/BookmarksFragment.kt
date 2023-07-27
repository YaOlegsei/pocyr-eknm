package com.eknm.oleksiikolotylo.pocyr.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eknm.oleksiikolotylo.pocyr.R
import com.eknm.oleksiikolotylo.pocyr.databinding.FragmentBookmarksBinding
import com.eknm.oleksiikolotylo.pocyr.translate.TranslationFragmentViewModel

class BookmarksFragment : Fragment(R.layout.fragment_bookmarks) {

    private val viewModel: BookmarksViewModel by viewModels {
        BookmarksViewModel.getFactory(
            requireActivity().applicationContext
        )
    }
    private lateinit var binding: FragmentBookmarksBinding
    private val bookMarksRecyclerAdapter = BookMarksRecyclerAdapter {
        if (it.isEnabled) {
            viewModel.removeBookmark(it.bookmark)
        }
    }
    private val bookmarksRecycler
        get() = binding.bookmarksRecycler

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