package com.basalbody.app.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.basalbody.app.utils.language.LocaleHelper

abstract class BaseAdapterWithViewBinding(private val items: ArrayList<out Any?>) :
    RecyclerView.Adapter<BaseAdapterWithViewBinding.ItemViewHolder>() {

    private val animatedPositions = mutableSetOf<Int>()

    protected abstract fun getViewBinding(viewType: Int, parent: ViewGroup): ViewBinding

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(getViewBinding(viewType, parent))

    class ItemViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (!animatedPositions.contains(position)) {
            holder.itemView.translationX =
                if (LocaleHelper.isRtl(holder.binding.root.context)) 300f else -300f
            holder.itemView.alpha = 0f
            holder.itemView.animate()
                .translationX(0f)
                .alpha(1f)
                .setStartDelay(position * 50L)
                .setDuration(200)
                .start()

            animatedPositions.add(position)
        }
    }
}