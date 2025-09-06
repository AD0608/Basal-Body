package com.basalbody.app.extensions

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout


fun Any?.isNull() = this == null

fun Any?.notNull() = this != null

/**
 * Change font of TextInputLayout.
 */
fun TextInputLayout.changeFont(@FontRes font: Int) {
    typeface = ResourcesCompat.getFont(context, font)

}

/*
* Execute block into try...catch
* */
inline fun <T> justTry(tryBlock: () -> T) = try {
    tryBlock()
} catch (e: Throwable) {
    e.printStackTrace()
}

inline fun <T> justTry(tryBlock: () -> T, noinline catchBlock: ((Throwable) -> Unit)? = null): T? {
    return try {
        tryBlock()
    } catch (e: Throwable) {
        catchBlock?.invoke(e)
        null
    }
}

fun changeTextInputLayoutsFont(list: Array<TextInputLayout>) {
    for (element in list) {
//        element.changeFont(R.font.cera_pro_regular)
    }
}

fun changeTextInputLayoutsBoldFont(list: Array<TextInputLayout>) {
    for (element in list) {
//        element.changeFont(R.font.cera_pro_bold)
    }
}

/**
 * Change endIconDrawableTintColor of TextInputLayout.
 */
fun TextInputLayout.changeEndIconDrawableTintColor(@ColorRes color: Int) {
    setEndIconTintList(AppCompatResources.getColorStateList(context, color))
}

/**
 * Change font of AppCompatCheckBox.
 */
fun AppCompatCheckBox.changeFont(@FontRes font: Int) {
    typeface = ResourcesCompat.getFont(context, font)
}

//-------This method us not able to remove background - So use below method for change background-------//
/*fun View.changeBackground(@DrawableRes drawable: Int) {
    background = ResourcesCompat.getDrawable(resources, drawable, null)
}*/

//-------We can remove background with setBackgroundResource() method-------//
//-------Pass 0 for remove background-------//
fun View.changeBackground(@DrawableRes drawable: Int) {
    setBackgroundResource(drawable)
}


fun View.changeBackgroundTint(@ColorRes color: Int) {
    backgroundTintList = ResourcesCompat.getColorStateList(resources, color, null)
}

fun View.changeBackgroundTint(colorHex: String) {
    val colorInt = colorHex.toColorInt()
    backgroundTintList = ColorStateList.valueOf(colorInt)
}

/**
 * Change text of MaterialButton.
 */
fun MaterialButton.changeText(@StringRes string: Int) {
    text = resources.getString(string)
}

fun MaterialButton.changeTextColor(@ColorRes color: Int) {
    setTextColor(ResourcesCompat.getColor(resources, color, null))
}

/**
 * Change background of AppCompact ImageView.
 */
fun AppCompatImageView.changeBackground(@DrawableRes drawable: Int) {
    background = ResourcesCompat.getDrawable(resources, drawable, null)
}

/**
 * Change background tint of AppCompact ImageView.
 */
fun AppCompatImageView.applyColorFilter(@ColorRes color: Int) {
    setColorFilter(ResourcesCompat.getColor(resources, color, null))
}

/**
 * Change image of AppCompact ImageView.
 */
fun AppCompatImageView.changeDrawableImage(@DrawableRes drawable: Int) {
    setImageResource(drawable)
}

fun MaterialButton.changeBackgroundTint(@ColorRes color: Int) {
    backgroundTintList = ResourcesCompat.getColorStateList(resources, color, null)
}

/**
 * Change background of ConstraintLayout.
 */
fun ConstraintLayout.changeBackground(@DrawableRes drawable: Int) {
    background = ResourcesCompat.getDrawable(resources, drawable, null)
}

/**
 * Change background tint of ConstraintLayout.
 */
fun ConstraintLayout.changeBackgroundTint(@ColorRes color: Int) {
    backgroundTintList = ResourcesCompat.getColorStateList(resources, color, null)
}

/**
 * Change background of CardView.
 */
fun CardView.changeBackground(@DrawableRes drawable: Int) {
    background = ResourcesCompat.getDrawable(resources, drawable, null)
}

/**
 * Change font of Tabs in TabLayout.
 */
fun TabLayout.changeFont(@FontRes font: Int) {
    val vg = getChildAt(0) as ViewGroup
    val tabsCount = vg.childCount
    for (j in 0 until tabsCount) {
        val vgTab = vg.getChildAt(j) as ViewGroup
        val tabChildCount = vgTab.childCount
        for (i in 0 until tabChildCount) {
            val tabViewChild = vgTab.getChildAt(i)
            if (tabViewChild is TextView) {
                tabViewChild.typeface = ResourcesCompat.getFont(context, font)
            }
        }
    }
}

/**
 * Change margin of Tabs in TabLayout.
 */
fun TabLayout.setMargin(@DimenRes dimen: Int) {
    for (i in 0 until tabCount) {
        val tab = (getChildAt(0) as ViewGroup).getChildAt(i)
        val p = tab.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(
            context.resources.getDimension(dimen).toInt(), 1,
            context.resources.getDimension(dimen).toInt(), 1
        )
        tab.requestLayout()
    }
}

/**
 * Apply Layout to animation, [transition] duration 300 millisecond
 */

private val transition: ChangeBounds by lazy {
    ChangeBounds().apply {
        duration = 300 // Set the duration of the animation (in milliseconds)
    }
}

fun ViewGroup.applyLayoutAnimation() {
    TransitionManager.beginDelayedTransition(
        this,
        transition
    )

}

fun View.applyDateBackground(drawable: Drawable?) {
    visible()
    background = drawable
}