package com.basalbody.app.extensions

import android.graphics.drawable.InsetDrawable
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.basalbody.app.utils.CommonUtils.dpToPx
import com.basalbody.app.utils.EnumUtils
import java.util.Locale

/**
 * Disable the Edit Text
 */
fun EditText.disableEditTextFocus() {
    this.isFocusable = false
    this.isLongClickable = false
    this.isCursorVisible = false
    this.isClickable = false
}

/**
 * Enable the Edit Text
 */
fun EditText.enableEditTextFocus() {
    this.isFocusable = true
    this.isLongClickable = true
    this.isCursorVisible = true
    this.isClickable = true
    this.invalidate()
}

/**
 * Set textChange listener
 */
fun AppCompatEditText.applyTextChangeListenerWithHandler(
    stateOfTextChange: EnumUtils.TextChangeState,
    delay: Long,
    textChangeListener: (text: String) -> Unit
) {
    val handler = Handler(Looper.getMainLooper())
    var runnable: Runnable = Runnable { }
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            handler.postDelayed(runnable, delay)
            if (stateOfTextChange == EnumUtils.TextChangeState.AfterTextChange) {
                textChangeListener(s.toString().nullSafe())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (stateOfTextChange == EnumUtils.TextChangeState.BeforeTextChange) {
                textChangeListener(s.toString().nullSafe())
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            handler.removeCallbacks(runnable)
            runnable = Runnable {
                justTry {
                    if (stateOfTextChange == EnumUtils.TextChangeState.OnTextChange) {
                        textChangeListener(s.toString().nullSafe())
                    }
                }
            }
        }
    })
}

/**
 * Set Cap. only in Edit Text
 */
fun EditText.capsEditText() {
//        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
    filters = arrayOf(InputFilter.AllCaps())

}

/**
 * Set Char. only in Edit Text
 */
fun EditText.onlyCharacter() {
    filters = arrayOf(InputFilter { src, start, end, dest, dstart, dend ->
        if (src.toString().matches("[\\p{L}\\p{M}]*".toRegex())) {
            src
        } else src.toString().dropLast(1)
    })

}

/**
 * Set Char. with White space only in Edit Text
 */
fun EditText.onlyCharacterWithSpace() {
    filters = arrayOf(InputFilter { src, start, end, dest, dstart, dend ->
        if (src.toString().matches("[\\p{L}\\p{M}\\p{javaSpaceChar}]*".toRegex())) {
            src
        } else src.toString().dropLast(1)
    })

}

/**
 * Set Char. with White space and Upper Case only in Edit Text
 */
fun EditText.onlyCharacterWithSpaceWithUpperCaps() {
    filters = arrayOf(InputFilter { src, start, end, dest, dstart, dend ->
        if (src.toString().matches("[\\p{L}\\p{M}\\p{javaSpaceChar}]*".toRegex())) {
            src.toString().uppercase(Locale.getDefault())
        } else src.toString().dropLast(1).uppercase(Locale.getDefault())
    })
}

/**
 * Set Char. with Upper Case only in Edit Text
 */
fun EditText.onlyCharacterWithUpperCase() {
    filters = arrayOf(InputFilter { src, start, end, dest, dstart, dend ->
        if (src.toString().matches("[\\p{L}\\p{M}]*".toRegex())) {
            src.toString().uppercase(Locale.getDefault())
        } else src.toString().dropLast(1).uppercase(Locale.getDefault())
    })
}

/**Change background color of EditText*/
/*1. Current focused back = bg_common_current_selected_edittext
* 2. UnFocused and empty edittext = bg_common_edittext
* 3. UnFocused and notEmpty edittext = bg_common_fill_edittext*/
fun AppCompatEditText.changeBackgroundColor() {
    /*applyTextChangeListener(EnumUtils.TextChangeState.AfterTextChange) {
        this.background = if (isFocused){
            ContextCompat.getDrawable(context, R.drawable.bg_common_current_selected_edittext)
        } else {
            if (this.text.toString().isEmpty()) ContextCompat.getDrawable(context, R.drawable.bg_common_edittext) else ContextCompat.getDrawable(context, R.drawable.bg_common_fill_edittext)
        }
    }

    this.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus){
            this.background = ContextCompat.getDrawable(context, R.drawable.bg_common_current_selected_edittext)
        } else {
            this.background = if (this.text.toString().isEmpty()) ContextCompat.getDrawable(context, R.drawable.bg_common_edittext) else ContextCompat.getDrawable(context, R.drawable.bg_common_fill_edittext)
        }
    }*/
}

/**
 * Invoke callback when action done in Edit Text
 */

fun AppCompatEditText.onDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}

/**
 * Set textChange listener
 */
fun AppCompatEditText.applyTextChangeListener(
    stateOfTextChange: EnumUtils.TextChangeState, textChangeListener: (text: String) -> Unit
) {

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (stateOfTextChange == EnumUtils.TextChangeState.AfterTextChange) {
                textChangeListener(s.toString().nullSafe())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (stateOfTextChange == EnumUtils.TextChangeState.BeforeTextChange) {
                textChangeListener(s.toString().nullSafe())
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (stateOfTextChange == EnumUtils.TextChangeState.OnTextChange) {
                textChangeListener(s.toString().nullSafe())
            }
        }
    })
}

fun TextInputLayout.setEndIconInsetDrawable(drawable: Int) {
    val iconDrawable = ContextCompat.getDrawable(context, drawable)
    val insetDrawable = InsetDrawable(
        iconDrawable,
        dpToPx(context, 24),
        0,
        dpToPx(context, 24),
        0
    ) // Adjust padding as needed
    this.endIconDrawable = insetDrawable
}

fun TextInputLayout.setStartIconInsetDrawable(drawable: Int) {
    val iconDrawable = ContextCompat.getDrawable(context, drawable)
    val insetDrawable = InsetDrawable(
        iconDrawable,
        dpToPx(context, 24),
        0,
        0,
        0
    ) // Adjust padding as needed
    this.startIconDrawable = insetDrawable
}
