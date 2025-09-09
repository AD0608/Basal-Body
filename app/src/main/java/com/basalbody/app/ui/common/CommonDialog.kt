package com.basalbody.app.ui.common

import com.basalbody.app.R
import com.basalbody.app.base.BaseDialogFragment
import com.basalbody.app.ui.common.CommonViewModel
import com.basalbody.app.databinding.DialogCommonBinding
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.setDialogWidthPercent
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.EnumUtils
import kotlin.reflect.KClass

class CommonDialog : BaseDialogFragment<CommonViewModel, DialogCommonBinding>(
    inflate = DialogCommonBinding::inflate,
    isCancelAble = isCancel,
) {
    var type = EnumUtils.DialogType.NONE
    var callback: (() -> Unit)? = null
    var callbackNegative: (() -> Unit)? = null
    override val modelClass: KClass<CommonViewModel>
        get() = CommonViewModel::class

    override fun initControl() {
        setDialogWidthPercent(Constants.DEFAULT_WIDTH_DIALOG)
        with(binding) {
            when (type) {

                EnumUtils.DialogType.LOG_OUT -> {
                    textViewLabelTitle.text = getString(R.string.label_logout)
                    textViewLabelDesc.text =
                        getString(R.string.message_are_you_sure_you_want_to_logout)
                    buttonPositive.text = getString(R.string.label_logout)
                    buttonNegative.text = getString(R.string.button_cancel)
                }

                EnumUtils.DialogType.DELETE_ACCOUNT -> {
                    textViewLabelTitle.text = getString(R.string.label_delete_account)
                    textViewLabelDesc.text = getString(R.string.message_delete_account_confirm)
                    buttonPositive.text = getString(R.string.button_delete)
                    buttonNegative.text = getString(R.string.button_cancel)
                }

                EnumUtils.DialogType.NONE -> {
                    textViewLabelTitle.text = getString(R.string.label_delete_account)
                    textViewLabelDesc.text = getString(R.string.message_delete_account_confirm)
                    buttonPositive.text = getString(R.string.button_delete)
                    buttonNegative.text = getString(R.string.button_cancel)
                }

                else -> {
                    // will never execute these types - APP_UPDATE, APP_FORCE_UPDATE, APP_FORCE_UPDATE
                }
            }

            buttonNegative onSafeClick {
                callbackNegative?.invoke()
                dismiss()
            }
            buttonPositive onSafeClick {
                callback?.invoke()
                dismiss()
            }
        }

    }

    companion object {
        var isCancel = false
        fun newInstance(
            type: EnumUtils.DialogType = EnumUtils.DialogType.NONE,
            isCancel: Boolean = false
        ) = CommonDialog().apply {
            this.type = type
            Companion.isCancel = isCancel
        }
    }
}