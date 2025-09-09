package com.basalbody.app.ui.common

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.basalbody.app.R
import com.basalbody.app.utils.BasalTextView
import androidx.core.graphics.drawable.toDrawable

fun showLanguageSelectionPopup(mContext: Activity, anchorView: View) {
    val layoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val layout: View = layoutInflater.inflate(R.layout.language_selection_popup, null)

    val popupWindow = PopupWindow(mContext)
    popupWindow.contentView = layout
    popupWindow.width = LinearLayout.LayoutParams.WRAP_CONTENT
    popupWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
    popupWindow.isFocusable = true
    // set transparent background to enable outside touch dismissal
    popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

    val tvEnglish: BasalTextView = layout.findViewById(R.id.tvEnglish)
    val tvChinese: BasalTextView = layout.findViewById(R.id.tvChinese)

    tvEnglish.setOnClickListener {
        // Handle English selection
        popupWindow.dismiss()
    }
    tvChinese.setOnClickListener {
        // Handle Chinese selection
        popupWindow.dismiss()
    }


    popupWindow.showAsDropDown(anchorView)
}