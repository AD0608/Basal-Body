package com.basalbody.app.ui.home.fragment

import android.app.Activity.RESULT_OK
import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.base.FlowInFragment
import com.basalbody.app.databinding.FragmentCalenderBinding
import com.basalbody.app.databinding.MonthCalenderDayViewBinding
import com.basalbody.app.extensions.changeBackground
import com.basalbody.app.extensions.changeColor
import com.basalbody.app.extensions.changeDrawableImage
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onNoSafeClick
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.response.CalenderLogs
import com.basalbody.app.ui.home.activity.AddNewActivityActivity
import com.basalbody.app.ui.home.adapter.TodaysActivityListAdapter
import com.basalbody.app.ui.home.bottomsheet.ActivityDetailsBottomSheetDialog
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.utils.BasalTextView
import com.basalbody.app.utils.startActivityWithLauncher
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class CalenderFragment :
    BaseFragment<HomeViewModel, FragmentCalenderBinding>(FragmentCalenderBinding::inflate) {

    private val titleFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    var selectedDate: LocalDate? = LocalDate.now()
    var logsList : ArrayList<CalenderLogs> = arrayListOf()
    private val todaysActivityListAdapter by lazy {
        TodaysActivityListAdapter(
            requireContext(),
            logsList,
            ::onItemClick
        )
    }
    private val mainResponse: ArrayList<CalenderLogs> = arrayListOf()
    private var logsByDate: Map<LocalDate, List<CalenderLogs>> = emptyMap()
    private var isCalendarInitialized = false

    override fun getViewBinding(): FragmentCalenderBinding =
        FragmentCalenderBinding.inflate(layoutInflater)

    override fun addObserver() {
        lifecycleScope.launch {
            viewModel.callGetCalenderLogsApiStateFlow.collect {
                FlowInFragment<BaseResponse<ArrayList<CalenderLogs>>>(
                    data = it,
                    fragment = this@CalenderFragment,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleCalenderLogsResponse,
                )
            }
        }
    }

    override fun initSetup() {
        setupUI()
        viewModel.callGetCalenderLogsApi()
    }

    private fun setupUI() {
        binding.apply {
            with(toolBar) {
                tvTitle.changeText(getString(R.string.label_calendar))
                ivBack.gone()
                ivMenu.changeDrawableImage(R.drawable.ic_search_calender)
            }
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

            fabAddActivity onSafeClick {
                activityLauncher.startActivityWithLauncher(requireActivity(),
                    AddNewActivityActivity::class.java) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val newLogString = result.data?.getStringExtra("newLog")
                        val newLog = gson.fromJson(newLogString, CalenderLogs::class.java)
                        handleAddLogSuccess(newLog)
                    }
                }
            }
        }
    }

    private fun handleAddLogSuccess(newLog: CalenderLogs) {
        val newLogLocalDate = parseIsoToLocalDate(newLog.date)
        Log.d("CalendarDebug", "serverDate='${newLog.date}', parsed=$newLogLocalDate, selected=$selectedDate")

        mainResponse.add(newLog)
        setCalendarLogs(mainResponse)

        // notify both the new date and selected date (defensive)
        newLogLocalDate?.let {
            try { binding.calendarView.notifyDateChanged(it) }
            catch (e: Exception) { binding.calendarView.notifyCalendarChanged() }
        }
        selectedDate?.let {
            try { binding.calendarView.notifyDateChanged(it) }
            catch (_: Exception) {}
        }

        updateDailyLogsList(selectedDate)
        updateUI()
    }

    private fun handleCalenderLogsResponse(response: BaseResponse<ArrayList<CalenderLogs>>?) {
        if (response.notNull() && response?.status == true) {
            mainResponse.clear()
            mainResponse.addAll(response.data ?: arrayListOf())

            // ensure calendar is set up first (call setupCalendar() once in onCreate/onViewCreated ideally)
            if (!isCalendarInitialized) {
                setupCalendar()
                isCalendarInitialized = true
            }

            // update logs, then refresh calendar UI
            setCalendarLogs(mainResponse)

            // Refresh the calendar display:
            // - Prefer a library refresh call if available. Keep safe-call to avoid crashes.
            try {
                binding.calendarView.notifyCalendarChanged()
            } catch (e: Throwable) {
                // fallback: re-scroll to current month to force rebind
                binding.calendarView.scrollToMonth(YearMonth.now())
            }
        }
    }

    private fun setCalendarLogs(calendarLogs: List<CalenderLogs>) {
        logsByDate = calendarLogs.mapNotNull { log ->
            parseIsoToLocalDate(log.date)?.let { date -> date to log }
        }.groupBy({ it.first }, { it.second })
        // Do NOT call notifyCalendarChanged() here — keep refresh separate.
        // Now update bottom list for current selection:
        updateDailyLogsList(selectedDate)
        updateUI()
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
                override fun create(view: View) = MonthDayViewContainer(view)

                override fun bind(container: MonthDayViewContainer, data: CalendarDay) {
                    val date = data.date
                    container.apply {
                        tvMonthDay.text = date.dayOfMonth.toString()

                        val isSelected = selectedDate == date

                        // Change Color of other month in current month
                        if (data.position == DayPosition.MonthDate) {
                            tvMonthDay.changeColor(R.color.black)
                        } else {
                            tvMonthDay.changeColor(R.color.colorB3B3B3)
                        }

                        // Reset UI then apply selection
                        if (isSelected && data.position == DayPosition.MonthDate) {
                            tvMonthDay.changeColor(R.color.white)
                            tvMonthDay.changeBackground(R.drawable.bg_selected_month_day)
                            // selection shows all dots by previous logic — but we'll also update based on logs
                            spaceView.gone()
                        } else {
                            tvMonthDay.background = null
                            spaceView.visible()
                        }

                        // --- HERE: use logsByDate to control dots ---
                        val logsForDay: List<CalenderLogs> = logsByDate[date] ?: emptyList()

                        // Menstruation dot: show only if any log has type == "MENSTRUATION" and status == true
                        val showMenstruation = logsForDay.any { it.type == "MENSTRUATION"}
                        if (showMenstruation && data.position == DayPosition.MonthDate) {
                            dotMenstruation.visible()
                        } else {
                            dotMenstruation.gone()
                        }

                        // Intercourse dot: show only if any log has type == "INTERCOURSE" and status == true
                        val showIntercourse = logsForDay.any { it.type == "INTERCOURSE"}
                        if (showIntercourse && data.position == DayPosition.MonthDate) {
                            dotIntercourse.visible()
                        } else {
                            dotIntercourse.gone()
                        }

                        // Temperature trend dot: per your instruction, when type == null treat as temperature entry
                        val showTemperature = logsForDay.any { it.type == null && it.temperature != null }
                        if (showTemperature && data.position == DayPosition.MonthDate) {
                            dotTemperatureTrend.visible()
                        } else {
                            dotTemperatureTrend.gone()
                        }

                        // If selected and you want to still show dots while selected:
                        // (previous code made dot visible on selection; adjust if needed)
                        if (isSelected && data.position == DayPosition.MonthDate) {
                            // ensure background and dots reflect logs as above; if you prefer selection overrides dots,
                            // move dot visibility changes inside this block instead.
                        }

                        view.setOnClickListener {
                            val old = selectedDate
                            selectedDate = date
                            old?.let(calendarView::notifyDateChanged)
                            calendarView.notifyDateChanged(date)
                            updateDailyLogsList(date)
                            updateUI()
                        }
                    }
                }
            }
            binding.root.post {
                updateDailyLogsList(selectedDate)
                updateUI()
                // also ensure the calendar highlights selectedDate visually
                try {
                    calendarView.notifyDateChanged(selectedDate!!)
                } catch (e: Exception) {
                    // ignore if calendar doesn't support that exact method
                }
            }
        }
    }

    private fun updateUI() {
        binding.apply {
            // Update any other UI elements based on the selected date
            val isToday = selectedDate == LocalDate.now()
            // Update the label to show "Today" if the selected date is today if it is yesterday then show "Yesterday" else show the date in dd/MM/yyyy format
            tvToday.text = when {
                isToday -> getString(R.string.label_today)
                selectedDate == LocalDate.now().minusDays(1) -> getString(R.string.label_yesterday)
                selectedDate == LocalDate.now().plusDays(1) -> getString(R.string.label_tomorrow)
                else -> selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
            }
        }
    }


    private fun updateDailyLogsList(date: LocalDate?) {
        // If date is null -> hide
        if (date == null) {
            binding.rvTodaysActivity.gone()
            binding.tvEmptyLogsView.visible() // optional: show an empty view or message
            return
        }

        // Get logs for this date from the grouped map
        val logsForDay = logsByDate[date] ?: emptyList()

        if (logsForDay.isEmpty()) {
            // nothing for this date -> hide list
            binding.rvTodaysActivity.gone()
            binding.tvEmptyLogsView.visible() // optional
        } else {
            // we have items -> show list and feed adapter
            binding.rvTodaysActivity.visible()
            binding.tvEmptyLogsView.gone()

            logsList.clear()
            logsList.addAll(logsForDay)
            todaysActivityListAdapter.notifyDataSetChanged()
        }
    }


    // robust parser (keeps server date as-intended)
    private fun parseIsoToLocalDate(iso: String?): LocalDate? {
        if (iso.isNullOrBlank()) return null
        return try {
            // prefer this: respects server's timestamp and yields the server date
            java.time.OffsetDateTime.parse(iso).toLocalDate()
        } catch (e: Exception) {
            try {
                LocalDate.parse(iso)
            } catch (ex: Exception) {
                null
            }
        }
    }

    private fun onItemClick(item: CalenderLogs) {
        ActivityDetailsBottomSheetDialog.newInstance(binding.root, requireActivity(), callBack = {

        }, log = item).show(requireActivity().supportFragmentManager, "ActivityDetailsBottomSheetDialog")
    }
}

class MonthDayViewContainer(view: View) : ViewContainer(view) {
    val tvMonthDay = MonthCalenderDayViewBinding.bind(view).tvMonthDay
    val dotMenstruation = MonthCalenderDayViewBinding.bind(view).dotMenstruation
    val dotIntercourse = MonthCalenderDayViewBinding.bind(view).dotIntercourse
    val dotTemperatureTrend = MonthCalenderDayViewBinding.bind(view).dotTemperatureTrend
    val spaceView = MonthCalenderDayViewBinding.bind(view).view
}