package com.basalbody.app.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.basalbody.app.utils.ActivityLauncher
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.showSnackBar

/**
 * Start an new activity.
 *
 * @param className The target activity's class.
 * @param isFinish want to finish Current Activity or not
 * @param isClearAllStacks want to clear all activity from stack or not
 * @param bundle Optional data bundle to pass to the new activity.
 * @param transitionEffectBundle With Transition Effect bundle pass.
 */

fun <T> Activity.startNewActivity(
    className: Class<T>,
    isFinish: Boolean = false,
    isClearAllStacks: Boolean = false,
    bundle: Bundle? = null,
    transitionEffectBundle: Bundle? = null
) {
    hideKeyboard()
    val intent = Intent(this, className)
    if (isClearAllStacks) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    bundle?.let {
        intent.putExtras(it)
    }
    startActivity(intent, transitionEffectBundle)
    if (isFinish) {
        finish()
    }
}

/**
 * Start an activity with a launcher, allowing for a result callback.
 *
 * @param context The calling activity.
 * @param className The target activity's class.
 * @param bundle Optional data bundle to pass to the new activity.
 * @param onResult Callback to handle the activity result.
 */
inline fun <T> ActivityLauncher<Intent, ActivityResult>.startActivityWithLauncher(
    context: Activity,
    className: Class<T>,
    bundle: Bundle? = null,
    crossinline onResult: (ActivityResult) -> Unit
) {
    context.hideKeyboard()
    val intent = Intent(context, className).apply {
        bundle?.let {
            putExtras(it)
        }
    }

    launch(intent) {
        onResult(ActivityResult(it.resultCode, it.data))
    }
}


/**
 * Finish the activity with a launcher result, optionally passing a bundle.
 *
 * @param bundle Optional data bundle to pass as the result.
 */
fun Activity.finishActivityWithLauncherResult(
    /*resultCode: Int = RESULT_OK,*/ // If there is any scenario need to change result code
    bundle: Bundle? = null,
) {
    hideKeyboard()
    Intent().apply {
        bundle?.let {
            putExtras(it)
        }
        setResult(AppCompatActivity.RESULT_OK, this)
        finish()
    }
}

/**
 * Finish the activity result, optionally passing a bundle.
 *
 * @param bundle Optional data bundle to pass as the result.
 */
fun AppCompatActivity.finishActivityWithResult(bundle: Bundle) {
    val resultIntent = Intent()
    resultIntent.putExtras(bundle)
    setResult(Activity.RESULT_OK, resultIntent)
    finish()
}

/**
 * Finish the activity result, optionally passing a message.
 *
 * @param message Optional data String to pass as the result.
 */
fun AppCompatActivity.finishActivityWithMessage(message: String) {
    val resultIntent = Intent()
    val b = bundleOf("message" to message)
    resultIntent.putExtras(b)
    setResult(Activity.RESULT_OK, resultIntent)
    finish()
}

/**
 * Replace Fragment in activity
 *
 * @param container
 * @param fragment Fragment Class name
 * @param toBackStack want to add fragment in back of stack or not
 */
fun <F> AppCompatActivity.replaceFragment(
    container: Int,
    fragment: F,
    toBackStack: Boolean
) where F : Fragment {
    supportFragmentManager.inTransaction {
        if (toBackStack) {
            addToBackStack(fragment::class.java.simpleName)
        }
        replace(container, fragment)
    }
}

/**
 * Finish activity and Start new Activity
 *
 * @param newActivity The target activity's class.
 */

inline fun <reified A : Class<*>> AppCompatActivity.finishAndStartNewActivity(newActivity: A) {
    val i = Intent(this, newActivity)
    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(i)
    onBackPressedDispatcher.onBackPressed()
}

/**
 * Share text on WhatsApp.
 */
fun AppCompatActivity.shareWithWhatsApp(message: String, context: Context) {
    try {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.whatsapp")
        startActivity(sendIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        showSnackBar("WhatsApp not installed!", 2, context)
        // Toast.makeText(this, "WhatsApp not installed!", Toast.LENGTH_LONG).show()
    }
}

/**
 * Open Play Store URL.
 */
fun AppCompatActivity.openPlayStore(packageName: String) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$packageName".toUri()
        )
    )
}

/**
 * Open Default Email app.
 */
@SuppressLint("IntentReset")
fun Context.sendEmail(mailId: String) {
    try {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SENDTO
        sendIntent.data = "mailto:".toUri()
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailId))
        startActivity(sendIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        showSnackBar("There is no email client installed.", Constants.STATUS_ERROR, this)
    }
}

/*
* Open Dialpad to call
* */
fun Context.callOnPhoneNumber(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phoneNumber".toUri()
    }
    startActivity(intent)
}

// Hide keyboard in Activity
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

// Show keyboard in Activity
fun Activity.showKeyboard() {
    showKeyboard(currentFocus ?: View(this))
}