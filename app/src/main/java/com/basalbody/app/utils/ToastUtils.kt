package com.basalbody.app.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.basalbody.app.R
import com.basalbody.app.extensions.gone


var toast: Toast? = null
fun showSnackBar(
    message: String,
    isStatus: Int,
    context: Context,
    title: String = ""
) {
    if (toast != null) {
        toast?.cancel()
    }
    val layoutInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = layoutInflater.inflate(R.layout.layout_alert, null)

    val image: ImageView = layout.findViewById<View>(R.id.img) as AppCompatImageView
    val text = layout.findViewById<View>(R.id.txtTitle) as AppCompatTextView
    val txtMessage = layout.findViewById<View>(R.id.txtMessage) as AppCompatTextView
    val card = layout.findViewById<CardView>(R.id.alertCard)

    txtMessage.text = message

    when (isStatus) {
        Constants.STATUS_SUCCESSFUL -> {
            text.text = Constants.STATUS_SUCCESS
            image.setImageResource(R.drawable.ic_success_snackbar)
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_green_A700))
        }

        Constants.STATUS_ERROR -> {
            if (title.isEmpty()) {
                text.text = Constants.STATUS_FAILED
            } else {
                text.text = title
            }
            image.setImageResource(R.drawable.ic_failed_snackbar) // TODO : Add App logo
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_red_A700))
        }

        else -> {
            text.text = Constants.STATUS_REQUIRE
            image.setImageResource(R.drawable.ic_failed_snackbar) // TODO : Add App logo
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_red_A700))
        }
    }

    toast = Toast(context)
    toast?.setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP, 0, 0)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.view = layout
    toast?.show()
}


fun showSnackBarInternet(context: Context, message: String = "") {
    if (toast != null) {
        toast?.cancel()
    }
    val layoutInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout = layoutInflater.inflate(R.layout.layout_alert, null)

    val image: ImageView = layout.findViewById<View>(R.id.img) as AppCompatImageView
    val text = layout.findViewById<View>(R.id.txtTitle) as AppCompatTextView
    val txtMessage = layout.findViewById<View>(R.id.txtMessage) as AppCompatTextView
    val card = layout.findViewById<CardView>(R.id.alertCard)

    text.gone()
    if (message.isEmpty()) {
        txtMessage.text = context.resources.getString(R.string.message_no_internet_found)
    } else {
        txtMessage.text = message
    }
    image.setImageResource(R.drawable.ic_open_eye)
    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))

    toast = Toast(context)
    toast?.setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP, 0, 0)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.view = layout
    toast?.show()
}
