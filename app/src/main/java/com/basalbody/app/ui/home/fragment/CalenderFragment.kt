package com.basalbody.app.ui.home.fragment

import android.view.View
import androidx.core.view.children
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentCalenderBinding
import com.basalbody.app.databinding.MonthCalenderDayViewBinding
import com.basalbody.app.extensions.changeBackground
import com.basalbody.app.extensions.changeColor
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.onNoSafeClick
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.visible
import com.basalbody.app.ui.home.adapter.TodaysActivityListAdapter
import com.basalbody.app.ui.home.bottomsheet.ActivityDetailsBottomSheetDialog
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.utils.BasalTextView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class CalenderFragment :
    BaseFragment<HomeViewModel, FragmentCalenderBinding>(FragmentCalenderBinding::inflate) {
    override fun getViewBinding(): FragmentCalenderBinding =
        FragmentCalenderBinding.inflate(layoutInflater)

    private val titleFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    var selectedDate: LocalDate? = LocalDate.now()
    private val todaysActivityListAdapter by lazy {
        TodaysActivityListAdapter(requireContext(), arrayListOf("menstruation","intercourse","temperatureTrend"), ::onItemClick)
    }

    override fun initSetup() {
        setupCalendar()
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            rvTodaysActivity.adapter = todaysActivityListAdapter
        }
    }

    override fun listeners() {
        binding.apply {
            calendarView.monthScrollListener = {
                tvMothYear.changeText(titleFormatter.format(it.yearMonth))
            }

            imgPreviousMonth onNoSafeClick {
                calendarView.findFirstVisibleMonth()?.let {
                    calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
                }
            }

            imgNextMonth onNoSafeClick {
                calendarView.findFirstVisibleMonth()?.let {
                    calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
                }
            }
        }
    }

    private fun setupCalendar() {
        binding.apply {
            val daysOfWeek = daysOfWeek()
            weekDayTitleContainer.root.children
                .map { it as BasalTextView }
                .forEachIndexed { index, textView ->
                    val dayOfWeek = daysOfWeek[index]
                    val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    textView.text = title
                }
            val currentMonth = YearMonth.now()
            val startMonth = currentMonth.minusMonths(100) // Adjust as needed
            val endMonth = currentMonth.plusMonths(100) // Adjust as needed
            val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
            calendarView.setup(startMonth, endMonth, firstDayOfWeek)
            calendarView.scrollToMonth(currentMonth)
            calendarView.dayBinder = object : MonthDayBinder<MonthDayViewContainer> {
                // Called only when a new container is needed.
                override fun create(view: View) = MonthDayViewContainer(view)

                // Called every time we need to reuse a container.
                override fun bind(container: MonthDayViewContainer, data: CalendarDay) {
                    val date = data.date

                    container.apply {
                        tvMonthDay.text = date.dayOfMonth.toString()

                        val isSelected = selectedDate == date

                        // Change Color of other moth in current month
                        if (data.position == DayPosition.MonthDate) {
                            tvMonthDay.changeColor(R.color.black)
                        } else {
                            tvMonthDay.changeColor(R.color.colorB3B3B3)
                        }

                        // Change background of selected date
                        if (isSelected && data.position == DayPosition.MonthDate) {
                            tvMonthDay.changeColor(R.color.white)
                            tvMonthDay.changeBackground(R.drawable.bg_selected_month_day)
                            dotMenstruation.visible()
                            dotIntercourse.visible()
                            dotTemperatureTrend.visible()
                        } else {
                            tvMonthDay.background = null
                            dotMenstruation.gone()
                            dotIntercourse.gone()
                            dotTemperatureTrend.gone()
                        }

                        view.setOnClickListener {
                            val old = selectedDate
                            selectedDate = date
                            old?.let(calendarView::notifyDateChanged)
                            calendarView.notifyDateChanged(date)
                        }
                    }
                }
            }
        }
    }

    private fun onItemClick(item : String) {
        ActivityDetailsBottomSheetDialog.newInstance(binding.root, requireActivity(), callBack = {

        }).show(requireActivity().supportFragmentManager, "ActivityDetailsBottomSheetDialog")
    }
}

class MonthDayViewContainer(view: View) : ViewContainer(view) {
    val tvMonthDay = MonthCalenderDayViewBinding.bind(view).tvMonthDay
    val dotMenstruation = MonthCalenderDayViewBinding.bind(view).dotMenstruation
    val dotIntercourse = MonthCalenderDayViewBinding.bind(view).dotIntercourse
    val dotTemperatureTrend = MonthCalenderDayViewBinding.bind(view).dotTemperatureTrend
}