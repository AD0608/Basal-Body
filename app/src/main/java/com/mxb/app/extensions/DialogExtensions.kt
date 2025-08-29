package com.mxb.app.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Show Keyboard.
 */
fun DialogFragment.showKeyboard() {
    val view = dialog?.currentFocus
    val inputManager =
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    inputManager?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * Hide Keyboard.
 */
fun DialogFragment.hideKeyboard() {
    val view = dialog?.currentFocus
    val inputManager =
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    inputManager?.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

/**
 * Set custom height of dialog fragment.
 */
fun DialogFragment.setHeightPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentHeight = rect.height() * percent
    dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, percentHeight.toInt())
}

/**
 * Set custom width of dialog fragment.
 */
fun DialogFragment.setWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent
    dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}

/**
 * Set custom height of bottom sheet dialog fragment.
 */
fun BottomSheetDialogFragment.setHeightPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentHeight = rect.height() * percent
    dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, percentHeight.toInt())
}

/**
 * Set custom width of bottom sheet dialog fragment.
 */
fun BottomSheetDialogFragment.setWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent
    dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}
fun DialogFragment.setDialogWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent
    dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}