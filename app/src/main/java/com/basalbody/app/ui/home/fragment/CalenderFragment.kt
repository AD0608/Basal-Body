package com.basalbody.app.ui.home.fragment

import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentCalenderBinding
import com.basalbody.app.ui.home.viewmodel.HomeViewModel

class CalenderFragment :
    BaseFragment<HomeViewModel, FragmentCalenderBinding>(FragmentCalenderBinding::inflate) {
    override fun getViewBinding(): FragmentCalenderBinding =
        FragmentCalenderBinding.inflate(layoutInflater)

    override fun initSetup() {

    }

    override fun listeners() {

    }
}