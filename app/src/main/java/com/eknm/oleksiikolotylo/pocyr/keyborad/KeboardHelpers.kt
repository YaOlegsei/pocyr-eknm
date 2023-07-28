package com.eknm.oleksiikolotylo.pocyr.keyborad

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager

fun <V : View> V.doOnWindowFocused(block: V.() -> Unit) {
    viewTreeObserver.addOnWindowFocusChangeListener(object :
        ViewTreeObserver.OnWindowFocusChangeListener {
        override fun onWindowFocusChanged(windowHasFocus: Boolean) {
            if (windowHasFocus) {
                viewTreeObserver.removeOnWindowFocusChangeListener(this)
                block()
            }
        }
    })
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    fun hideKeyboardInternally() {
        context.hideKeyboard(this)
        post { context.hideKeyboard(this) }
    }

    if (hasWindowFocus()) {
        hideKeyboardInternally()
    } else {
        doOnWindowFocused { hideKeyboardInternally() }
    }
}
