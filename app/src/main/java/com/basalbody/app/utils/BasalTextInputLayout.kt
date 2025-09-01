package com.basalbody.app.utils

import android.content.Context
import android.graphics.Typeface
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.basalbody.app.R
import com.basalbody.app.extensions.invisible
import com.basalbody.app.extensions.setEndIconInsetDrawable
import com.basalbody.app.extensions.visible
import com.basalbody.app.utils.CommonUtils.dpToPx

class BasalTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) :
    TextInputLayout(context, attrs) {
    private var customTextInputEditText: TextInputEditText? = null
    private var labelTextView: AppCompatTextView? = null
    private var currentHint: String = this.hint.toString()
    private var isPassword = true

    init {
        orientation = VERTICAL
        this.setEndIconTintList(
            null
        )
        this.boxStrokeWidth = 0
        this.boxBackgroundColor = context.getColor(R.color.color_bg_custom_edittext)
        init(context, attrs, this)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?,
        customTextInputLayout: BasalTextInputLayout
    ) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.BasalTextInputLayout
        )
        val inputType = a.getInteger(R.styleable.BasalTextInputLayout_inputType, 0x00000001)
        val imeOptions = a.getInteger(R.styleable.BasalTextInputLayout_imeOptions, 0x00000005)
        val maxLines = a.getInteger(R.styleable.BasalTextInputLayout_maxLines, 1)
        val minLines = a.getInteger(R.styleable.BasalTextInputLayout_minLines, 1)
        val lines = a.getInteger(R.styleable.BasalTextInputLayout_lines, 1)
        val maxLength = a.getInteger(R.styleable.BasalTextInputLayout_maxLength, 50)
        val singleLine = a.getBoolean(R.styleable.BasalTextInputLayout_singleLine, true)
        val lineSpacing = a.getFloat(R.styleable.BasalTextInputLayout_lineSpacingExtra, 2f)
        //-------Text size should be in float or int, ex: 14, 18-------//
        val textSize = a.getFloat(
            R.styleable.BasalTextInputLayout_textSize,
            14f
        ) //-------Default text size is 14-------//
        val fontFamilyStyle = a.getInt(R.styleable.BasalTextInputLayout_fontFamilyStyle, 2)
        val digits = a.getString(R.styleable.BasalTextInputLayout_digits)
        val drawableEnd = a.getResourceId(R.styleable.BasalTextInputLayout_drawableEnd, 0)
        val typeface = getTypeface(context, fontFamilyStyle)

        //-------As of now we are not set padding, because google material textInputLayout & textInputEditText
        // is providing default padding and that is enough for us, if we have to set different padding then we can set it from here-------//
        val padding = a.getDimensionPixelSize(R.styleable.BasalTextInputLayout_padding, 0)
        val paddingHorizontal =
            a.getDimensionPixelSize(R.styleable.BasalTextInputLayout_paddingHorizontal, 0)
        val paddingVertical =
            a.getDimensionPixelSize(R.styleable.BasalTextInputLayout_paddingVertical, 0)
        val paddingTop = a.getDimensionPixelSize(R.styleable.BasalTextInputLayout_paddingTop, 0)
        val paddingBottom =
            a.getDimensionPixelSize(R.styleable.BasalTextInputLayout_paddingBottom, 0)
        val paddingLeft = a.getDimensionPixelSize(R.styleable.BasalTextInputLayout_paddingLeft, 0)
        val paddingRight =
            a.getDimensionPixelSize(R.styleable.BasalTextInputLayout_paddingRight, 0)
        val textColor = a.getColor(
            R.styleable.BasalTextInputLayout_tilTextColor,
            ContextCompat.getColor(context, R.color.color_edittext_text)
        )


        setWillNotDraw(false)
        customTextInputEditText = TextInputEditText(getContext())
        customTextInputEditText?.inputType = inputType
        customTextInputEditText?.imeOptions = imeOptions
        customTextInputEditText?.maxLines = maxLines
        customTextInputEditText?.minLines = minLines
        customTextInputEditText?.setLines(lines)
        customTextInputEditText?.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        customTextInputEditText?.isSingleLine = singleLine
        customTextInputEditText?.setLineSpacing(lineSpacing, 1F)
        customTextInputEditText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        customTextInputEditText?.typeface = typeface
        val androidAttrs = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.gravity))
        val gravityValue = androidAttrs.getInt(0, Gravity.START)
        androidAttrs.recycle()
        customTextInputEditText?.textAlignment = when (gravityValue) {
            Gravity.CENTER, Gravity.CENTER_VERTICAL, Gravity.CENTER_HORIZONTAL -> View.TEXT_ALIGNMENT_CENTER
            Gravity.START -> View.TEXT_ALIGNMENT_VIEW_START
            Gravity.END -> View.TEXT_ALIGNMENT_VIEW_END
            else -> View.TEXT_ALIGNMENT_VIEW_START
        }
        customTextInputEditText?.setTextColor(textColor)
        if (drawableEnd != 0) {
            customTextInputLayout.setEndIconInsetDrawable(drawableEnd)
        }

        //-------Set digits-------//
        val digitsFilter = if (!digits.isNullOrEmpty()) {
            InputFilter { source, _, _, _, _, _ ->
                val regex = digits.toRegex()
                if (source.toString().matches(regex)) {
                    null
                } else {
                    ""
                }
            }
        } else {
            null
        }
        setWillNotDraw(false)
        // Set digits filter if it is not null
        if (digitsFilter != null) {
            customTextInputEditText?.filters = arrayOf(digitsFilter)
        }

        //-------As of now we are not set padding, because google material textInputLayout & textInputEditText
        // is providing default padding and that is enough for us, if we have to set different padding then we can set it from here-------//
        //-------Set padding logic-------//
        if (padding != 0) {
            customTextInputEditText?.setPadding(padding, padding, padding, padding)
        } else if (paddingHorizontal != 0 && paddingVertical != 0) {
            customTextInputEditText?.setPadding(
                paddingHorizontal,
                paddingVertical,
                paddingHorizontal,
                paddingVertical
            )

        } else if (paddingHorizontal != 0) {
            customTextInputEditText?.setPadding(
                paddingHorizontal,
                customTextInputEditText?.paddingTop ?: 0,
                paddingHorizontal,
                customTextInputEditText?.paddingBottom ?: 0
            )
        } else if (paddingVertical != 0) {
            customTextInputEditText?.setPadding(
                customTextInputEditText?.paddingLeft ?: 0,
                paddingVertical,
                customTextInputEditText?.paddingRight ?: 0,
                paddingVertical
            )
        } else if (paddingLeft != 0 || paddingTop != 0 || paddingRight != 0 || paddingBottom != 0) {
            customTextInputEditText?.setPadding(
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom
            )
        } else {
            customTextInputEditText?.setPadding(47, 47, 47, 47)
        }

        // Create Label TextView
        labelTextView = AppCompatTextView(context).apply {
            this.text = currentHint
            this.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            this.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_edittext_label
                )
            ) // Orange color for label
            this.typeface = ResourcesCompat.getFont(context, R.font.geist_medium)
            this.invisible() // Initially hidden
            this.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = dpToPx(context, 8)
                topMargin = dpToPx(context, 2)
            }
        }

        // Add views to the layout
        addView(labelTextView, 0)

        createEditBox(customTextInputEditText!!, customTextInputLayout)

        a.recycle()
    }

    private fun createEditBox(
        customTextInputEditText: TextInputEditText,
        customTextInputLayout: BasalTextInputLayout
    ) {
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//        editText.setPadding(50, 0, 0, 0)
        customTextInputEditText.layoutParams = layoutParams

        //-------For password-------//
        if (customTextInputEditText.inputType == 0x00000081) {
            customTextInputEditText.transformationMethod = CustomPasswordTransformation()
            customTextInputLayout.setEndIconOnClickListener {
                if (customTextInputEditText.text?.isNotEmpty() == true) {
                    if (isPassword) {
                        customTextInputLayout.setEndIconInsetDrawable(R.drawable.ic_password_eye_close)
                        isPassword = false
                        customTextInputLayout.editText?.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        customTextInputEditText.setSelection(customTextInputEditText.text!!.length)
                    } else {
                        customTextInputLayout.setEndIconInsetDrawable(R.drawable.ic_open_eye)
                        isPassword = true
                        customTextInputLayout.editText?.transformationMethod =
                            CustomPasswordTransformation()
                        customTextInputEditText.setSelection(customTextInputEditText.text!!.length)
                    }
                }
            }
        }

        customTextInputEditText.addTextChangedListener {
            if (it.toString().isNotEmpty() && !customTextInputEditText.hasFocus()) {
                this.boxStrokeColor = context.getColor(R.color.color_bg_custom_edittext)
                this.boxStrokeWidth = 0
                this.boxBackgroundColor = context.getColor(R.color.color_bg_custom_edittext)
                this.hint = ""
            }
        }

        customTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                customTextInputLayout.boxStrokeColor =
                    context.getColor(R.color.color_bg_custom_edittext)
                customTextInputLayout.boxStrokeWidth = 1
                customTextInputLayout.boxBackgroundColor =
                    context.getColor(R.color.color_bg_custom_edittext)
                customTextInputLayout.hint = ""
                labelTextView?.visible()
                if (customTextInputEditText.inputType == 0x00000081) {
                    if (customTextInputLayout.isPassword) {
                        customTextInputLayout.setEndIconInsetDrawable(R.drawable.ic_open_eye)
                    } else {
                        customTextInputLayout.setEndIconInsetDrawable(R.drawable.ic_password_eye_close)
                    }
                }
            } else {
                labelTextView?.invisible()
                if (customTextInputEditText.inputType == 0x00000081 && customTextInputEditText.text!!.isNotEmpty()) {
                    if (customTextInputLayout.isPassword) {
                        customTextInputLayout.setEndIconInsetDrawable(R.drawable.ic_open_eye)
                    } else {
                        customTextInputLayout.setEndIconInsetDrawable(R.drawable.ic_password_eye_close)
                    }
                } else if (customTextInputEditText.inputType == 0x00000081) {
                    customTextInputLayout.setEndIconInsetDrawable(R.drawable.ic_password_default)
                }
                if (customTextInputEditText.text!!.isEmpty()) {
                    customTextInputLayout.boxStrokeColor =
                        context.getColor(R.color.color_bg_custom_edittext)
                    customTextInputLayout.boxStrokeWidth = 0
                    customTextInputLayout.boxBackgroundColor =
                        context.getColor(R.color.color_bg_custom_edittext)
                    customTextInputLayout.hint = currentHint
                } else {
                    customTextInputLayout.boxStrokeColor =
                        context.getColor(R.color.color_bg_custom_edittext)
                    customTextInputLayout.boxStrokeWidth = 0
                    customTextInputLayout.boxBackgroundColor =
                        context.getColor(R.color.color_bg_custom_edittext)
                    customTextInputLayout.hint = ""
                }
            }
        }
        addView(customTextInputEditText)
    }

    private fun getTypeface(context: Context, textStyle: Int): Typeface? {
        when (textStyle) {
            0 -> {
                return ResourcesCompat.getFont(context, R.font.geist_regular)
            }

            1 -> {
                return ResourcesCompat.getFont(context, R.font.geist_bold)
            }

            2 -> {
                return ResourcesCompat.getFont(context, R.font.geist_medium)
            }

            3 -> {
                return ResourcesCompat.getFont(context, R.font.geist_semi_bold)
            }

            4 -> {
                return ResourcesCompat.getFont(context, R.font.geist_extra_bold)
            }

            5 -> {
                return ResourcesCompat.getFont(context, R.font.geist_extra_light)
            }

            6 -> {
                return ResourcesCompat.getFont(context, R.font.geist_extra_light)
            }
        }
        return ResourcesCompat.getFont(context, R.font.geist_medium)
    }
}

fun BasalTextInputLayout.setText(text: String?) {
    this.editText?.setText(text?.takeIf { it.isNotEmpty() || it != "null" }.orEmpty())
}

fun BasalTextInputLayout.getText(): String? {
    return this.editText?.text?.toString()
}
