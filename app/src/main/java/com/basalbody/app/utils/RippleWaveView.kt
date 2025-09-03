package com.basalbody.app.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView

@SuppressLint("ViewConstructor")
class RippleWaveView(
    imageView: ImageView,
    private val waveColor: Int,
    private val waveCount: Int = 5,
    private val duration: Long = 3000
) : View(imageView.context) {

    private val paint = Paint().apply {
        color = waveColor
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    private var progress = 0f

    init {
        startAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val maxRadius = width / 2f

        val space = maxRadius / waveCount
        for (i in 0 until waveCount) {
            val radius = (progress * maxRadius + i * space) % maxRadius
            val alpha = (255 - (radius / maxRadius) * 255).toInt().coerceAtLeast(0)
            paint.alpha = alpha
            canvas.drawCircle(cx, cy, radius, paint)
        }
    }

    private fun startAnimation() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = duration
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }
}