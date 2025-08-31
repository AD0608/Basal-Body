package com.basalbody.app.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.basalbody.app.utils.showSnackBar
import androidx.core.net.toUri

/**
 * Hide keyboard in Fragment
 */
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

/**
 * Show keyboard in Fragment
 */

/**
 * Clear all Fragments from Related that [Fragment]
 */
fun Fragment.clearStack() {
    var count = requireActivity().supportFragmentManager.backStackEntryCount
    while (count > 0) {
        count--
        requireActivity().supportFragmentManager.popBackStack()
    }
}

/**
 * Start an new activity.
 *
 * @param className The target activity's class.
 * @param isFinish want to finish Current Activity or not
 * @param isClearAllStacks want to clear all activity from stack or not
 * @param bundle Optional data bundle to pass to the new activity.
 * @param transitionEffectBundle With Transition Effect bundle pass.
 */

fun <T> Fragment.startNewActivity(
    className: Class<T>,
    isFinish: Boolean = false,
    isClearAllStacks: Boolean = false,
    bundle: Bundle? = null,
    transitionEffectBundle: Bundle? = null
) {
    hideKeyboard()
    val intent = Intent(requireActivity(), className)
    if (isClearAllStacks) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    bundle?.let {
        intent.putExtras(it)
    }
    startActivity(intent, transitionEffectBundle)
    if (isFinish) {
        requireActivity().finish()
    }
}

/**
 * Finish the activity result, optionally passing a bundle.
 *
 * @param activity FragmentActivity to pass as the result.
 * @param bundle  data bundle to pass as the result.
 */

fun Fragment.finishActivityWithResult(activity: FragmentActivity?, bundle: Bundle) {
    val resultIntent = Intent()
    resultIntent.putExtras(bundle)
    activity?.setResult(Activity.RESULT_OK, resultIntent)
    activity?.finish()
}

/**
 * Finish the activity result, optionally passing a message.
 *
 * @param message Optional data String to pass as the result.
 */
fun Fragment.finishActivityWithMessage(message: String) {
    requireActivity().let {
        val resultIntent = Intent()
        val b = bundleOf("message" to message)
        resultIntent.putExtras(b)
        it.setResult(Activity.RESULT_OK, resultIntent)
        it.finish()
    }
}

fun Fragment.showLocationInMap(
    context: Context,
    latitude: Double,
    longitude: Double,
    title: String
) {
    try {
        val gmmIntentUri =
            if (title.notNullAndNotEmpty()) "geo:0,0?q=$latitude,$longitude($title)".toUri() else "geo:0,0?q=$latitude,$longitude".toUri()

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            startActivity(mapIntent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        showSnackBar("There is no map related app", 2, context)
    }
}

/**
 * Open Default Map app and draw path current to given lat,lng.
 */
fun Fragment.showPathInMap(context: Context, latitude: Double, longitude: Double) {
    try {
        val gmmIntentUri = "http://maps.google.com/maps?daddr=$latitude,$longitude".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        startActivity(mapIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        showSnackBar("There is no map related app", 2, context)
    }
}

val Any.tagName: String
    get() {
        return if (this::class.java.simpleName.isEmpty()) {
            "Class"
        } else {
            this::class.java.simpleName
        }
    }

/**
 *   This will replace fragment to container and it's good if you maintain default container id as [R.id.container]
 */

fun FragmentManager.addFragment(
    containerId: Int = 0,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    animate: Boolean = true
) {

    justTry(
        tryBlock = {
            beginTransaction()
                .apply {

                    if (addToBackStack) {
                        addToBackStack(fragment.tagName)
                    }
                    if (animate) {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    }
                    add(containerId, fragment).commit()
                }
        },
        // catch block is optional
        catchBlock = {

        }
    )
}


/**
 *   This will replace fragment to container and it's good if you maintain default container id as [R.id.container]
 */
fun FragmentManager.replaceFragment(
    containerId: Int = 0, // TODO :-  Add Tab Fragment ID
    fragment: Fragment,
    addToBackStack: Boolean = true,
    animate: Boolean = true
) {
    justTry {
        beginTransaction()
            .apply {
                if (addToBackStack) {
                    addToBackStack(fragment.tagName)
                }
                if (animate) {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                }
                replace(containerId, fragment).commit()
            }
    }
}

/**
 *   This will Add fragment to container and it's good if you maintain default container id as [R.id.container]
 */
fun FragmentActivity.addFragment(
    containerId: Int = 0, // TODO :-  Add Tab Fragment ID
    fragment: Fragment,
    addToBackStack: Boolean = true/*false*/,
    animate: Boolean = true,
    bundle: Bundle? = null
) {
    bundle?.let {
        fragment.arguments = it
    }
    supportFragmentManager.addFragment(containerId, fragment, addToBackStack, animate)
}

/**
 *   This will replace fragment to container and it's good if you maintain default container id as [R.id.container]
 */
fun FragmentActivity.replaceFragment(
    containerId: Int = 0, // TODO :-  Add Tab Fragment ID
    fragment: Fragment,
    addToBackStack: Boolean = true,
    animate: Boolean = false,
    bundle: Bundle? = null
) {
    bundle?.let {
        fragment.arguments = it
    }
    supportFragmentManager.replaceFragment(containerId, fragment, addToBackStack, animate)
}

fun Fragment.getDrawable(drawableImage: Int): Drawable? {
    return ContextCompat.getDrawable(this.requireContext(), drawableImage)
}