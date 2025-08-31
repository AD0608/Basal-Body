package com.basalbody.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.basalbody.app.R
import com.basalbody.app.extensions.isNull
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.nullSafe
import com.basalbody.app.utils.CommonUtils.dpToPx

@GlideModule
class SampleGlideModule : AppGlideModule()

@SuppressLint("CheckResult")
fun ImageView.loadImageViaGlide(
    value: String? = null,
    drawable: Int? = null,
    bitmap: Bitmap? = null,
    uri: Uri? = null,
    isCircle: Boolean = false,
    showLoading: Boolean = true,
    largeLoading: Boolean = false,
    placeholderRes: Int? = R.drawable.ic_dummy_user,
    roundRadius: Int? = null,
    overrideSize: Int? = null,
    newContext: Context? = null
) {

    val currentContext = newContext ?: context

    if (drawable.notNull() && value.isNull() && bitmap.isNull()) {
        drawable?.let {
            setImageResource(drawable)
        }
    } else {
        val requestOptions = RequestOptions().apply {
            if (isCircle) {
                circleCrop()
            }
            if (roundRadius.nullSafe() > 0) {
                transform(CenterCrop(), RoundedCorners(roundRadius!!))
            }
            if (overrideSize.notNull()) {
                override(dpToPx(context, overrideSize!!))
            }

            error(placeholderRes.nullSafe(R.drawable.ic_dummy_user))

            if (showLoading) {
                val circularProgressDrawable = CircularProgressDrawable(currentContext)
                circularProgressDrawable.strokeWidth = 5f
                if (largeLoading) {
                    circularProgressDrawable.centerRadius = 60f
                } else {
                    circularProgressDrawable.centerRadius = 30f
                }
                circularProgressDrawable.start()
                placeholder(circularProgressDrawable)
            } else {
                scaleType = ImageView.ScaleType.CENTER_CROP
                placeholder(placeholderRes.nullSafe(R.drawable.ic_dummy_user))
            }
        }

        if (!bitmap.isNull()) {
            Glide.with(currentContext)
                .asBitmap()
                .load(bitmap)
                .apply(requestOptions)
                .into(this@loadImageViaGlide)
        } else {
            Glide.with(currentContext)
                .asDrawable()
                .load(
                    when {
                        !value.isNullOrEmpty() -> value
                        !uri.isNull() -> uri
                        else -> {
                        }
                    }
                ).apply(requestOptions).into(this@loadImageViaGlide)
        }
    }
}