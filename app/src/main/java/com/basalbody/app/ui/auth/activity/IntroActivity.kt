package com.basalbody.app.ui.auth.activity

import android.content.Intent
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityIntroBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.invisible
import com.basalbody.app.extensions.onNoSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.model.dummy.DummyData
import com.basalbody.app.ui.auth.adapter.IntroPagerAdapter
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : BaseActivity<AuthViewModel, ActivityIntroBinding>() {

    private val TAG = "IntroActivity"
    private lateinit var adapter: IntroPagerAdapter

    override fun getViewBinding(): ActivityIntroBinding = ActivityIntroBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {

        Log.e(TAG, "setupUI()")
        binding.apply {
            adapter = IntroPagerAdapter(DummyData.getIntroData())
            vpIntro.adapter = adapter

            vpIntro.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == adapter.itemCount - 1) {
                        // Last page → change button text & hide skip
                        btnNext.changeText(R.string.btn_get_start)
                        btnSkip.invisible()
                    } else {
                        // Not last page → normal state
                        btnNext.text = getString(R.string.btn_next)
                        btnSkip.visible()
                    }
                }
            })
        }

    }

    override fun listeners() {
        Log.e(TAG, "listeners()")

        binding.apply {
            btnNext.onNoSafeClick {
                val currentItem = vpIntro.currentItem
                if (currentItem < adapter.itemCount - 1) {
                    vpIntro.currentItem = currentItem + 1
                } else {
                    goToHome()
                }
            }

            btnSkip.onNoSafeClick {
                goToHome()
            }
        }
    }

    fun goToHome() {
        Log.e(TAG, "goToHome()")
        startNewActivity(LoginActivity::class.java, isFinish = true)
    }

}