package com.basalbody.app.ui.home.activity

import android.os.Handler
import android.os.Looper
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityHomeBinding
import com.basalbody.app.extensions.notNull
import com.basalbody.app.ui.home.fragment.BluetoothFragment
import com.basalbody.app.ui.home.fragment.CalenderFragment
import com.basalbody.app.ui.home.fragment.HomeFragment
import com.basalbody.app.ui.home.fragment.InsightsFragment
import com.basalbody.app.ui.home.fragment.ProfileFragment
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    override fun getViewBinding(): ActivityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)

    private val fragmentMap = mutableMapOf<Int, Fragment>()

    override fun initSetup() {
        setupUI()
        getDataFromIntent()
    }

    private fun setupUI() {
        binding.apply {
            bottomNavigationView.itemIconTintList = null
            ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPaddingRelative(0,systemBars.top,0,0)
                bottomNavigationView.updatePadding(bottom = systemBars.bottom)
                bottomNavigationView.layoutParams.height = systemBars.bottom + resources.getDimension(R.dimen.dp_80).toInt()
                insets
            }
        }
    }

    override fun listeners() {
        binding.apply {
            bottomNavigationView.setOnItemSelectedListener { item ->
                val fragment = fragmentMap[item.itemId] ?: when (item.itemId) {
                    R.id.itemHome -> HomeFragment()
                    R.id.itemBluetooth -> BluetoothFragment()
                    R.id.itemCalender -> CalenderFragment()
                    R.id.itemInsights -> InsightsFragment()
                    R.id.itemProfile -> ProfileFragment()
                    else -> null
                }
                fragment?.let { loadFragment(it, item.itemId) }
                true
            }
        }
    }

    private fun getDataFromIntent() {
        if (intent.notNull() && intent.extras.notNull()) {
            val userFrom = intent.extras?.getString("User_From")
            if (userFrom == "Profile") {
                loadFragment(ProfileFragment(), R.id.itemProfile)
                binding.bottomNavigationView.selectedItemId = R.id.itemProfile
            } else {
                loadFragment(HomeFragment(), R.id.itemHome)
                binding.bottomNavigationView.selectedItemId = R.id.itemHome
            }
        } else {
            loadFragment(HomeFragment(), R.id.itemHome)
            binding.bottomNavigationView.selectedItemId = R.id.itemHome
        }
    }

    private fun loadFragment(fragment: Fragment, itemId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        fragmentMap.values.forEach { transaction.hide(it) }
        if (!fragmentMap.containsKey(itemId)) {
            Handler(Looper.getMainLooper()).postDelayed({
                transaction.add(R.id.fragmentContainer, fragment)
                fragmentMap[itemId] = fragment
                transaction.commitAllowingStateLoss()
            }, 300)
        } else {
            transaction.show(fragment)
            transaction.commitAllowingStateLoss()
        }
    }
}