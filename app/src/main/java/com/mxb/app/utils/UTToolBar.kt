package com.mxb.app.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.mxb.app.R
import com.mxb.app.databinding.ViewToolbarBinding
import com.mxb.app.extensions.visibleIfOrGone

class UTToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    val binding: ViewToolbarBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_toolbar, this, true)
    private var onBackListener: (() -> Unit?)? = null
    private var menuClickListener: (() -> Unit?)? = null


    fun setToolbarBackListener(listener: () -> (Unit)) {
        this.onBackListener = listener
    }

    fun setMenuOneClickListener(listener: () -> Unit) {
        this.menuClickListener = listener
    }


    fun setMenuOneDrawable(drawable: Drawable?) {
        binding.ivMenu.setImageDrawable(drawable)
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.UTToolBar)
        val title = a.getString(R.styleable.UTToolBar_title)
        val endText = a.getString(R.styleable.UTToolBar_endText)


        val isMenuVisible = a.getBoolean(R.styleable.UTToolBar_isMenuVisible, false)
        if (isMenuVisible) {
            binding.ivMenu.visibility = View.VISIBLE
        } else {
            binding.ivMenu.visibility = View.GONE
        }


        val isTitleVisible = a.getBoolean(R.styleable.UTToolBar_isTitleVisible, false)
        if (isTitleVisible) {
            binding.tvTitle.visibility = View.VISIBLE
        } else {
            binding.tvTitle.visibility = View.GONE
        }
        binding.ivMenu.setImageDrawable(a.getDrawable(R.styleable.UTToolBar_toolbar_menu))

        val isBackArrowVisible = a.getBoolean(R.styleable.UTToolBar_isBackArrowVisible, false)
        binding.tvTitle.text = title
        if (isBackArrowVisible) {
            binding.ivBack.visibility = View.VISIBLE
        } else {
            binding.ivBack.visibility = View.GONE
        }
        binding.textViewEnd.text = endText

        val isEndTextVisible = a.getBoolean(R.styleable.UTToolBar_isEndTextVisible, false)
        binding.textViewEnd.visibleIfOrGone(isEndTextVisible)

        binding.ivBack.setOnClickListener {
            if (onBackListener != null) {
                onBackListener?.invoke()
            } else {
                if (context is Activity) {
                    context.onBackPressed()
                }
            }
        }
        binding.ivMenu.setOnClickListener {
            menuClickListener?.invoke()
        }
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun getTitleTV(): AppCompatTextView = binding.tvTitle

    fun getEndTV(): AppCompatTextView = binding.textViewEnd


    fun setEndText(endText: String) {
        binding.textViewEnd.text = endText
    }

    fun setBackGroundTint(backGroundTint : Int){
        binding.cardView.setBackgroundColor(ContextCompat.getColor(context, backGroundTint))
    }

    fun showEndText(visible: Boolean) {
        binding.textViewEnd.visibleIfOrGone(visible)
    }

    fun showTitle(visible: Boolean){
        binding.tvTitle.visibleIfOrGone(visible)
    }
    fun setStartIconDrawable(drawable: Int) {
        binding.ivBack.setImageResource(drawable)
    }

    fun getLeftArrow(): AppCompatImageView = binding.ivBack
}