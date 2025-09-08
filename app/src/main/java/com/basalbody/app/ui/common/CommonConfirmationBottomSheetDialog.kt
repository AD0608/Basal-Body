package com.basalbody.app.ui.common

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.base.BaseBottomSheetDialogFragment
import com.basalbody.app.databinding.CommonConfirmationBottomSheetDialogBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import kotlin.reflect.KClass

class CommonConfirmationBottomSheetDialog :
    BaseBottomSheetDialogFragment<AuthViewModel, CommonConfirmationBottomSheetDialogBinding>(
        inflate = CommonConfirmationBottomSheetDialogBinding::inflate,
        isCancel = true,
        isDraggable = true,
        isPreventBackButton = false
    ) {

    var callBack: (() -> Unit)? = null
    var title: String = ""
    var description: String = ""
    var positiveBtnText: String = ""

    companion object {
        fun newInstance(
            rootView: ViewGroup,
            activity: FragmentActivity,
            callBack: (() -> Unit)
        ) = CommonConfirmationBottomSheetDialog().apply {
            this.rootView = rootView
            this.mActivity = activity
            this.callBack = callBack
        }
    }

    override val modelClass: KClass<AuthViewModel>
        get() = AuthViewModel::class

    override fun initControls() {
        binding.apply {
            tvTitle.changeText(title)
            tvMessage.changeText(description)
            btnPositive.changeText(positiveBtnText)
        }
    }

    override fun setOnClickListener() {
        binding.apply {
            btnPositive onSafeClick {
                callBack?.invoke()
                dismiss()
            }
            btnCancel onSafeClick {
                dismiss()
            }
        }
    }
}