package com.basalbody.app.ui.home.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.basalbody.app.R
import com.basalbody.app.base.BaseAdapterWithViewBinding
import com.basalbody.app.databinding.EachRowNotificationBinding
import com.basalbody.app.extensions.changeBackground
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.utils.SwipeLayout

class NotificationListAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private var onItemClick: ((String) -> Unit)? = null,
    private var onDeleteIconClick : ((String, Int) -> Unit)? = null
) : BaseAdapterWithViewBinding(list) {
    override fun getViewBinding(
        viewType: Int,
        parent: ViewGroup
    ): ViewBinding {
        return EachRowNotificationBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding = holder.binding as EachRowNotificationBinding
        val item = list[position]
        binding.apply {
            // You can bind your data to the views here
            // For example:
            // textView.text = item
            swipeLayout.setOnActionsListener(object : SwipeLayout.SwipeActionsListener {
                override fun onOpen(direction: Int, isContinuous: Boolean) {
                    // Handle on open if needed
                    clNotificationMain.changeBackground(R.drawable.bg_swipe_to_delete)
                }

                override fun onClose() {
                    clNotificationMain.changeBackground(R.drawable.bg_notification_item)
                    // Handle on close if needed
                }

                override fun onSwipeStart() {
                    clNotificationMain.changeBackground(R.drawable.bg_swipe_to_delete)
                }
            })
            swipeLayoutItem.root onSafeClick {
                swipeLayout.close()
                onDeleteIconClick?.invoke(item, position)
            }
            root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    fun removeAt(position: Int) {
        if (position < 0 || position >= list.size) return
        list.removeAt(position)
        notifyItemRemoved(position)
    }
}