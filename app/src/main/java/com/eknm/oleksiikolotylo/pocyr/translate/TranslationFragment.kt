package com.eknm.oleksiikolotylo.pocyr.translate

import android.animation.LayoutTransition
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eknm.oleksiikolotylo.pocyr.R
import com.eknm.oleksiikolotylo.pocyr.bookmarks.Bookmark
import com.eknm.oleksiikolotylo.pocyr.databinding.FragmentTranslationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TranslationFragment : Fragment(R.layout.fragment_translation) {
    private val viewModel: TranslationFragmentViewModel by viewModels()
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
        binding.translationContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.textTranslateInput.setHorizontallyScrolling(false)
        binding.textTranslateInput.maxLines = 50
        translatedTextTextView.setOnLongClickListener {
            copyText()
            true
        }
        viewModel.translatedTextLiveData.observe(viewLifecycleOwner) { translatedText ->
            translatedTextTextView.text = translatedText
        }
        textTranslateInput.doOnTextChanged { text, _, _, _ ->
            viewModel.translateText(text.toString())
        }
        buttonCopy.setOnClickListener { copyText() }

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

    private fun copyText() {
        val clip = ClipData.newPlainText("PoCyr translation", translatedTextTextView.text)
        clipboard.setPrimaryClip(clip)
    }
}