package com.eknm.oleksiikolotylo.pocyr

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.eknm.oleksiikolotylo.pocyr.bookmarks.BookmarksFragment
import com.eknm.oleksiikolotylo.pocyr.databinding.ActivityMainBinding
import com.eknm.oleksiikolotylo.pocyr.translate.TranslationFragment
import com.eknm.oleksiikolotylo.pocyr.translate.TranslationFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val buttonQuestion get() = binding.toolbar.buttonQuestion

    private val buttonContextAction get() = binding.toolbar.buttonContextAction
    private val faqView get() = binding.faqView
    private val toolbar get() = binding.toolbar.root
    private val navHostFragment get() = binding.navHostFragment
    private val navController get() = navHostFragment.findNavController()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        )
        buttonQuestion.setOnClickListener {
            showFAQ()
        }
        navHostFragment.setOnClickListener {
            hideFAQ()
        }
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == R.id.bookmarkFragment) {
                buttonContextAction.setIconResource(R.drawable.ic_arrow_back)
                buttonContextAction.setOnClickListener {
                    controller.popBackStack()
                }
            } else {
                buttonContextAction.setIconResource(R.drawable.ic_to_book_marks)
                buttonContextAction.setOnClickListener {
                    val toBookmarks =
                        TranslationFragmentDirections.actionTranslationFragmentToBookmarkFragment()
                    controller.navigate(toBookmarks)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun hideFAQ() {
        faqView
            .animate()
            .translationY(-toolbar.height.toFloat())
            .setDuration(500L)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    faqView.isVisible = false
                }

                override fun onAnimationRepeat(animation: Animator) {}

                override fun onAnimationStart(animation: Animator) {}
            })
    }

    private fun showFAQ() {
        faqView
            .animate()
            .setDuration(500L)
            .translationY(toolbar.height.toFloat())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}

                override fun onAnimationStart(animation: Animator) {
                    faqView.isVisible = true
                }
            })
    }
}