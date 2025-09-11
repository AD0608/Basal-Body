package com.basalbody.app.extensions

import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Shader
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.basalbody.app.utils.Constants.EMPTY_STRING
import com.basalbody.app.utils.textdecorator.OnTextClickListener
import com.basalbody.app.utils.textdecorator.TextDecorator


fun TextView.changeText(@StringRes string: Int) {
    text = resources.getString(string)
}

fun TextView.changeText(string: String) {
    text = string
}

fun TextView.setSeparatedText(
    items: List<String>,
    @DrawableRes separatorDrawableRes: Int,
    drawableWidthDp: Int = 2,
    drawableHeightDp: Int = 6
) {
    if (items.isEmpty()) {
        this.text = ""
        return
    }
    val context = this.context
    val spannable = SpannableStringBuilder()

    val widthPx = (drawableWidthDp * context.resources.displayMetrics.density).toInt()
    val heightPx = (drawableHeightDp * context.resources.displayMetrics.density).toInt()

    items.forEachIndexed { index, item ->
        spannable.append(item)

        if (index < items.lastIndex) {
            spannable.append(" ")

            val drawable = ContextCompat.getDrawable(context, separatorDrawableRes)
            drawable?.setBounds(0, 0, widthPx, heightPx)

            val imageSpan = drawable?.let {
                ImageSpan(it, ImageSpan.ALIGN_CENTER)
            }

            spannable.append(" ") // placeholder for image
            imageSpan?.let {
                spannable.setSpan(
                    it,
                    spannable.length - 1,
                    spannable.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            spannable.append(" ")
        }
    }

    this.text = spannable
}

fun TextView.setVerticalGradientText(colors: IntArray, positions: FloatArray? = null) {
    post {
        val height = height.toFloat()
        val shader = LinearGradient(
            0f, 0f, 0f, height,
            colors,
            positions,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        invalidate()
    }
}

fun Button.changeText(string: String) {
    text = string
}

fun Button.removeButtonTint() {
    backgroundTintList = null
}

fun Button.setButtonTint(@ColorRes color: Int) {
    backgroundTintList = ContextCompat.getColorStateList(context, color)
}

fun Button.removeDrawableEnd() {
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
}

fun Button.setDrawableEnd(@DrawableRes drawableEnd: Int) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawableEnd, 0)
}

fun Button.setDrawableEndWithColor(@DrawableRes drawableEnd: Int, @ColorRes color: Int) {
    val drawable = ResourcesCompat.getDrawable(resources, drawableEnd, null)
    drawable?.colorFilter = PorterDuffColorFilter(
        ContextCompat.getColor(context, color),
        PorterDuff.Mode.SRC_IN
    )
    setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
}

fun TextView.changeFont(@FontRes font: Int) {
    typeface = ResourcesCompat.getFont(context, font)
}

fun TextView.changeColor(@ColorRes color: Int) {
    setTextColor(ResourcesCompat.getColor(resources, color, null))
}

fun TextView.clearAllText() {
    text = EMPTY_STRING
}

//-------Change textView Drawable color-------//
fun TextView.changeTextViewDrawableColor(@ColorRes color: Int) {
    for (drawable in this.compoundDrawables) {
        if (drawable != null) {
            drawable.colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(this.context, color),
                    PorterDuff.Mode.SRC_IN
                )
        }
    }
}

fun AppCompatTextView.changeBackground(@DrawableRes drawable: Int) {
    background = ResourcesCompat.getDrawable(resources, drawable, null)
}

fun TextView.changeDrawableLeftRight(
    @DrawableRes drawableLeft: Int = 0,
    @DrawableRes drawableRight: Int = 0
) {
    setCompoundDrawablesWithIntrinsicBounds(drawableLeft, 0, drawableRight, 0)
}

fun Button.changeDrawableStart(
    @DrawableRes drawableStart: Int = 0,
) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, 0, 0, 0)
}

fun MaterialButton.changeIcon(@DrawableRes iconStart: Int) {
    icon = ResourcesCompat.getDrawable(resources, iconStart, null)
}

fun MaterialButton.removeIcon() {
    icon = null
}

fun TextView.setColor(@ColorRes res: Int) {
    setTextColor(
        ContextCompat.getColor(
            context,
            res
        )
    )
}

/**
 * Set top drawable to TextView
 */
fun AppCompatTextView.topDrawable(@DrawableRes id: Int) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        null,
        AppCompatResources.getDrawable(context, id),
        null,
        null
    )
}

/**
 * Set bottom drawable to TextView
 */
fun AppCompatTextView.bottomDrawable(@DrawableRes id: Int) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        null,
        null,
        null,
        AppCompatResources.getDrawable(context, id)
    )
}

/**
 * Set start drawable to TextView
 */
fun AppCompatTextView.startDrawable(@DrawableRes id: Int?) {
    val drawable = if (id != null) AppCompatResources.getDrawable(context, id) else null
    setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
}

/**
 * Set Custom TextView using @params
 */
fun AppCompatTextView.setTextDecorator(
    text: String,
    textColor: Int,
    textFont: Int,
    isUnderLineText: Boolean = false,
    allowCallback: Boolean = false,
    callBack: (() -> Unit)? = null
) {
    TextDecorator.decorate(
        this,
        this.text.toString()
    ).makeTextClickable(object : OnTextClickListener {
        override fun onClick(view: View, text: String) {
            if (allowCallback) callBack?.invoke()
        }
    }, isUnderLineText, text)
        .setTextColor(textColor, text)
        .setTypeface(
            textFont,
            text
        )
        .build()
}

/**
 * Set end drawable to TextView
 */
fun AppCompatTextView.endDrawable(@DrawableRes id: Int?) {
    val drawable = if (id != null) AppCompatResources.getDrawable(context, id) else null
    setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
}

/**
 * Remove Drawable of TextView
 */
fun AppCompatTextView.removeTextViewDrawable() {
    setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
}

//-------Change specific text's color of textview-------//
fun AppCompatTextView.setTextDecorate(
    originalText: String,
    colorChangeValue: String,
    color: Int,
    font: Int? = null,
    isUnderLineText: Boolean = false
) {
    val builder = SpannableStringBuilder(originalText)
    val start = originalText.indexOf(colorChangeValue, ignoreCase = true)
    val end = start + colorChangeValue.length
    builder.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, color)),
        start,
        end,
        Spannable.SPAN_INCLUSIVE_INCLUSIVE
    )
    font.withNotNull {
        TextDecorator.decorate(
            this,
            this.text.toString()
        ).setTypeface(it, colorChangeValue).build()
    }
    if (isUnderLineText) paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    text = builder
}