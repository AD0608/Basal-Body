package com.basalbody.app.utils

import android.content.Context
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.basalbody.app.R
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onNoSafeClick
import com.basalbody.app.extensions.setEndIconInsetDrawable
import com.basalbody.app.extensions.visible
import com.google.android.material.textfield.TextInputLayout

class BasalTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val labelText : BasalTextView
    private val startDrawableImage : AppCompatImageView
    private val endDrawableImage : AppCompatImageView
    private val editText : AppCompatEditText
    private var isPassword = true

    init {
        LayoutInflater.from(context).inflate(R.layout.basal_text_field, this, true)

        labelText = findViewById(R.id.tvLabel)
        startDrawableImage = findViewById(R.id.imgStartDrawable)
        endDrawableImage = findViewById(R.id.imgEndDrawable)
        editText = findViewById(R.id.edtField)

        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.BasalTextField
        )
        val inputType = a.getInteger(R.styleable.BasalTextField_btf_inputType, 0x00000001)
        val imeOptions = a.getInteger(R.styleable.BasalTextField_btf_imeOptions, 0x00000005)
        val maxLines = a.getInteger(R.styleable.BasalTextField_btf_maxLines, 1)
        val minLines = a.getInteger(R.styleable.BasalTextField_btf_minLines, 1)
        val lines = a.getInteger(R.styleable.BasalTextField_btf_lines, 1)
        val maxLength = a.getInteger(R.styleable.BasalTextField_btf_maxLength, 50)
        val singleLine = a.getBoolean(R.styleable.BasalTextField_btf_singleLine, true)
        val lineSpacing = a.getFloat(R.styleable.BasalTextField_btf_lineSpacingExtra, 2f)
        val label = a.getString(R.styleable.BasalTextField_btf_labelText)
        val digits = a.getString(R.styleable.BasalTextField_btf_digits)
        val drawableEnd = a.getResourceId(R.styleable.BasalTextField_btf_drawableEnd, 0)
        val drawableStart = a.getResourceId(R.styleable.BasalTextField_btf_drawableStart, 0)
        val isPasswordField = a.getBoolean(R.styleable.BasalTextField_btf_isPassword, false)
        val fieldHint = a.getString(R.styleable.BasalTextField_btf_hint)

        labelText.changeText(label ?: "")
        editText.apply {
            this.inputType = inputType
            this.imeOptions = imeOptions
            this.maxLines = maxLines
            this.minLines = minLines
            this.hint = fieldHint
            this.setLines(lines)
            this.setTextCursorDrawable(R.drawable.cursor)
            this.setLineSpacing(lineSpacing, 1F)
            this.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            this.isSingleLine = singleLine
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
            if (digitsFilter != null) {
                this.filters = arrayOf(digitsFilter, InputFilter.LengthFilter(maxLength))
            }
        }
        if (drawableEnd != 0) {
            endDrawableImage.visible()
            endDrawableImage.setImageDrawable(ContextCompat.getDrawable(context, drawableEnd))
        }
        if (drawableStart != 0) {
            startDrawableImage.setImageDrawable(ContextCompat.getDrawable(context, drawableStart))
        }


        if (isPasswordField) {
            endDrawableImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_open_eye))
            editText.transformationMethod =
                CustomPasswordTransformation()
            editText.typeface = CommonUtils.getTypeface(context, 0)
        }

        endDrawableImage onNoSafeClick {
            if (editText.text?.isNotEmpty() == true) {
                if (isPassword) {
                    endDrawableImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_password_eye_close))
                    isPassword = false
                    editText.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    editText.setSelection(editText.text!!.length)
                    editText.typeface = CommonUtils.getTypeface(context, 0)
                } else {
                    endDrawableImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_open_eye))
                    isPassword = true
                    editText.transformationMethod =
                        CustomPasswordTransformation()
                    editText.setSelection(editText.text!!.length)
                    editText.typeface = CommonUtils.getTypeface(context, 0)
                }
            }
        }
    }
}

class CustomPasswordTransformation : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View?): CharSequence {
        return StarPasswordCharSequence(source)
    }

    private class StarPasswordCharSequence(private val source: CharSequence) : CharSequence {
        override val length: Int
            get() = source.length

        override fun get(index: Int): Char {
            return '*' // Replace dot with star
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return StarPasswordCharSequence(source.subSequence(startIndex, endIndex))
        }
    }
}