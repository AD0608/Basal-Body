package com.basalbody.app.ui.home.fragment

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.base.FlowInFragment
import com.basalbody.app.databinding.FragmentHomeBinding
import com.basalbody.app.databinding.WeekCalendarDayViewBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.formatCycleDay
import com.basalbody.app.extensions.formatFertileWindow
import com.basalbody.app.extensions.formatTemperatureOneDecimal
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.response.GetInsightsResponse
import com.basalbody.app.model.response.HomeResponse
import com.basalbody.app.ui.home.activity.HomeActivity
import com.basalbody.app.ui.home.activity.NotificationsActivity
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.utils.BasalTextView
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<HomeViewModel, FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    val TAG = "HomeFragment"
    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    var selectedDate: LocalDate? = LocalDate.now()
    val menstruationDays: ArrayList<LocalDate> = arrayListOf(LocalDate.now())
    val intercourseDays: ArrayList<LocalDate> = arrayListOf(LocalDate.now())

    override fun initSetup() {
        setupCalendar()
        setupUI()
        viewModel.callHomeApi()
    }

    override fun addObserver() {
        super.addObserver()

        lifecycleScope.launch {
            viewModel.callHomeApiStateFlow.collect {
                FlowInFragment<BaseResponse<HomeResponse>>(
                    data = it,
                    fragment = this@HomeFragment,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleHomeResponse,
                )
            }
        }
    }

    private fun handleHomeResponse(response: BaseResponse<HomeResponse>?) {
        if (response.notNull() && response?.status == true) {
            Log.e(TAG, "handleHomeResponse()")
            response?.data?.let {

                binding.tvLatestTemp.changeText(formatTemperatureOneDecimal(it.temperature))
                binding.tvDays.changeText(formatCycleDay(it.cycleInfo?.cycleDay))

                val menstruationStatus = it.activityStatus?.menstruation?.status ?: false
                val intercourseStatus = it.activityStatus?.intercourse?.status ?: false

                if (menstruationStatus){
                    binding.btnYesMenstruation.visible()
                    binding.btnNoMenstruation.gone()
                }else{
                    binding.btnYesMenstruation.gone()
                    binding.btnNoMenstruation.visible()
                }

                if (intercourseStatus){
                    binding.btnYesIntercourse.visible()
                    binding.btnNoIntercourse.gone()
                }else{
                    binding.btnYesIntercourse.gone()
                    binding.btnNoIntercourse.visible()
                }
            }
        }
    }

    private fun setupCalendar() {
        binding.apply {
            val currentDate = LocalDate.now()
            val currentMonth = YearMonth.now()
            val startDate = currentMonth.minusMonths(10).atStartOfMonth() // Adjust as needed
            val endDate = currentMonth.atDay(LocalDate.now().dayOfMonth) // Adjust as needed
            val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
            weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
            weekCalendarView.scrollToWeek(currentDate)

            weekCalendarView.dayBinder = object : WeekDayBinder<WeekdayViewContainer> {
                override fun create(view: View) = WeekdayViewContainer(view)

                override fun bind(container: WeekdayViewContainer, day: WeekDay) {
                    val date = day.date
                    container.apply {

                        tvDate.text = date.dayOfMonth.toString()
                        tvWeekDay.text = date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        ) // Mo, Tu, We...
                        val isSelected = selectedDate == date
                        view.background = if (isSelected) ContextCompat.getDrawable(
                            container.view.context,
                            R.drawable.bg_selected_week_day
                        ) else null
                        tvDate.setTextColor(if (isSelected) "#46B74F".toColorInt() else "#1E293B".toColorInt())
                        tvWeekDay.setTextColor(if (isSelected) "#46B74F".toColorInt() else "#94A3B8".toColorInt())

                        view.setOnClickListener {
                            // Only allow selection of dates till today
                            if (day.date.isAfter(LocalDate.now())) return@setOnClickListener
                            val old = selectedDate
                            selectedDate = day.date
                            old?.let(weekCalendarView::notifyDateChanged)
                            weekCalendarView.notifyDateChanged(day.date)
                            updateUI()
                        }
                    }
                }
            }

        }
    }

    private fun setupUI() {
        binding.apply {
            val userName = localDataRepository.getUserDetails()?.user?.fullname.orEmpty()
            tvWelcomeText.changeText("Welcome $userName!")
            grpConnectedDevice.gone()
            btnTapToScan.gone()
            btnTapToScanDevice.visible()
        }
    }

    override fun listeners() {
        binding.apply {
            btnTapToScanDevice.setOnClickListener {
                btnTapToScanDevice.gone()
                grpConnectedDevice.visible()
                btnTapToScan.visible()
            }

            imgNotifications onSafeClick {
                startNewActivity(NotificationsActivity::class.java)
            }

            /*btnYesMenstruation onSafeClick {
                llMenstruationAnswer.background = null
                btnNoMenstruation.gone()
            }

            btnNoMenstruation onSafeClick {
                btnYesMenstruation.gone()
            }

            btnYesIntercourse onSafeClick {
                llIntercourseAnswer.background = null
                btnNoIntercourse.gone()
            }
            btnNoIntercourse onSafeClick {
                btnYesIntercourse.gone()
            }*/

            btnViewAll onSafeClick {
                (activity as HomeActivity).setCalenderTab()
            }
        }
    }

    private fun updateUI() {
        binding.apply {
            // Update any other UI elements based on the selected date
            val isToday = selectedDate == LocalDate.now()
            // Update the label to show "Today" if the selected date is today if it is yesterday then show "Yesterday" else show the date in dd/MM/yyyy format
            tvSelectedDate.text = when {
                isToday -> getString(R.string.label_today)
                selectedDate == LocalDate.now().minusDays(1) -> getString(R.string.label_yesterday)
                else -> selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
            }
        }
    }

}

class WeekdayViewContainer(view: View) : ViewContainer(view) {
    val tvDate = WeekCalendarDayViewBinding.bind(view).tvDate
    val tvWeekDay: BasalTextView = WeekCalendarDayViewBinding.bind(view).tvWeekDay
}