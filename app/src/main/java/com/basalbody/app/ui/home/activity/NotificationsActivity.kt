package com.basalbody.app.ui.home.activity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityNotificationsBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.home.adapter.NotificationListAdapter
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsActivity : BaseActivity<HomeViewModel, ActivityNotificationsBinding>() {
    override fun getViewBinding(): ActivityNotificationsBinding =
        ActivityNotificationsBinding.inflate(layoutInflater)

    private var notificationList = arrayListOf(
        "", "", "", "", "", "",
        "", "", "", "", "", "",
        "", "", "", "", "", "",
    )

    private val notificationsAdapter by lazy {
        NotificationListAdapter(
            context = this,
            notificationList,
            onItemClick = ::onItemClick,
            onDeleteIconClick = ::onDeleteIconClick
        )
    }

    override fun initSetup() {
        binding.apply {
            ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPaddingRelative(0, systemBars.top, 0, 0)
                rvNotifications.updatePadding(bottom = systemBars.bottom)
                insets
            }
            toolBar.tvTitle.changeText(getString(R.string.label_notification))
            rvNotifications.adapter = notificationsAdapter
        }
    }

    override fun listeners() {
        binding.apply {
            toolBar.ivBack onSafeClick { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun onItemClick(item: String) {
        // Handle item click if needed
    }

    private fun onDeleteIconClick(item: String, position: Int) {
        notificationList.removeAt(position)
        notificationsAdapter.notifyItemRemoved(position)
    }

}