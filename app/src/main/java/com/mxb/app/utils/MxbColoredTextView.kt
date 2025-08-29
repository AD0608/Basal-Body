package com.mxb.app.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.mxb.app.R

class MxbColoredTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val firstColor = context.getColor(R.color.colorSecondary)
    private val secondColor = context.getColor(R.color.colorPrimary)

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MxbColoredTextView)

        val fullText = attributes.getString(R.styleable.MxbColoredTextView_fullText).orEmpty()
        val textStyle = attributes.getInt(R.styleable.MxbColoredTextView_ctv_textStyle, 1)

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
        attributes.recycle()

        if (fullText.isNotEmpty()) {
            setColoredText(fullText, firstColor, secondColor)
        }
    }

    fun setFullText(fullText: String) {
        if (fullText.isNotEmpty()) {
            setColoredText(fullText, firstColor, secondColor)
        }
    }

    private fun setColoredText(fullText: String, firstColor: Int, secondColor: Int) {
        val words = fullText.split(" ", limit = 2) // Split by first space


        if (words.size < 2) {
            val spannable = SpannableString(fullText)
            // Apply color to first word
            spannable.setSpan(
                ForegroundColorSpan(firstColor),
                0, fullText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text = spannable
            return
        }

        val firstPart = words[0]
        val secondPart = words[1]

        val spannable = SpannableString(fullText)

        // Apply color to first word
        spannable.setSpan(
            ForegroundColorSpan(firstColor),
            0, firstPart.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Apply color to remaining text
        spannable.setSpan(
            ForegroundColorSpan(secondColor),
            firstPart.length + 1, fullText.length, // +1 to skip space
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        text = spannable
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
