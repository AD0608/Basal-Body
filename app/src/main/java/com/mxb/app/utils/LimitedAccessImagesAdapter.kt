package com.mxb.app.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.mxb.app.R
import com.mxb.app.base.BaseAdapterWithViewBinding
import com.mxb.app.databinding.EachRowLimitedAccessImageBinding
import com.mxb.app.databinding.LayoutLoadingItemBinding
import com.mxb.app.extensions.onSafeClick
import com.mxb.app.extensions.visibleIfOrGone
import com.mxb.app.model.common.LimitedAccessImagesDataModel

class LimitedAccessImagesAdapter(
    private var limitedAccessImagesUriList: ArrayList<LimitedAccessImagesDataModel>,
    var maxPickItem: Int,
    private var onClickImageCallback: (Int, LimitedAccessImagesDataModel) -> Unit,
) : BaseAdapterWithViewBinding(limitedAccessImagesUriList) {

    private lateinit var context: Context

    override fun getViewBinding(viewType: Int, parent: ViewGroup): ViewBinding {
        context = parent.context

        return when (viewType) {
            R.layout.each_row_limited_access_image -> {
                EachRowLimitedAccessImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            }

            else -> {
                LayoutLoadingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.each_row_limited_access_image

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (holder.itemViewType == R.layout.each_row_limited_access_image) {
            val binding = holder.binding as EachRowLimitedAccessImageBinding
            val dataBean = limitedAccessImagesUriList[position]
            binding.apply {
                imageViewSelection.visibleIfOrGone(maxPickItem > Constants.DEFAULT_ONE)
                Glide.with(context).load(dataBean.uri).into(imageViewLimitedAccess)
                imageViewSelection.loadImageViaGlide(drawable = if (dataBean.isSelected) R.drawable.ic_check_selected else R.drawable.ic_check_unselected)
                root onSafeClick {
                    onClickImageCallback.invoke(position, dataBean)
                }
            }
        }
    }

}
