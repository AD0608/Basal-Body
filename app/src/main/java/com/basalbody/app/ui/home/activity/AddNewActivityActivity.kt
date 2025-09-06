package com.basalbody.app.ui.home.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityAddNewActivityBinding
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewActivityActivity : BaseActivity<HomeViewModel, ActivityAddNewActivityBinding>() {
    override fun getViewBinding(): ActivityAddNewActivityBinding = ActivityAddNewActivityBinding.inflate(layoutInflater)

    override fun initSetup() {

    }

    override fun listeners() {

    }

}