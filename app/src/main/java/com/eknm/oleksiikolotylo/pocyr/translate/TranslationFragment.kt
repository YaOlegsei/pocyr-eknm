package com.eknm.oleksiikolotylo.pocyr.translate

import android.animation.LayoutTransition
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.LocaleList
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.eknm.oleksiikolotylo.pocyr.R
import com.eknm.oleksiikolotylo.pocyr.bookmarks.Bookmark
import com.eknm.oleksiikolotylo.pocyr.databinding.FragmentTranslationBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

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

    private val navController get() = binding.root.findNavController()

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
        textTranslateInput.imeHintLocales = LocaleList(Locale("pl"))
        textTranslateInput.doOnTextChanged { text, _, _, _ ->
            viewModel.textToTranslate = text.toString()
        }
        buttonCopy.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            copyText()
        }

        buttonMakeBookmark.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            viewModel.makeBookmark()
        }
        viewModel.bookmarkStateLiveData.observe(viewLifecycleOwner) { state ->
            buttonMakeBookmark.isEnabled = state == BookmarkState.NotSaved
            when (state) {
                BookmarkState.NotSaved -> R.drawable.ic_bookmark_add
                BookmarkState.Loading -> R.drawable.ic_bookmark_add
                BookmarkState.Saved -> R.drawable.ic_bookmark_filled
            }.let(buttonMakeBookmark::setIconResource)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("bookmark")
            ?.observe(
                viewLifecycleOwner
            ) { bookmark ->
                if (bookmark.isNotEmpty()) {
                    navController.currentBackStackEntry?.savedStateHandle?.set("bookmark", "")
                    textTranslateInput.setText(bookmark)
                }
            }
    }

    private fun copyText() {
        val clip = ClipData.newPlainText("PoCyr translation", translatedTextTextView.text)
        clipboard.setPrimaryClip(clip)
    }
}