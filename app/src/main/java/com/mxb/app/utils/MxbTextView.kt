package com.mxb.app.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textview.MaterialTextView
import com.mxb.app.R

class MxbTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : MaterialTextView(context, attrs, defStyle) {
    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MxbTextView)
        val textStyle = a.getInt(R.styleable.MxbTextView_textStyle, 0)

        typeface = getTypeface(context, textStyle)
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

    private fun getTypeface(context: Context, textStyle: Int): Typeface? {
        when (textStyle) {
            0 -> {
                return ResourcesCompat.getFont(context, R.font.just_sans_regular)
            }

            1 -> {
                return ResourcesCompat.getFont(context, R.font.just_sans_bold)
            }

            2 -> {
                return ResourcesCompat.getFont(context, R.font.just_sans_medium)
            }

            3 -> {
                return ResourcesCompat.getFont(context, R.font.just_sans_semi_bold)
            }

            4 -> {
                return ResourcesCompat.getFont(context, R.font.just_sans_extra_bold)
            }

            5 -> {
                return ResourcesCompat.getFont(context, R.font.just_sans_extra_light)
            }

            6 -> {
                return ResourcesCompat.getFont(context, R.font.just_sans_extra_light)
            }
        }
        return ResourcesCompat.getFont(context, R.font.just_sans_regular)
    }
}