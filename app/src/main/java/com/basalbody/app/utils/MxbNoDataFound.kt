package com.basalbody.app.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.basalbody.app.R

class MxbNoDataFound @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val imageView: AppCompatImageView
    private val titleView: MxbTextView
    private val descriptionView: MxbTextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_no_data_found, this, true)

        imageView = findViewById(R.id.imgNoData)
        titleView = findViewById(R.id.tvNoDataTitle)
        descriptionView = findViewById(R.id.tvNoDataDescription)

        attrs?.let {
            context.withStyledAttributes(it, R.styleable.MxbNoDataFound, 0, 0) {
                titleView.text = getString(R.styleable.MxbNoDataFound_mndf_title) ?: ""
                descriptionView.text =
                    getString(R.styleable.MxbNoDataFound_mndf_description) ?: ""
                imageView.setImageResource(
                    getResourceId(
                        R.styleable.MxbNoDataFound_mndf_image,
                        R.drawable.ic_launcher_background
                    )
                )
            }
        }
    }

    fun setTitle(text: String) {
        titleView.text = text
    }

    fun setDescription(text: String) {
        descriptionView.text = text
    }

    fun setImage(@DrawableRes imageRes: Int) {
        imageView.setImageResource(imageRes)
    }
}