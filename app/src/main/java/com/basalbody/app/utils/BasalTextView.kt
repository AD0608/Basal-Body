package com.basalbody.app.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textview.MaterialTextView
import com.basalbody.app.R

class   BasalTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : MaterialTextView(context, attrs, defStyle) {
    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BasalTextView)
        val textStyle = a.getInt(R.styleable.BasalTextView_textStyle, 0)

        typeface = CommonUtils.getTypeface(context, textStyle)
        val androidAttrs = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.gravity))
        val gravityValue = androidAttrs.getInt(0, Gravity.START)
        androidAttrs.recycle()
        textAlignment = when (gravityValue) {
            Gravity.CENTER, Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL -> View.TEXT_ALIGNMENT_CENTER
            Gravity.START -> View.TEXT_ALIGNMENT_VIEW_START
            Gravity.END -> View.TEXT_ALIGNMENT_VIEW_END
            else -> View.TEXT_ALIGNMENT_VIEW_START
        }
        includeFontPadding = false
        a.recycle()
    }
}