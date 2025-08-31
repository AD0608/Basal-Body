package com.basalbody.app.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.MaterialShapeDrawable
import com.basalbody.app.R
import com.basalbody.app.utils.language.LocaleHelper

class ShimmerHelper(private val context: Context) {

    private val shimmerColor = context.getColor(R.color.shimmerColor)
    private val shimmer = Shimmer.AlphaHighlightBuilder()
        .setDirection(if (LocaleHelper.isRtl(context)) Shimmer.Direction.RIGHT_TO_LEFT else Shimmer.Direction.LEFT_TO_RIGHT)
        .setDuration(1500).setAutoStart(true).build()

    private val defaultWidth = 80.dpToPx()
    private val defaultHeight = 14.dpToPx()

    fun createShimmerLayout(layoutResId: Int, parent: ViewGroup): ShimmerFrameLayout {
        val original = LayoutInflater.from(context).inflate(layoutResId, parent, false)
        return ShimmerFrameLayout(context).apply {
            layoutParams = original.layoutParams
            background = original.background?.mutate()
            backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.colorSecondary3_op40)
            setShimmer(shimmer)
            addView(
                when (original) {
                    is ConstraintLayout -> createSkeletonFromConstraintLayout(original)
                    is LinearLayout -> createLinearLayoutSkeleton(original)
                    is RelativeLayout -> createRelativeLayoutSkeleton(original)
                    is CardView -> createSkeletonFromCardView(original)
                    else -> createSkeletonView(original)
                }
            )
        }
    }

    private fun createSkeletonView(view: View): View {
        if (view is ViewGroup) {
            when (view) {
                is ConstraintLayout -> {
                    val skeletonGroup = createSkeletonFromConstraintLayout(view)
                    applyBackgroundTint(view, skeletonGroup)
                    ensureMeasuredSize(view, skeletonGroup)
                    return skeletonGroup
                }

                is LinearLayout -> {
                    val skeletonGroup = createLinearLayoutSkeleton(view)
                    applyBackgroundTint(view, skeletonGroup)
                    return skeletonGroup
                }

                is RelativeLayout -> {
                    val skeletonGroup = createRelativeLayoutSkeleton(view)
                    applyBackgroundTint(view, skeletonGroup)
                    return skeletonGroup
                }

                is CardView -> {
                    val skeletonGroup = createSkeletonFromCardView(view)
                    applyBackgroundTint(view, skeletonGroup)
                    return skeletonGroup
                }
            }
        }

        val width = calculateSkeletonDimension(view, isWidth = true)
        val height = calculateSkeletonDimension(view, isWidth = false)

        val layoutParams = when (val originalParams = view.layoutParams) {
            is LinearLayout.LayoutParams -> {
                LinearLayout.LayoutParams(width, height)
            }

            is RelativeLayout.LayoutParams -> {
                RelativeLayout.LayoutParams(width, height)
            }

            is ConstraintLayout.LayoutParams -> {
                ConstraintLayout.LayoutParams(width, height)
            }

            else -> {
                ViewGroup.MarginLayoutParams(width, height).apply {
                    setMargins(
                        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin ?: 0,
                        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0,
                        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin ?: 0,
                        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
                    )
                }
            }
        }

        return View(view.context).apply {
            this.layoutParams = layoutParams
            background = createSkeletonBackground(view)
        }
    }


    private fun applyBackgroundTint(originalView: View, targetView: ViewGroup) {
        if (originalView.background != null) {
            targetView.background = originalView.background.constantState?.newDrawable()?.mutate()
            targetView.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(originalView.context, R.color.colorSecondary3_op60)
            )
        }
    }

    private fun ensureMeasuredSize(originalView: ViewGroup, skeletonGroup: ViewGroup) {
        if ((skeletonGroup.layoutParams.width < 0 && skeletonGroup.layoutParams.height < 0)
            && (originalView.measuredWidth <= 0 || originalView.measuredHeight <= 0)
        ) {
            originalView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            skeletonGroup.layoutParams = ViewGroup.LayoutParams(
                originalView.measuredWidth,
                originalView.measuredHeight
            )
        }
    }

    private fun calculateSkeletonDimension(view: View, isWidth: Boolean): Int {
        val size = if (isWidth) view.layoutParams?.width else view.layoutParams?.height
        if (size != 0 && size != ViewGroup.LayoutParams.WRAP_CONTENT && size != ViewGroup.LayoutParams.MATCH_PARENT) return size
            ?: 0

        return when (view) {
            is ImageView -> {
                if (isWidth) view.drawable?.intrinsicWidth ?: defaultWidth
                else view.drawable?.intrinsicHeight ?: defaultHeight
            }

            is TextView -> {
                if (isWidth) defaultWidth
                else {
                    val lineHeight = view.lineHeight
                    val lines = view.lineCount.coerceAtLeast(1)
                    (lineHeight * lines) + view.paddingTop + view.paddingBottom
                }
            }

            else -> if (isWidth) defaultWidth else defaultHeight
        }
    }

    private fun createSkeletonBackground(view: View): Drawable {
        return when (view) {
            is ShapeableImageView -> MaterialShapeDrawable(view.shapeAppearanceModel).apply {
                setTint(shimmerColor)
            }

            else -> view.background?.mutate()?.apply { setTint(shimmerColor) }
                ?: GradientDrawable().apply {
                    cornerRadius = 8.dpToPx().toFloat()
                    setColor(shimmerColor)
                }
        }
    }

    private fun createSkeletonFromConstraintLayout(originalLayout: ConstraintLayout): ConstraintLayout {
        val skeletonLayout = ConstraintLayout(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                originalLayout.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT,
                originalLayout.layoutParams?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT
            )
            id = originalLayout.id.takeIf { it != View.NO_ID } ?: View.generateViewId()
            setPaddingRelative(
                originalLayout.paddingStart,
                originalLayout.paddingTop,
                originalLayout.paddingEnd,
                originalLayout.paddingBottom
            )
        }

        val idMap = mutableMapOf<Int, View>()

        for (i in 0 until originalLayout.childCount) {
            val child = originalLayout.getChildAt(i).apply {
                if (id == View.NO_ID) id = View.generateViewId()
            }
            val skeletonChild = createSkeletonView(child).apply { id = child.id }
            idMap[child.id] = skeletonChild
        }

        val constraintSet = ConstraintSet()

        idMap.forEach { (id, skeletonView) ->
            val originalView = originalLayout.findViewById<View>(id)
            val origParams = originalView.layoutParams as ConstraintLayout.LayoutParams
            val layoutParams = if (skeletonView.layoutParams is ConstraintLayout.LayoutParams) {
                skeletonView.layoutParams as ConstraintLayout.LayoutParams
            } else {
                ConstraintLayout.LayoutParams(skeletonView.layoutParams)
            }

            constraintSet.constrainWidth(id, layoutParams.width)
            constraintSet.constrainHeight(id, layoutParams.height)

            connectSides(constraintSet, id, origParams)

            adjustMatchConstraints(constraintSet, id, origParams, layoutParams)

            /*Logger.e(
                "AjayEWW-->",
                "skeletonView Id : ${skeletonView.resources.getResourceName(skeletonView.id)}"
            )
            Logger.e("AjayEWW-->", "skeletonView Height : ${skeletonView.layoutParams.height}")
            Logger.e("AjayEWW-->", "skeletonView Width : ${skeletonView.layoutParams.width}")*/
            skeletonLayout.addView(skeletonView)
        }

        constraintSet.applyTo(skeletonLayout)
        return skeletonLayout
    }

    private fun connectSides(cs: ConstraintSet, id: Int, p: ConstraintLayout.LayoutParams) {
        cs.safeConnect(id, ConstraintSet.START, p.startToStart, ConstraintSet.START, p.marginStart)
        cs.safeConnect(id, ConstraintSet.START, p.startToEnd, ConstraintSet.END, p.marginStart)
        cs.safeConnect(id, ConstraintSet.END, p.endToStart, ConstraintSet.START, p.marginEnd)
        cs.safeConnect(id, ConstraintSet.END, p.endToEnd, ConstraintSet.END, p.marginEnd)
        cs.safeConnect(id, ConstraintSet.TOP, p.topToTop, ConstraintSet.TOP, p.topMargin)
        cs.safeConnect(id, ConstraintSet.TOP, p.topToBottom, ConstraintSet.BOTTOM, p.topMargin)
        cs.safeConnect(id, ConstraintSet.BOTTOM, p.bottomToTop, ConstraintSet.TOP, p.bottomMargin)
        cs.safeConnect(
            id, ConstraintSet.BOTTOM, p.bottomToBottom, ConstraintSet.BOTTOM, p.bottomMargin
        )
        cs.safeConnect(id, ConstraintSet.BASELINE, p.baselineToBaseline, ConstraintSet.BASELINE, 0)
    }

    private fun adjustMatchConstraints(
        cs: ConstraintSet,
        id: Int,
        origParams: ConstraintLayout.LayoutParams,
        layoutParams: ConstraintLayout.LayoutParams
    ) {
        if (layoutParams.width == defaultWidth) {
            if ((origParams.startToStart != ConstraintLayout.LayoutParams.UNSET && origParams.endToEnd != ConstraintLayout.LayoutParams.UNSET) ||
                (origParams.startToStart != ConstraintLayout.LayoutParams.UNSET && origParams.endToStart != ConstraintLayout.LayoutParams.UNSET) ||
                (origParams.startToEnd != ConstraintLayout.LayoutParams.UNSET && origParams.endToEnd != ConstraintLayout.LayoutParams.UNSET) ||
                (origParams.startToEnd != ConstraintLayout.LayoutParams.UNSET && origParams.endToStart != ConstraintLayout.LayoutParams.UNSET)
            ) {
                cs.constrainWidth(id, ConstraintSet.MATCH_CONSTRAINT)
            }
        }
        val oriWidth = origParams.width
        if (layoutParams.height == defaultHeight && oriWidth > 0) {
            if ((origParams.topToTop != ConstraintLayout.LayoutParams.UNSET && origParams.bottomToBottom != ConstraintLayout.LayoutParams.UNSET) ||
                (origParams.topToTop != ConstraintLayout.LayoutParams.UNSET && origParams.bottomToTop != ConstraintLayout.LayoutParams.UNSET) ||
                (origParams.topToBottom != ConstraintLayout.LayoutParams.UNSET && origParams.bottomToBottom != ConstraintLayout.LayoutParams.UNSET) ||
                (origParams.topToBottom != ConstraintLayout.LayoutParams.UNSET && origParams.bottomToTop != ConstraintLayout.LayoutParams.UNSET)
            ) {
                cs.constrainHeight(id, ConstraintSet.MATCH_CONSTRAINT)
            }
        }
    }

    private fun createLinearLayoutSkeleton(originalLayout: LinearLayout): LinearLayout {
        val skeletonLayout = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                originalLayout.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT,
                originalLayout.layoutParams?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = originalLayout.orientation
            weightSum = originalLayout.weightSum
            id = if (originalLayout.id == View.NO_ID) View.generateViewId() else originalLayout.id
            setPaddingRelative(
                originalLayout.paddingStart,
                originalLayout.paddingTop,
                originalLayout.paddingEnd,
                originalLayout.paddingBottom
            )
        }

        val skeletonViewsMap = mutableMapOf<Int, View>()

        for (i in 0 until originalLayout.childCount) {
            val originalChild = originalLayout.getChildAt(i)

            if (originalChild.id == View.NO_ID) {
                originalChild.id = View.generateViewId()
            }

            val skeletonView = createSkeletonView(originalChild)
            skeletonView.id = originalChild.id

            val originalParams = originalChild.layoutParams
            val skeletonParams = skeletonView.layoutParams

            if (originalParams is LinearLayout.LayoutParams && skeletonParams is LinearLayout.LayoutParams) {
                if (originalParams.width == LinearLayout.LayoutParams.MATCH_PARENT) {
                    skeletonParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                }
                if (originalParams.height == LinearLayout.LayoutParams.MATCH_PARENT) {
                    skeletonParams.height = LinearLayout.LayoutParams.MATCH_PARENT
                }
                skeletonParams.weight = originalParams.weight
                skeletonParams.gravity = originalParams.gravity
                skeletonParams.setMargins(
                    originalParams.leftMargin,
                    originalParams.topMargin,
                    originalParams.rightMargin,
                    originalParams.bottomMargin
                )
                skeletonParams.marginStart = originalLayout.marginStart
                skeletonParams.marginEnd = originalLayout.marginEnd
                skeletonView.layoutParams = skeletonParams
            }

            skeletonViewsMap[skeletonView.id] = skeletonView
            skeletonLayout.addView(skeletonView)
        }
        return skeletonLayout
    }

    private fun createRelativeLayoutSkeleton(originalLayout: RelativeLayout): RelativeLayout {
        originalLayout.layoutDirection =
            if (LocaleHelper.isRtl(context)) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
        val skeletonLayout = RelativeLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                originalLayout.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT,
                originalLayout.layoutParams?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT
            )
            id = if (originalLayout.id == View.NO_ID) View.generateViewId() else originalLayout.id
            setPaddingRelative(
                originalLayout.paddingStart,
                originalLayout.paddingTop,
                originalLayout.paddingEnd,
                originalLayout.paddingBottom
            )
            // Needed for Relative Layout
            layoutDirection = originalLayout.layoutDirection
        }

        val skeletonViewsMap = mutableMapOf<Int, View>()

        for (i in 0 until originalLayout.childCount) {
            val originalChild = originalLayout.getChildAt(i)

            if (originalChild.id == View.NO_ID) {
                originalChild.id = View.generateViewId()
            }

            val skeletonView = createSkeletonView(originalChild).apply {
                id = originalChild.id
            }

            val originalParams = originalChild.layoutParams as? RelativeLayout.LayoutParams
            val skeletonParams = skeletonView.layoutParams as? RelativeLayout.LayoutParams

            if (originalParams is RelativeLayout.LayoutParams && skeletonParams is RelativeLayout.LayoutParams) {
                if (originalParams.width == RelativeLayout.LayoutParams.MATCH_PARENT) {
                    skeletonParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                }
                if (originalParams.height == RelativeLayout.LayoutParams.MATCH_PARENT) {
                    skeletonParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
                }
            }

            originalParams?.let { params ->
                skeletonParams?.setMargins(
                    params.leftMargin,
                    params.topMargin,
                    params.rightMargin,
                    params.bottomMargin
                )
                skeletonParams?.marginStart = params.marginStart
                skeletonParams?.marginEnd = params.marginEnd

                val ruleTypes = params.rules
                for (rule in ruleTypes.indices) {
                    val anchorId = params.getRule(rule)
                    if (anchorId != 0) {
                        if (anchorId == -1) {
                            skeletonParams?.addRule(rule)
                        } else {
                            skeletonParams?.addRule(rule, anchorId)
                        }
                    }
                }
            }
            skeletonView.layoutParams = skeletonParams
            skeletonViewsMap[skeletonView.id] = skeletonView
            Logger.e(
                "AjayEWW-->",
                "skeletonView Id : ${skeletonView.resources.getResourceName(skeletonView.id)}"
            )
            Logger.e("AjayEWW-->", "skeletonView Rules : ${skeletonParams?.rules}")
            Logger.e("AjayEWW-->", "skeletonView Height : ${skeletonView.layoutParams.height}")
            Logger.e("AjayEWW-->", "skeletonView Width : ${skeletonView.layoutParams.width}")
            skeletonLayout.addView(skeletonView)
        }
        return skeletonLayout
    }

    private fun createSkeletonFromCardView(originalLayout: CardView): CardView {
        val skeletonLayout = CardView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                originalLayout.layoutParams?.width ?: ViewGroup.LayoutParams.MATCH_PARENT,
                originalLayout.layoutParams?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT
            )
            id = if (originalLayout.id == View.NO_ID) View.generateViewId() else originalLayout.id
            setPaddingRelative(
                originalLayout.paddingStart,
                originalLayout.paddingTop,
                originalLayout.paddingEnd,
                originalLayout.paddingBottom
            )
            elevation = originalLayout.elevation
            cardElevation = cardElevation
            radius = originalLayout.radius
            useCompatPadding = originalLayout.useCompatPadding
        }

        val skeletonViewsMap = mutableMapOf<Int, View>()

        for (i in 0 until originalLayout.childCount) {
            val originalChild = originalLayout.getChildAt(i)

            if (originalChild.id == View.NO_ID) {
                originalChild.id = View.generateViewId()
            }

            val skeletonView = createSkeletonView(originalChild)
            skeletonView.id = originalChild.id

            val originalParams = originalChild.layoutParams

            skeletonView.layoutParams = originalParams
            skeletonViewsMap[skeletonView.id] = skeletonView
            skeletonLayout.addView(skeletonView)
        }

        return skeletonLayout
    }

    fun startShimmer(shimmerLayout: ShimmerFrameLayout) = shimmerLayout.startShimmer()

    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density + 0.5f).toInt()
}

fun ViewGroup.createShimmerView(
    layoutResId: Int, shimmerHelper: ShimmerHelper
): ShimmerFrameLayout {
    return shimmerHelper.createShimmerLayout(layoutResId, this)
}

fun ConstraintSet.safeConnect(startID: Int, startSide: Int, endID: Int, endSide: Int, margin: Int) {
    if (startID != ConstraintLayout.LayoutParams.UNSET && endID != ConstraintLayout.LayoutParams.UNSET) {
        connect(startID, startSide, endID, endSide, margin)
    }
}
