package com.basalbody.app.ui.home.fragment

import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentProfileBinding
import com.basalbody.app.ui.home.viewmodel.HomeViewModel

class ProfileFragment :
    BaseFragment<HomeViewModel, FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    override fun initSetup() {

    }

    override fun listeners() {

    }
}