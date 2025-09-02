package com.basalbody.app.ui.home.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.common.CommonBottomSheetDialog
import com.basalbody.app.databinding.ActivityHomeBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.openPlayStore
import com.basalbody.app.extensions.putEnum
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.response.InitData
import com.basalbody.app.ui.common.CommonDialog
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.ui.setting.activity.WebViewActivity
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.EnumUtils
import com.basalbody.app.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {
    override fun getViewBinding(): ActivityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            tvApiResponse.changeText("Press button to call Api")
        }
    }

    override fun listeners() {
        binding.apply {
            btnCallAPI onSafeClick {
                tvApiResponse.changeText("Calling Api")
            }
            btnTermCondition.onSafeClick {
                navigateWebView()
            }
            /*buttonLogout.onSafeClick {
                val logoutDialog =
                    CommonDialog.newInstance(type = EnumUtils.DialogType.LOG_OUT).apply {
                        callback = {
                           //------Here need to call api---//
                        }
                    }
                logoutDialog.show(
                    supportFragmentManager,
                    logoutDialog::class.simpleName
                )
            }
            buttonDeleteAccount.onSafeClick {
                val deleteAccountDialog =
                    CommonDialog.newInstance(type = EnumUtils.DialogType.DELETE_ACCOUNT).apply {
                        callback = {
                            //------Here need to call api---//
                        }
                    }
                deleteAccountDialog.show(
                    supportFragmentManager,
                    deleteAccountDialog::class.simpleName
                )
            }*/
        }
    }

    private fun navigateWebView(){
        startNewActivity(className = WebViewActivity::class.java, bundle = Bundle().apply {
            putEnum(
                Constants.BUNDLE_KEY_WHICH_WEB_VIEW,
                EnumUtils.WebView.TERMS_AND_CONDITIONS
            )
        })
    }
}