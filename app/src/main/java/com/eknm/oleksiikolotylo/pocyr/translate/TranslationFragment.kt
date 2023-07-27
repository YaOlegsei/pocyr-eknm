package com.eknm.oleksiikolotylo.pocyr.translate

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eknm.oleksiikolotylo.pocyr.R
import com.eknm.oleksiikolotylo.pocyr.bookmarks.Bookmark
import com.eknm.oleksiikolotylo.pocyr.databinding.FragmentTranslationBinding

class TranslationFragment : Fragment(R.layout.fragment_translation) {
    private val viewModel: TranslationFragmentViewModel by viewModels {
        TranslationFragmentViewModel.getFactory(requireActivity().applicationContext)
    }
    private val clipboard get() = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private lateinit var binding: FragmentTranslationBinding
    private val textTranslateInput
        get() = binding.textTranslateInput
    private val translatedTextTextView
        get() = binding.translatedTextTextView

    private val buttonMakeBookmark
        get() = binding.buttonMakeBookmark

    private val buttonCopy
        get() = binding.buttonCopy

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTranslationBinding.inflate(inflater, container, false)
        viewModel.translatedTextLiveData.observe(viewLifecycleOwner) { translatedText ->
            translatedTextTextView.text = translatedText
        }
        textTranslateInput.doOnTextChanged { text, _, _, _ ->
            viewModel.translateText(text.toString())
        }
        buttonCopy.setOnClickListener {
            val clip = ClipData.newPlainText("PoCyr translation", translatedTextTextView.text)
            clipboard.setPrimaryClip(clip)
        }

        buttonMakeBookmark.setOnClickListener {
            //TODO: Add loading state
            viewModel
                .makeBookmark(Bookmark(textTranslateInput.text.toString()))
                .observe(viewLifecycleOwner) {
                    //TODO handle adding
                }
        }
        return binding.root
    }
}