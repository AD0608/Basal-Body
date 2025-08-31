package com.basalbody.app.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt

class RoundedDashLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val dashPaint = Paint().apply {
        color = "#66010D15".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val dashWidth = 1.dp
    private val dashHeight = 3.dp
    private val dashGap = 2.dp
    private val cornerRadius = 4.dp

    // Reused RectF to avoid allocations
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var top = 0f
        while (top + dashHeight <= height) {
            rect.set(
                (width - dashWidth) / 2f,
                top,
                (width + dashWidth) / 2f,
                top + dashHeight
            )
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, dashPaint)
            top += dashHeight + dashGap
        }
    }

    private val Int.dp: Float get() = this * resources.displayMetrics.density
}