package com.basalbody.app.ui.home.fragment

import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentHomeBinding
import com.basalbody.app.ui.home.viewmodel.HomeViewModel

class HomeFragment :
    BaseFragment<HomeViewModel, FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun initSetup() {

    }

    override fun listeners() {

    }

}