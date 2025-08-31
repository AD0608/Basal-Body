package com.basalbody.app.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Window
import androidx.core.graphics.drawable.toDrawable
import com.basalbody.app.R

object LoadingDialog {
    private var dialog: Dialog? = null

    fun showLoadDialog(context: Context) {
        dialog?.let {
            if (it.isShowing) {
                try {
                    it.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        dialog = Dialog(context)

        try {
            dialog?.apply {
                window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCancelable(false)
                setContentView(R.layout.dialog_loading)
                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideLoadDialog() {
        try {
            dialog?.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}