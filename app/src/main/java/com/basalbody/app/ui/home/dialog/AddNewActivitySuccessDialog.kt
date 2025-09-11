package com.basalbody.app.ui.home.dialog

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.base.BaseDialogFragment
import com.basalbody.app.databinding.AddNewActivitySuccessDialogBinding
import com.basalbody.app.extensions.setDialogWidthPercent
import com.basalbody.app.utils.Constants

class AddNewActivitySuccessDialog :
    BaseDialogFragment<AddNewActivitySuccessDialogBinding>(
        inflate = AddNewActivitySuccessDialogBinding::inflate,
        isCancelAble = isCancel,
    ) {

    override fun initControl() {
        setDialogWidthPercent(Constants.DEFAULT_WIDTH_DIALOG)
    }

    companion object {
        var isCancel = false
        fun newInstance(
            isCancel: Boolean = false,
            rootView: ViewGroup,
            activity: FragmentActivity,
        ) = AddNewActivitySuccessDialog().apply {
            Companion.isCancel = isCancel
            this.rootView = rootView
            this.mActivity = activity
        }
    }
}