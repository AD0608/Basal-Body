package com.basalbody.app.ui.common

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.base.BaseBottomSheetDialogFragment
import com.basalbody.app.databinding.CommonSuccessBottomSheetDialogBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import kotlin.reflect.KClass

class CommonSuccessBottomSheetDialog :
    BaseBottomSheetDialogFragment<AuthViewModel, CommonSuccessBottomSheetDialogBinding>(
        inflate = CommonSuccessBottomSheetDialogBinding::inflate,
        isCancel = true,
        isDraggable = true,
        isPreventBackButton = false
    ) {

    var callBack: (() -> Unit)? = null
    var title : String = ""
    var description : String = ""
    var btnText : String = ""

    companion object {
        fun newInstance(
            rootView: ViewGroup,
            activity: FragmentActivity,
            isCancel : Boolean = false,
            callBack: (() -> Unit)
        ) = CommonSuccessBottomSheetDialog().apply {
            this.rootView = rootView
            this.mActivity = activity
            this.callBack = callBack
            this.isCancel = isCancel
        }
    }

    override val modelClass: KClass<AuthViewModel>
        get() = AuthViewModel::class

    override fun initControls() {
        binding.apply {
            tvTitle.changeText(title)
            tvMessage.changeText(description)
            btnAction.changeText(btnText)
        }
    }

    override fun setOnClickListener() {
        binding.apply {
            btnAction onSafeClick {
                callBack?.invoke()
                dismiss()
            }
        }
    }
}