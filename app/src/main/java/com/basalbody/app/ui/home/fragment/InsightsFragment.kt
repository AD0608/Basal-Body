package com.basalbody.app.ui.home.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentInsightsBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.ui.home.viewmodel.HomeViewModel

class InsightsFragment : BaseFragment<HomeViewModel, FragmentInsightsBinding>(
    FragmentInsightsBinding::inflate) {
    val TAG = "InsightsFragment"

    override fun getViewBinding(): FragmentInsightsBinding = FragmentInsightsBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        Log.e(TAG, "setupUI()")
        binding.apply {
            toolBar.tvTitle.changeText(R.string.item_insights)
            toolBar.ivBack.gone()
        }
    }

    override fun listeners() {
        binding.apply {

        }
    }

}