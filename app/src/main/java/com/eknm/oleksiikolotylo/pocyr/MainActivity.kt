package com.eknm.oleksiikolotylo.pocyr

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.eknm.oleksiikolotylo.pocyr.databinding.ActivityMainBinding
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

    private var isFaqVisible = false

    private val touchInterceptor get() = binding.touchInterceptor
    private val navHostFragment get() = binding.navHostFragment
    private val navController get() = navHostFragment.findNavController()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            LayoutInflater.from(this), null, false
        )
        buttonQuestion.setOnClickListener {
            if (isFaqVisible) {
                hideFAQ()
            } else {
                showFAQ()
            }
        }
        binding.faqText.movementMethod = LinkMovementMethod.getInstance()
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            hideFAQ()
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

    private fun hideFAQ() {
        isFaqVisible = false
        touchInterceptor.setOnTouchListener(null)
        touchInterceptor.isVisible = false
        faqView.animate().translationY(-toolbar.height.toFloat()).setDuration(500L)
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
        isFaqVisible = true
        touchInterceptor.setOnTouchListener { v, event ->
            hideFAQ()
            false
        }
        touchInterceptor.isVisible = true
        faqView.animate().setDuration(500L).translationY(toolbar.height.toFloat())
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