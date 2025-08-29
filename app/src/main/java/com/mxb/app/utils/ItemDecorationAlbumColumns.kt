package com.mxb.app.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mxb.app.utils.language.LocaleHelper

class ItemDecorationAlbumColumns(
    private val gridSpacingPx: Int,
    private val gridSize: Int
) : RecyclerView.ItemDecoration() {

    private var needLeftSpacing = false

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val isRtl = LocaleHelper.isRtl(view.context)

        val frameWidth =
            ((parent.width - gridSpacingPx * (gridSize - 1)).toFloat() / gridSize).toInt()
        val padding = parent.width / gridSize - frameWidth
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition < 0) return

        if (itemPosition < gridSize) {
            outRect.top = 0
        } else {
            outRect.top = gridSpacingPx
        }

        when {
            itemPosition % gridSize == 0 -> {
                setOffsets(outRect, left = 0, right = padding, isRtl = isRtl)
                needLeftSpacing = true
            }

            (itemPosition + 1) % gridSize == 0 -> {
                setOffsets(outRect, left = padding, right = 0, isRtl = isRtl)
                needLeftSpacing = false
            }

            needLeftSpacing -> {
                needLeftSpacing = false
                val left = gridSpacingPx - padding
                val right =
                    if ((itemPosition + 2) % gridSize == 0) gridSpacingPx - padding else gridSpacingPx / 2
                setOffsets(outRect, left = left, right = right, isRtl = isRtl)
            }

            (itemPosition + 2) % gridSize == 0 -> {
                setOffsets(
                    outRect,
                    left = gridSpacingPx / 2,
                    right = gridSpacingPx - padding,
                    isRtl = isRtl
                )
            }

            else -> {
                val half = gridSpacingPx / 2
                setOffsets(outRect, left = half, right = half, isRtl = isRtl)
            }
        }

        outRect.bottom = 0
    }

    private fun setOffsets(outRect: Rect, left: Int, right: Int, isRtl: Boolean) {
        if (isRtl) {
            outRect.left = right
            outRect.right = left
        } else {
            outRect.left = left
            outRect.right = right
        }
    }
}