package com.basalbody.app.ui.home.bottomsheet

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.R
import com.basalbody.app.base.BaseBottomSheetDialogFragment
import com.basalbody.app.databinding.ActivityDetailsBottomSheetDialogBinding
import com.basalbody.app.extensions.changeBackground
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.visible
import com.basalbody.app.model.response.CalenderLogs
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import kotlin.reflect.KClass

class ActivityDetailsBottomSheetDialog : BaseBottomSheetDialogFragment<HomeViewModel, ActivityDetailsBottomSheetDialogBinding>(
    inflate = ActivityDetailsBottomSheetDialogBinding::inflate,
    isCancel = true,
    isDraggable = true,
    isPreventBackButton = false
) {

    var callBack: (() -> Unit)? = null
    var log : CalenderLogs? = null

    companion object {
        fun newInstance(
            rootView: ViewGroup,
            activity: FragmentActivity,
            callBack: (() -> Unit),
            log: CalenderLogs
        ) = ActivityDetailsBottomSheetDialog().apply {
            this.rootView = rootView
            this.mActivity = activity
            this.callBack = callBack
            this.log = log
        }
    }

    override val modelClass: KClass<HomeViewModel>
        get() = HomeViewModel::class

    override fun initControls() {
        with(binding) {
            when (log?.type) {
                "MENSTRUATION" -> {
                    tvActivityTitle.changeText(mActivity.getString(R.string.label_menstruation))
                    imgActivityIcon.setImageResource(R.drawable.ic_menstruation)
                }

                "INTERCOURSE" -> {
                    tvActivityTitle.changeText(mActivity.getString(R.string.label_intercourse))
                    imgActivityIcon.setImageResource(R.drawable.ic_intercourse)
                }

                else -> {
                    tvActivityTitle.changeText(mActivity.getString(R.string.label_temperature_trend))
                    imgActivityIcon.setImageResource(R.drawable.ic_intercourse)
                }
            }

            tvActivityTemperature.changeText((log?.temperature?.toString() ?: "0").plus("°"))
            if (log?.notes?.isNotEmpty() == true) {
                tvActivityDescription.visible()
                tvActivityDescription.changeText(log?.notes ?: "")
            } else {
                tvActivityDescription.gone()
            }

            if (log?.status == true) {
                tvActivityStatus.changeText(mActivity.getString(R.string.label_yes))
            } else {
                tvActivityStatus.changeText(mActivity.getString(R.string.label_no))
            }

        }
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