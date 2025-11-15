package com.basalbody.app.ui.home.activity

import android.content.Intent
import android.util.Log
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivityAddNewActivityBinding
import com.basalbody.app.extensions.changeBackground
import com.basalbody.app.extensions.changeColor
import com.basalbody.app.extensions.changeDrawableImage
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onNoSafeClick
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.AddDailyLogRequest
import com.basalbody.app.model.response.AddDailyLogResponse
import com.basalbody.app.model.response.CalenderLogs
import com.basalbody.app.model.response.UserResponse
import com.basalbody.app.ui.home.dialog.AddNewActivitySuccessDialog
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.disableField
import com.basalbody.app.utils.getText
import com.basalbody.app.utils.setText
import com.basalbody.app.utils.showSnackBar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class AddNewActivityActivity : BaseActivity<HomeViewModel, ActivityAddNewActivityBinding>() {

    private var selectedDate: String = ""

    override fun getViewBinding(): ActivityAddNewActivityBinding =
        ActivityAddNewActivityBinding.inflate(layoutInflater)

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callAllDailyLogApiStateFlow.collect {
                FlowInActivity<BaseResponse<AddDailyLogResponse>>(
                    data = it,
                    context = this@AddNewActivityActivity,
                    shouldShowErrorMessage = true,
                    shouldShowSuccessMessage = false,
                    shouldShowLoader = true,
                    onSuccess = ::handleSuccessResponse,
                )
            }
        }
    }

    private fun handleSuccessResponse(data: BaseResponse<AddDailyLogResponse>?) {
        if (data?.data.notNull()) {
            AddNewActivitySuccessDialog.newInstance(
                isCancel = true,
                rootView = binding.root,
                activity = this@AddNewActivityActivity,
                onDismiss = {
                    val addedLog = data?.data
                    val newLog = CalenderLogs(
                        date = addedLog?.date,
                        temperature = addedLog?.temperature,
                        notes = addedLog?.notes,
                        status = addedLog?.status,
                        type = addedLog?.type
                    )
                    val intent = Intent()
                    intent.putExtra("newLog", gson.toJson(newLog))
                    setResult(RESULT_OK, intent)
                    finish()
                }
            ).show(supportFragmentManager, AddNewActivitySuccessDialog::class.java.simpleName)
        }
    }

    override fun initSetup() {
        binding.apply {
            toolBar.tvTitle.changeText("Add")
            toolBar.ivBack onSafeClick { onBackPressedDispatcher.onBackPressed() }
            etDate.disableField()
            changeYesNo(isYesSelected = true)
            changeSelectedActivity(true)
        }
    }

    override fun listeners() {
        binding.apply {
            rbMenstruation onNoSafeClick {
                changeSelectedActivity(true)
            }

            rbIntercourse onNoSafeClick {
                changeSelectedActivity(false)
            }

            etDate onSafeClick {
                showDatePicker()
            }

            btnAdd onSafeClick {
                if (allDetailsValid()) {
                    val request = AddDailyLogRequest(
                        type = if (rbMenstruation.isSelected) "MENSTRUATION" else "INTERCOURSE",
                        status = btnYes.isSelected,
                        date = selectedDate,
                        temperature = etTemperature.getText(),
                        notes = etNotes.getText()?.trim()?.toString(),
                    )
                    viewModel.callAllDailyLogApi(request = request)
                }
            }

            btnYes onNoSafeClick {
                changeYesNo(isYesSelected = true)
            }

            btnNo onNoSafeClick {
                changeYesNo(isYesSelected = false)
            }
        }
    }

    private fun changeYesNo(isYesSelected: Boolean) {
        binding.apply {
            btnYes.isSelected = isYesSelected
            btnNo.isSelected = !isYesSelected
            btnYes.apply {
                changeColor(if (isYesSelected) R.color.white else R.color.green_46B74F)
                changeBackground(if (isYesSelected) R.drawable.bg_rounded_corners_gradient else R.drawable.bg_rounded_corners)
            }
            btnNo.apply {
                changeColor(if (!isYesSelected) R.color.white else R.color.green_46B74F)
                changeBackground(if (!isYesSelected) R.drawable.bg_rounded_corners_gradient else R.drawable.bg_rounded_corners)
            }
        }
    }

    private fun changeSelectedActivity(isMenstruationSelected: Boolean) {
        binding.apply {
            rbMenstruation.isSelected = isMenstruationSelected
            rbIntercourse.isSelected = !isMenstruationSelected
            imgRBMenstruation.changeDrawableImage(if (isMenstruationSelected) R.drawable.ic_radio_selected else R.drawable.ic_radio_unselected)
            imgRBIntercourse.changeDrawableImage(if (isMenstruationSelected) R.drawable.ic_radio_unselected else R.drawable.ic_radio_selected)
        }
    }

    private fun showDatePicker() {
        // If empty, set today's date
        if (selectedDate.isEmpty()) {
            selectedDate = getToday()
        }
        // Create constraints (no future dates)
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())   // <-- THIS disables future dates
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setTheme(R.style.MyDatePickerTheme)
            .setSelection(dateToMillis(selectedDate))
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            selectedDate = millisToDate(calendar.timeInMillis)
            binding.etDate.setText(selectedDate)
        }

        datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
    }

    private fun getToday(): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return "%04d-%02d-%02d".format(year, month, day)
    }

    private fun dateToMillis(date: String): Long {
        val parts = date.split("-").map { it.toInt() }
        val year = parts[0]
        val month = parts[1] - 1 // calendar month is 0-based
        val day = parts[2]

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(year, month, day, 0, 0, 0)
        }
        return cal.timeInMillis
    }

    private fun millisToDate(millis: Long): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = millis
        }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        return "%04d-%02d-%02d".format(year, month, day)
    }



    private fun allDetailsValid() : Boolean {
        return when {
            binding.etTemperature.getText()?.isEmpty() == true -> {
                showSnackBar("Please enter temperature", Constants.STATUS_ERROR, this)
                false
            }

            binding.etDate.getText()?.isEmpty() == true -> {
                showSnackBar("Please enter date", Constants.STATUS_ERROR, this)
                false
            }

            binding.etNotes.text?.trim()?.isEmpty() == true -> {
                showSnackBar("Please enter notes", Constants.STATUS_ERROR, this)
                false
            }

            else -> {
                true
            }
        }
    }

}