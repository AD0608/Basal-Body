package com.basalbody.app.ui.home.bottomsheet

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.base.BaseBottomSheetDialogFragment
import com.basalbody.app.databinding.ActivityDetailsBottomSheetDialogBinding
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import kotlin.reflect.KClass

class ActivityDetailsBottomSheetDialog : BaseBottomSheetDialogFragment<HomeViewModel, ActivityDetailsBottomSheetDialogBinding>(
    inflate = ActivityDetailsBottomSheetDialogBinding::inflate,
    isCancel = true,
    isDraggable = true,
    isPreventBackButton = false
) {

    var callBack: (() -> Unit)? = null

    companion object {
        fun newInstance(
            rootView: ViewGroup,
            activity: FragmentActivity,
            callBack: (() -> Unit)
        ) = ActivityDetailsBottomSheetDialog().apply {
            this.rootView = rootView
            this.mActivity = activity
            this.callBack = callBack
        }
    }

    override val modelClass: KClass<HomeViewModel>
        get() = HomeViewModel::class

    override fun initControls() {

    }

    override fun setOnClickListener() {
        binding.apply {
            btnBack onSafeClick {
                callBack?.invoke()
                dismiss()
            }
        }
    }
}