package com.basalbody.app.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import androidx.core.graphics.toColorInt
import com.basalbody.app.R
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil
import kotlin.math.min

class RoundedBarChart : BarChart {
    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        readRadiusAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        readRadiusAttr(context, attrs)
    }

    private fun readRadiusAttr(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedBarChart, 0, 0)
        try {
            setRadius(a.getDimensionPixelSize(R.styleable.RoundedBarChart_radius, 0))
        } finally {
            a.recycle()
        }
    }

    fun setRadius(radius: Int) {
        renderer = RoundedBarChartRenderer(
            this,
            animator,
            viewPortHandler,
            radius
        )
    }

    private inner class RoundedBarChartRenderer(
        chart: BarDataProvider?,
        animator: ChartAnimator?,
        viewPortHandler: ViewPortHandler?,
        private val mRadius: Int
    ) : BarChartRenderer(chart, animator, viewPortHandler) {
        /**
         * OVERRIDE: Hides the values drawn at the top of the bars.
         * The implementation is intentionally empty.
         */
        override fun drawValues(c: Canvas) {
            // Intentionally empty to prevent drawing data values
        }

        override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
            val barData = mChart.barData

            for (high in indices) {
                val set = barData.getDataSetByIndex(high.dataSetIndex)

                if (set == null || !set.isHighlightEnabled) continue

                val e = set.getEntryForXValue(high.x, high.y)

                if (!isInBoundsX(e, set)) continue

                val trans = mChart.getTransformer(set.axisDependency)

                mHighlightPaint.color = set.highLightColor

                // CHANGE: Set alpha to 0 to make the highlight transparent
                mHighlightPaint.alpha = 0

                val isStack = high.stackIndex >= 0 && e.isStacked

                val y1: Float
                val y2: Float

                if (isStack) {
                    if (mChart.isHighlightFullBarEnabled) {
                        y1 = e.positiveSum
                        y2 = -e.negativeSum
                    } else {
                        val range = e.ranges[high.stackIndex]

                        y1 = range.from
                        y2 = range.to
                    }
                } else {
                    y1 = e.y
                    y2 = 0f
                }

                prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)

                setHighlightDrawPos(high, mBarRect)

                // This part will now draw a transparent rounded rectangle because alpha is 0
                c.drawRoundRect(mBarRect, mRadius.toFloat(), mRadius.toFloat(), mHighlightPaint)
            }
        }

        override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
            val trans = mChart.getTransformer(dataSet.axisDependency)

            mBarBorderPaint.color = dataSet.barBorderColor
            mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

            val drawBorder = dataSet.barBorderWidth > 0f
            val phaseX = mAnimator.phaseX
            val phaseY = mAnimator.phaseY

            // init buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.setBarWidth(mChart.barData.barWidth)
            buffer.feed(dataSet)

            trans.pointValuesToPixel(buffer.buffer)

            val isSingleColor = dataSet.colors.size == 1
            if (isSingleColor) {
                mRenderPaint.color = dataSet.color
            }

            var j = 0
            while (j < buffer.size()) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }
                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

                val left = buffer.buffer[j]
                val top = buffer.buffer[j + 1]
                val right = buffer.buffer[j + 2]
                val bottom = buffer.buffer[j + 3]
                val rectF = RectF(left, top, right, bottom)

                // Gradient shader
                val gradient = LinearGradient(
                    rectF.left, rectF.top, rectF.right, rectF.top, // horizontal
                    "#5EC798".toColorInt(),  // left side (light green)
                    "#17955D".toColorInt(),  // right side (dark green)
                    Shader.TileMode.CLAMP
                )
                mRenderPaint.shader = gradient

                // Rounded top corners only
                val radii = floatArrayOf(
                    mRadius.toFloat(), mRadius.toFloat(),  // top-left
                    mRadius.toFloat(), mRadius.toFloat(),  // top-right
                    0f, 0f,  // bottom-right
                    0f, 0f  // bottom-left
                )

                val path = android.graphics.Path().apply {
                    addRoundRect(rectF, radii, android.graphics.Path.Direction.CW)
                }

                c.drawPath(path, mRenderPaint)

                if (drawBorder) {
                    c.drawPath(path, mBarBorderPaint)
                }

                // reset shader after each bar
                mRenderPaint.shader = null

                j += 4
            }
        }
    }
}
