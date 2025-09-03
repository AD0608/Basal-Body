package com.basalbody.app.extensions

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.CheckResult
import com.basalbody.app.utils.RippleWaveView
import com.google.android.material.textfield.TextInputLayout
import com.basalbody.app.utils.blur.BlurView
import com.basalbody.app.utils.blur.RenderScriptBlur
import com.basalbody.app.utils.SafeClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

/**
 * Set visibility of the view
 */
fun View.visible() {
    visibility = VISIBLE
}

fun View.invisible() {
    visibility = INVISIBLE
}

fun View.gone() {
    visibility = GONE
}

fun View.isVisible(): Boolean {
    return visibility == VISIBLE
}

//-------View Visible Gone on Boolean Condition-------//
//-------Ex: expandable listview (In - Features of AppDetail, SubCategory Module,..., Compare App Screen (Bottom Info)-------//
fun View.visibleIfOrGone(isShown: Boolean) {
    if (isShown) {
        visible()
    } else {
        gone()
    }
}

fun View.visibleIfOrInvisible(isShown: Boolean) {
    if (isShown) {
        visible()
    } else {
        invisible()
    }
}

//-------View gone Visible on Boolean Condition-------//
fun View.goneIfOrVisible(isGone: Boolean) {
    if (isGone) {
        gone()
    } else {
        visible()
    }
}

//-------if need to visibleView on boolean condition-------//
fun View.visibleIf(isVisible: Boolean) {
    if (isVisible) {
        visible()
    }
}

//-------if need to Hide(Gone)View on boolean condition-------//
fun View.goneIf(isGone: Boolean) {
    if (isGone) {
        gone()
    }
}

/*--------- Set View onSafeClick Listener ----------*/
infix fun View.onSafeClick(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

/*--------- Set View onNoSafeClick Listener ----------*/
infix fun View.onNoSafeClick(onSafeClick: (View) -> Unit) {
    /*val safeClickListener = SafeClickListener(defaultInterval = 0) {
        onSafeClick(it)
    }*/
    setOnClickListener(onSafeClick)
}

/*--------- Set Show Keyboard  ----------*/
fun View.showKeyboard() {
    val imm: InputMethodManager? =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(this, 0)
}

/*--------- Set Hide Keyboard  ----------*/
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

//TextInputLayout End Icon ClickListener
fun TextInputLayout.onEndIconClick(callback: (View) -> Unit) {
    this.setEndIconOnClickListener {
        callback.invoke(it)
    }
}

/**
 * Blur
 */
fun blurRadius(
    llRoot: ViewGroup,
    context: Activity,
    editLayout: BlurView,
    radius: Float
) {
    val windowBackground = context.window.decorView.background

    editLayout.setupWith(llRoot)
        .setFrameClearDrawable(windowBackground)
        .setBlurAlgorithm(RenderScriptBlur(context))
        .setBlurRadius(radius)
}

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow<CharSequence?> {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun ImageView.addRippleWaves(
    color: Int = 0xFF407FFF.toInt(),
    waveCount: Int = 8,
    duration: Long = 3000L
) {
    val parent = this.parent
    if (parent is FrameLayout) {
        // Already inside a FrameLayout → add waves behind
        val waveView = RippleWaveView(this, color, waveCount, duration)
        parent.addView(
            waveView,
            0,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    } else {
        // Wrap inside FrameLayout
        val index = (parent as ViewGroup).indexOfChild(this)
        parent.removeView(this)

        val frame = FrameLayout(context)
        frame.layoutParams = this.layoutParams

        val waveView = RippleWaveView(this, color, waveCount, duration)
        frame.addView(
            waveView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        frame.addView(this)

        parent.addView(frame, index)
    }
}