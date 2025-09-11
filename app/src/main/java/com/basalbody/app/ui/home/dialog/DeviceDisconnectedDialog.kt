package com.basalbody.app.ui.home.dialog

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.base.BaseDialogFragment
import com.basalbody.app.databinding.DeviceDisconnectedDialogBinding
import com.basalbody.app.extensions.setDialogWidthPercent
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.EnumUtils

class DeviceDisconnectedDialog :
    BaseDialogFragment<DeviceDisconnectedDialogBinding>(
        inflate = DeviceDisconnectedDialogBinding::inflate,
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
        ) = DeviceDisconnectedDialog().apply {
            Companion.isCancel = isCancel
            this.rootView = rootView
            this.mActivity = activity
        }
    }
}