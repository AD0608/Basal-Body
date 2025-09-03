package com.basalbody.app.ui.home.fragment

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentHomeBinding
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.visible
import com.basalbody.app.extensions.visibleIfOrGone
import com.basalbody.app.extensions.visibleIfOrInvisible
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.utils.BasalTextView
import com.basalbody.app.utils.Logger
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class HomeFragment :
    BaseFragment<HomeViewModel, FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    var selectedDate: LocalDate? = LocalDate.now()
    val menstruationDays: ArrayList<LocalDate> = arrayListOf(LocalDate.now())
    val intercourseDays: ArrayList<LocalDate> = arrayListOf(LocalDate.now())

    override fun addObserver() {
        super.addObserver()
        /*lifecycleScope.launch {
            delay(5000)
            menstruationDays.add(LocalDate.of(2025, 9, 1))
            intercourseDays.add(LocalDate.of(2025, 9, 2))
            requireActivity().runOnUiThread {
                // Update those specific dates
                binding.weekCalendarView.notifyDateChanged(LocalDate.of(2025, 9, 1))
                binding.weekCalendarView.notifyDateChanged(LocalDate.of(2025, 9, 2))
            }

        }*/
    }
    override fun initSetup() {
        setupCalendar()
        setupUI()
    }

    private fun setupCalendar() {
        binding.apply {
            val currentDate = LocalDate.now()
            val currentMonth = YearMonth.now()
            val startDate = currentMonth.atStartOfMonth() // Adjust as needed
            val endDate = currentMonth.plusMonths(10).atEndOfMonth() // Adjust as needed
            val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
            weekCalendarView.setup(startDate, endDate, firstDayOfWeek)
            weekCalendarView.scrollToWeek(currentDate)

            weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)

                override fun bind(container: DayViewContainer, day: WeekDay) {
                    val date = day.date
                    val tvDate = container.tvDate
                    val tvWeekDay = container.tvWeekDay
                    val imgMenstruation = container.imgMenstruation
                    val imgIntercourse = container.imgIntercourse

                    tvDate.text = date.dayOfMonth.toString()
                    tvWeekDay.text = date.dayOfWeek.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    ) // Mo, Tu, We...
                    val isSelected = selectedDate == date
                    container.view.background = if (isSelected) ContextCompat.getDrawable(
                        container.view.context,
                        R.drawable.bg_selected_week_day
                    ) else null
                    tvDate.setTextColor(if (isSelected) "#DE496E".toColorInt() else "#1E293B".toColorInt())
                    tvWeekDay.setTextColor(if (isSelected) "#DE496E".toColorInt() else "#94A3B8".toColorInt())
                    imgMenstruation.visibleIfOrGone(menstruationDays.contains(date))
                    imgIntercourse.visibleIfOrGone(intercourseDays.contains(date))

                    container.view.setOnClickListener {
                        val old = selectedDate
                        selectedDate = day.date
                        old?.let(weekCalendarView::notifyDateChanged)
                        weekCalendarView.notifyDateChanged(day.date)
                    }
                }
            }

        }
    }

    private fun setupUI() {
        binding.apply {
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
        }
    }

}

class DayViewContainer(view: View) : ViewContainer(view) {
    val tvDate: BasalTextView = view.findViewById(R.id.tvDate)
    val tvWeekDay: BasalTextView = view.findViewById(R.id.tvWeekDay)
    val imgMenstruation: AppCompatImageView = view.findViewById(R.id.imgDayMenstruation)
    val imgIntercourse: AppCompatImageView = view.findViewById(R.id.imgDayIntercourse)
}