package com.basalbody.app.utils.textdecorator

import android.content.res.ColorStateList
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.EmbossMaskFilter
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.BackgroundColorSpan
import android.text.style.BulletSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.MaskFilterSpan
import android.text.style.QuoteSpan
import android.text.style.RelativeSizeSpan
import android.text.style.ScaleXSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

@Suppress("unused")
class TextDecorator private constructor(
    private val textView: TextView,
    private val content: String
) {
    private val decoratedContent: SpannableString = SpannableString(content)
    private var flags: Int
    fun setFlags(flags: Int): TextDecorator {
        this.flags = flags
        return this
    }

    fun underline(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(UnderlineSpan(), start, end, flags)
        return this
    }

    fun underline(vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(UnderlineSpan(), index, index + text.length, flags)
            }
        }
        return this
    }

    fun setTextColor(@ColorRes resColorId: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(textView.context, resColorId)), start, end,
            flags
        )
        return this
    }

    fun setTextColor(@ColorRes resColorId: Int, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            textView.context,
                            resColorId
                        )
                    ), index, index + text.length, flags
                )
            }
        }
        return this
    }

    fun setBackgroundColor(@ColorRes colorResId: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(
            BackgroundColorSpan(
                ContextCompat.getColor(
                    textView.context,
                    colorResId
                )
            ), start, end, 0
        )
        return this
    }

    fun setBackgroundColor(@ColorRes colorResId: Int, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            textView.context,
                            colorResId
                        )
                    ), index, index + text.length, flags
                )
            }
        }
        return this
    }

    fun insertBullet(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(BulletSpan(), start, end, flags)
        return this
    }

    fun insertBullet(gapWidth: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(BulletSpan(gapWidth), start, end, flags)
        return this
    }

    fun insertBullet(
        gapWidth: Int,
        @ColorRes colorResId: Int,
        start: Int,
        end: Int
    ): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(
            BulletSpan(gapWidth, ContextCompat.getColor(textView.context, colorResId)), start, end,
            flags
        )
        return this
    }

    fun makeTextClickable(
        listener: OnTextClickListener,
        start: Int,
        end: Int,
        underlineText: Boolean
    ): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                listener.onClick(view, content.substring(start, end))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = underlineText
            }
        }, start, end, flags)
        textView.movementMethod = LinkMovementMethod.getInstance()
        return this
    }

    fun makeTextClickable(
        listener: OnTextClickListener,
        underlineText: Boolean,
        vararg texts: String
    ): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(object : ClickableSpan() {
                    override fun onClick(view: View) {
                        listener.onClick(view, text)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = underlineText
                        ds.bgColor = Color.TRANSPARENT
                    }
                }, index, index + text.length, flags)
            }
        }
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
        return this
    }

    fun makeTextClickable(clickableSpan: ClickableSpan?, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(clickableSpan, start, end, flags)
        textView.movementMethod = LinkMovementMethod.getInstance()
        return this
    }

    fun makeTextClickable(clickableSpan: ClickableSpan?, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(clickableSpan, index, index + text.length, flags)
            }
        }
        textView.movementMethod = LinkMovementMethod.getInstance()
        return this
    }

    fun insertImage(@DrawableRes resId: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(ImageSpan(textView.context, resId), start, end, flags)
        return this
    }

    fun quote(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(QuoteSpan(), start, end, flags)
        return this
    }

    fun quote(vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(QuoteSpan(), index, index + text.length, flags)
            }
        }
        return this
    }

    fun quote(@ColorRes colorResId: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(
            QuoteSpan(ContextCompat.getColor(textView.context, colorResId)), start, end,
            flags
        )
        return this
    }

    fun quote(@ColorRes colorResId: Int, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    QuoteSpan(
                        ContextCompat.getColor(
                            textView.context,
                            colorResId
                        )
                    ), index, index + text.length, flags
                )
            }
        }
        return this
    }

    fun strikethrough(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(StrikethroughSpan(), start, end, flags)
        return this
    }

    fun strikethrough(vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(StrikethroughSpan(), index, index + text.length, flags)
            }
        }
        return this
    }

    fun setTextStyle(style: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(StyleSpan(style), start, end, flags)
        return this
    }

    fun setTextStyle(style: Int, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(StyleSpan(style), index, index + text.length, flags)
            }
        }
        return this
    }

    fun alignText(alignment: Layout.Alignment?, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(AlignmentSpan.Standard(alignment!!), start, end, flags)
        return this
    }

    fun alignText(alignment: Layout.Alignment?, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    AlignmentSpan.Standard(alignment!!),
                    index,
                    index + text.length,
                    flags
                )
            }
        }
        return this
    }

    fun setSubscript(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(SubscriptSpan(), start, end, flags)
        return this
    }

    fun setSubscript(vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(SubscriptSpan(), index, index + text.length, flags)
            }
        }
        return this
    }

    fun setSuperscript(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(SuperscriptSpan(), start, end, flags)
        return this
    }

    fun setSuperscript(vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(SuperscriptSpan(), index, index + text.length, flags)
            }
        }
        return this
    }

    fun setTypeface(family: String?, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(TypefaceSpan(family), start, end, flags)
        return this
    }

    /**
     * @param texts: String
     * will not work so specify every text individually
     * */
    fun setTypeface(@FontRes font: Int, vararg texts: String): TextDecorator {
        val typeface: Typeface? = ResourcesCompat.getFont(textView.context, font)
        typeface?.let {
            val customTypefaceSpan = CustomTypefaceSpan("", it)
            var index: Int
            for (text in texts) {
                if (content.contains(text)) {
                    index = content.indexOf(text)
                    decoratedContent.setSpan(customTypefaceSpan, index, index + text.length, flags)
                }
            }
        }
        return this
    }

    fun setTypeface(family: String?, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(TypefaceSpan(family), index, index + text.length, flags)
            }
        }
        return this
    }

    fun setTextAppearance(appearance: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(
            TextAppearanceSpan(textView.context, appearance), start, end,
            flags
        )
        return this
    }

    fun setTextAppearance(appearance: Int, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    TextAppearanceSpan(textView.context, appearance),
                    index,
                    index + text.length,
                    flags
                )
            }
        }
        return this
    }

    fun setTextAppearance(appearance: Int, colorList: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(
            TextAppearanceSpan(textView.context, appearance, colorList), start, end,
            flags
        )
        return this
    }

    fun setTextAppearance(appearance: Int, colorList: Int, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    TextAppearanceSpan(
                        textView.context,
                        appearance,
                        colorList
                    ), index, index + text.length, flags
                )
            }
        }
        return this
    }

    fun setTextAppearance(
        family: String?,
        style: Int,
        size: Int,
        color: ColorStateList?,
        linkColor: ColorStateList?,
        start: Int,
        end: Int
    ): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(
            TextAppearanceSpan(family, style, size, color, linkColor), start, end,
            flags
        )
        return this
    }

    fun setTextAppearance(
        family: String?,
        style: Int,
        size: Int,
        color: ColorStateList?,
        linkColor: ColorStateList?,
        vararg texts: String
    ): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    TextAppearanceSpan(family, style, size, color, linkColor),
                    index,
                    index + text.length,
                    flags
                )
            }
        }
        return this
    }

    fun setTextSize(
        size: Int,
        vararg texts: String
    ): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    AbsoluteSizeSpan(size),
                    index,
                    index + text.length,
                    flags
                )
            }
        }
        return this
    }

    fun setAbsoluteSize(size: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(AbsoluteSizeSpan(size), start, end, flags)
        return this
    }

    fun setAbsoluteSize(size: Int, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(AbsoluteSizeSpan(size), index, index + text.length, flags)
            }
        }
        return this
    }

    fun setAbsoluteSize(size: Int, dip: Boolean, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(AbsoluteSizeSpan(size, dip), start, end, flags)
        return this
    }

    fun setAbsoluteSize(size: Int, dip: Boolean, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    AbsoluteSizeSpan(size, dip),
                    index,
                    index + text.length,
                    flags
                )
            }
        }
        return this
    }

    fun setRelativeSize(proportion: Float, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(RelativeSizeSpan(proportion), start, end, flags)
        return this
    }

    fun setRelativeSize(proportion: Float, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    RelativeSizeSpan(proportion),
                    index,
                    index + text.length,
                    flags
                )
            }
        }
        return this
    }

    fun scaleX(proportion: Float, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(ScaleXSpan(proportion), start, end, flags)
        return this
    }

    fun scaleX(proportion: Float, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(ScaleXSpan(proportion), index, index + text.length, flags)
            }
        }
        return this
    }

    fun blur(radius: Float, style: BlurMaskFilter.Blur?, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(MaskFilterSpan(BlurMaskFilter(radius, style)), start, end, flags)
        return this
    }

    fun blur(radius: Float, style: BlurMaskFilter.Blur?, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    MaskFilterSpan(BlurMaskFilter(radius, style)),
                    index,
                    index + text.length,
                    flags
                )
            }
        }
        return this
    }

    @Suppress("DEPRECATION")
    fun emboss(
        direction: FloatArray?,
        ambient: Float,
        specular: Float,
        blurRadius: Float,
        start: Int,
        end: Int
    ): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(
            MaskFilterSpan(EmbossMaskFilter(direction, ambient, specular, blurRadius)), start, end,
            flags
        )
        return this
    }

    @Suppress("DEPRECATION")
    fun emboss(
        direction: FloatArray?,
        ambient: Float,
        specular: Float,
        blurRadius: Float,
        vararg texts: String
    ): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(
                    MaskFilterSpan(
                        EmbossMaskFilter(
                            direction,
                            ambient,
                            specular,
                            blurRadius
                        )
                    ), index, index + text.length, flags
                )
            }
        }
        return this
    }

    fun build() {
        textView.text = decoratedContent
    }

    private fun checkIndexOutOfBoundsException(start: Int, end: Int) {
        when {
            start < 0 -> {
                throw IndexOutOfBoundsException("start is less than 0")
            }

            end > content.length -> {
                throw IndexOutOfBoundsException("end is greater than content length " + content.length)
            }

            start > end -> {
                throw IndexOutOfBoundsException("start is greater than end")
            }
        }
    }

    companion object {
        fun decorate(textView: TextView, content: String): TextDecorator {
            return TextDecorator(textView, content)
        }
    }

    init {
        flags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    }
}