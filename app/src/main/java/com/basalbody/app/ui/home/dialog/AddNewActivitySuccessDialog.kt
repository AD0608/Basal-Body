package com.basalbody.app.ui.home.dialog

import android.content.DialogInterface
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

    private var onDismiss : (() -> Unit)? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }

    companion object {
        var isCancel = false
        fun newInstance(
            isCancel: Boolean = false,
            rootView: ViewGroup,
            activity: FragmentActivity,
            onDismiss: (() -> Unit)? = null
        ) = AddNewActivitySuccessDialog().apply {
            Companion.isCancel = isCancel
            this.rootView = rootView
            this.mActivity = activity
            this.onDismiss = onDismiss
        }
    }
}