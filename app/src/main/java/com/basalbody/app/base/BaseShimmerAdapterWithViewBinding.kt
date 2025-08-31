package com.basalbody.app.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.basalbody.app.utils.ShimmerHelper
import com.basalbody.app.utils.TZ_ISO_FORMAT
import com.basalbody.app.utils.createShimmerView
import com.basalbody.app.utils.defaultLocaleForDate
import com.basalbody.app.utils.language.LocaleHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

abstract class BaseShimmerAdapterWithViewBinding<T : Any, VB : ViewBinding>(
    private val context: Context,
    private val shimmerItemCount: Int = 10,
    private val inflateBinding: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val shimmerHelper = ShimmerHelper(context)
    private var isLoadingMore = false
    private val dataList = mutableListOf<T>()
    private val animatedPositions = mutableSetOf<Int>()

    companion object {
        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_DATA = 1
    }

    override fun getItemViewType(position: Int): Int {
        // If we are loading more AND the position is beyond the actual data, show shimmer
        return if (isLoadingMore && position >= dataList.size) VIEW_TYPE_SHIMMER else VIEW_TYPE_DATA
    }

    override fun getItemCount(): Int {
        // If loading more, add shimmerItemCount to the existing data size
        return if (isLoadingMore) dataList.size + shimmerItemCount else dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_SHIMMER) {
            val shimmerLayout = parent.createShimmerView(getItemLayoutRes(), shimmerHelper)
            ShimmerViewHolder(shimmerLayout)
        } else {
            val binding = inflateBinding(inflater, parent, false)
            DataViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ShimmerViewHolder) {
            shimmerHelper.startShimmer(holder.shimmerFrameLayout)
            // Apply animation only once per position for shimmer
            if (!animatedPositions.contains(position)) {
                holder.itemView.translationX = if (LocaleHelper.isRtl(context)) 300f else -300f
                holder.itemView.alpha = 0f
                holder.itemView.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setStartDelay(position * 50L)
                    .setDuration(200)
                    .start()
                animatedPositions.add(position)
            }
        } else if (holder is BaseShimmerAdapterWithViewBinding<*, *>.DataViewHolder) {
            // Ensure we are binding to a valid data item
            if (position < dataList.size) {
                bindItem(holder.binding, dataList[position], position)
            }
        }
    }

    /**
     * Sets the data for the adapter.
     * @param newData The new list of data.
     * @param pageNumber The page number. If 1, clears existing data.
     */
    fun setData(newData: List<T>, pageNumber: Int = 1) {
        if (pageNumber == 1) {
            dataList.clear()
            dataList.addAll(newData)
            // If we were showing shimmer before (e.g., initial load or full refresh)
            // we need to notify a complete change.
            isLoadingMore = false
            notifyDataSetChanged()
        } else {
            // For subsequent pages, remove shimmer and add new data
            val startRemoveIndex = dataList.size
            if (isLoadingMore) {
                isLoadingMore = false
                // First, remove the shimmer items
                notifyItemRangeRemoved(startRemoveIndex, shimmerItemCount)
            }

            val startInsertIndex = dataList.size
            dataList.addAll(newData)
            // Then, insert the new data
            notifyItemRangeInserted(startInsertIndex, newData.size)
        }
    }

    /**
     * Sets the data for the adapter.
     * @param newData The new list of data.
     * @param pageNumber The page number. If 1, clears existing data.
     */
    /*fun setChatData(newData: ArrayList<T>, pageNumber: Int = 1) {
        if (pageNumber == 1) {
            dataList.clear()
            dataList.addAll(newData)
            isLoadingMore = false
            notifyDataSetChanged()
        } else {
            if (isLoadingMore) {
                isLoadingMore = false
                notifyItemRangeRemoved(dataList.size, shimmerItemCount)
            }

            val newHeaders = newData*//*.mapNotNull { newItem ->
                (newItem as? ChatDetails)?.takeIf { it.isHeader }?.headerDate
            }*//*.toSet()

            if (newHeaders.isNotEmpty()) {
                val itemsToRemove = mutableListOf<T>()
                val indicesToRemove = mutableListOf<Int>()

                dataList.forEachIndexed { index, existingItem ->
                    (existingItem as? ChatDetails)?.let {
                        if (it.isHeader && newHeaders.contains(it.headerDate)) {
                            // Collect items and their original indices to remove later
                            itemsToRemove.add(existingItem)
                            indicesToRemove.add(index)
                        }
                    }
                }
                if (itemsToRemove.isNotEmpty()) {
                    indicesToRemove.asReversed().forEach { index ->
                        dataList.removeAt(index)
                        notifyItemRemoved(index)
                    }
                }
            }
            if (newData.isNotEmpty()) {
                dataList.addAll(0, newData)
                notifyItemRangeInserted(0, newData.size)
            }
        }
    }*/

    private fun formatDate(createdAt: String?): String {
        val inputFormat = SimpleDateFormat(TZ_ISO_FORMAT, defaultLocaleForDate).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("dd MMM yyyy", defaultLocaleForDate)
        return try {
            val parsed = inputFormat.parse(createdAt ?: "") ?: Date()
            outputFormat.format(parsed)
        } catch (e: Exception) {
            ""
        }
    }


    /**
     * Adds a single item to the adapter and notifies the insert.
     * @param item The item to add.
     * @param position Optional position to insert at. Defaults to end of list.
     */
    fun addItem(item: T, position: Int = dataList.size) {
        if (position < 0 || position > dataList.size) return
        dataList.add(position, item)
        notifyItemInserted(position)
    }

    fun getItemsSize(): Int = dataList.size - 1
    fun getItems(): List<T> = dataList

    /**
     * Shows shimmer effect for loading more data.
     * Call this when you initiate a new page load.
     * @param pageNumber The page number. If 1, shows shimmer for initial load.
     */
    fun showShimmer(pageNumber: Int = 1) {
        if (pageNumber == 1) {
            // For initial load or full refresh, clear existing data and show shimmer
            dataList.clear()
            isLoadingMore = true
            notifyDataSetChanged()
        } else {
            // For loading more pages, append shimmer to the end
            if (!isLoadingMore) { // Only add if not already loading more
                val startInsertIndex = dataList.size
                isLoadingMore = true
                notifyItemRangeInserted(startInsertIndex, shimmerItemCount)
            }
        }
    }

    /**
     * Clears all data from the adapter.
     * @param pageNumber The page number. If 1, clears all data.
     */
    fun clearData(pageNumber: Int = 1) {
        val oldSize = dataList.size
        dataList.clear()
        isLoadingMore = false
        if (oldSize > 0 || pageNumber == 1) { // Notify only if there was data or it's a first page clear
            notifyDataSetChanged()
        }
    }

    // You might want to add a method to hide shimmer if data loading fails
    fun hideShimmerOnError() {
        if (isLoadingMore) {
            val startRemoveIndex = dataList.size
            isLoadingMore = false
            notifyItemRangeRemoved(startRemoveIndex, shimmerItemCount)
        }
    }

    protected abstract fun bindItem(binding: ViewBinding, item: T, position: Int)
    protected abstract fun getItemLayoutRes(): Int

    inner class DataViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shimmerFrameLayout: ShimmerFrameLayout = itemView as ShimmerFrameLayout
    }
}