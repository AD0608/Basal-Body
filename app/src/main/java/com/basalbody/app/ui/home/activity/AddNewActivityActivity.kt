package com.basalbody.app.ui.home.activity

import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityAddNewActivityBinding
import com.basalbody.app.extensions.changeDrawableImage
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onNoSafeClick
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.home.dialog.AddNewActivitySuccessDialog
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewActivityActivity : BaseActivity<HomeViewModel, ActivityAddNewActivityBinding>() {
    override fun getViewBinding(): ActivityAddNewActivityBinding =
        ActivityAddNewActivityBinding.inflate(layoutInflater)

    override fun initSetup() {
        binding.apply {
            toolBar.tvTitle.changeText("Add")
            toolBar.ivBack onSafeClick { onBackPressedDispatcher.onBackPressed() }
        }
    }

    override fun listeners() {
        binding.apply {
            rbMenstruation onNoSafeClick {
                changeSelectedActivity(true)
            }

            rbIntercourse onNoSafeClick {
                changeSelectedActivity(false)
            }

            btnAdd onSafeClick {
                AddNewActivitySuccessDialog.newInstance(
                    isCancel = true,
                    rootView = root,
                    activity = this@AddNewActivityActivity,
                ).show(supportFragmentManager, AddNewActivitySuccessDialog::class.java.simpleName)
            }
        }
    }

    private fun changeSelectedActivity(isMenstruationSelected: Boolean) {
        binding.apply {
            imgRBMenstruation.changeDrawableImage(if (isMenstruationSelected) R.drawable.ic_radio_selected else R.drawable.ic_radio_unselected)
            imgRBIntercourse.changeDrawableImage(if (isMenstruationSelected) R.drawable.ic_radio_unselected else R.drawable.ic_radio_selected)
        }

    }

}