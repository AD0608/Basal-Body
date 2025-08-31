package com.basalbody.app.common

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.base.BaseBottomSheetDialogFragment
import com.basalbody.app.databinding.CommonBottomSheetDialogBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.onNoSafeClick
import com.basalbody.app.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.KClass

@AndroidEntryPoint
class CommonBottomSheetDialog :
    BaseBottomSheetDialogFragment<CommonViewModel, CommonBottomSheetDialogBinding>(
        CommonBottomSheetDialogBinding::inflate,
        isCancel,
        isPreventBackButton,
        isDraggable
    ) {

    companion object {
        var isDraggable = false
        var isPreventBackButton = false
        var isCancel = false
        fun newInstance(
            activity: FragmentActivity,
            rootView: ViewGroup,
            isPreventBackButton: Boolean = false,
            isCancel: Boolean = false,
            isBackButtonVisible: Boolean = false,
        ) =
            CommonBottomSheetDialog().apply {
                this.mActivity = activity
                this.rootView = rootView
                this.isPreventBackButton = isPreventBackButton
                this.isCancel = isCancel
                this.isBackButtonVisible = isBackButtonVisible
            }
    }

    private var isBackButtonVisible = false
    var onClick: (() -> Unit)? = null
    var onBackClick: (() -> Unit)? = null
    var title = ""
    var description = ""
    var image = 0
    var buttonText = ""


    override val modelClass: KClass<CommonViewModel>
        get() = CommonViewModel::class

    override fun initControls() {
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            if (isBackButtonVisible) {
                btnBack.visible()
            } else {
                btnBack.gone()
            }
            textViewTitle.changeText(title)
            tvDescription.changeText(description)
            btnProceed.changeText(buttonText)
            imgLogo.setImageResource(image)
        }
    }

    override fun setOnClickListener() {
        binding.apply {
            btnBack onNoSafeClick {
                onBackClick?.invoke()
                dismiss()
            }
            btnProceed onNoSafeClick {
                onClick?.invoke()
                dismiss()
            }
        }
    }
}