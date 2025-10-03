package com.basalbody.app.utils

import android.app.Activity
import com.basalbody.app.R

enum class ValidationStatus {
    EMPTY_PHONE,
    PHONE_LENGTH,
    PHONE_VALID,
    PHONE_VERIFY,
    EMPTY_PASSWORD,
    EMPTY_CURRENT_PASSWORD,
    PASSWORD_START_END_BLANK_SPACE,
    INVALID_PASSWORD,
    CURRENT_PASSWORD_START_END_BLANK_SPACE,
    EMPTY_NEW_PASSWORD,
    NEW_PASSWORD_START_END_BLANK_SPACE,
    CONFIRM_PASSWORD_START_END_BLANK_SPACE,
    EMPTY_CONFIRM_PASSWORD,
    PASSWORD_LENGTH,
    CURRENT_PASSWORD_LENGTH,
    NEW_PASSWORD_LENGTH,
    CONFIRM_PASSWORD_LENGTH,
    PASSWORD_CONFIRM_PASS_SAME,
    TERMS_CONDITION,
    PASSWORD_CONFIRM_PASS_NOT_SAME,
    NEW_PASSWORD_CONFIRM_PASS_NOT_SAME,
    CURRENT_PASSWORD_NEW_PASSWORD_NOT_SAME,

    EMPTY_CONFIRM_NEW_PASSWORD,
    CONFIRM_NEW_PASSWORD_LENGTH,
    CONFIRM_NEW_PASSWORD_START_END_BLANK_SPACE,
    EMPTY_NAME,
    EMPTY_EMAIL,
    INVALID_EMAIL,
    EMPTY_GENDER,
    EMPTY_OTP,
    INVALID_OTP,
    EMPTY_DOB,
    EMPTY_PROFILE_IMAGE,
    EMPTY_STREET,
    EMPTY_CITY,
    EMPTY_STATE,
    EMPTY_COUNTRY,
    EMPTY_BUILDING,
    EMPTY_ZIP,
    EMPTY_MESSAGE,
    EMPTY_ISSUE_MESSAGE,
    UNKNOWN,
}

object Validation {
    fun showMessageDialog(
        activity: Activity,
        validationStatus: ValidationStatus
    ) {
        val message = getMessage(activity = activity, validationStatus = validationStatus)
        if (message.isNotEmpty()) {
            showSnackBar(message, Constants.STATUS_ERROR, activity)
        }
    }

    private fun getMessage(activity: Activity, validationStatus: ValidationStatus): String {
        return when (validationStatus) {
            ValidationStatus.EMPTY_PHONE -> activity.getString(R.string.validation_empty_phone)
            ValidationStatus.PHONE_LENGTH -> activity.getString(R.string.validation_phone_min)
            ValidationStatus.PHONE_VALID -> activity.getString(R.string.validation_invalid_phone)
            ValidationStatus.PHONE_VERIFY -> activity.getString(R.string.validation_phone_verify)
            ValidationStatus.EMPTY_PASSWORD -> activity.getString(R.string.validation_empty_password)
            ValidationStatus.EMPTY_CURRENT_PASSWORD -> activity.getString(R.string.validation_empty_current_password)
            ValidationStatus.EMPTY_NEW_PASSWORD -> activity.getString(R.string.validation_empty_new_password)
            ValidationStatus.EMPTY_CONFIRM_PASSWORD -> activity.getString(R.string.validation_empty_confirm_password)
            ValidationStatus.PASSWORD_START_END_BLANK_SPACE -> activity.getString(R.string.validation_password_space)
            ValidationStatus.INVALID_PASSWORD -> activity.getString(R.string.invalid_password)
            ValidationStatus.CURRENT_PASSWORD_START_END_BLANK_SPACE -> activity.getString(R.string.validation_current_password_space)
            ValidationStatus.NEW_PASSWORD_START_END_BLANK_SPACE -> activity.getString(R.string.validation_new_password_space)
            ValidationStatus.PASSWORD_LENGTH -> activity.getString(R.string.validation_password_min)
            ValidationStatus.CURRENT_PASSWORD_LENGTH -> activity.getString(R.string.validation_current_password_min)
            ValidationStatus.NEW_PASSWORD_LENGTH -> activity.getString(R.string.validation_new_password_min)
            ValidationStatus.CONFIRM_PASSWORD_LENGTH -> activity.getString(R.string.validation_confirm_password_min)
            ValidationStatus.CONFIRM_PASSWORD_START_END_BLANK_SPACE -> activity.getString(R.string.validation_confirm_password_space)
            ValidationStatus.PASSWORD_CONFIRM_PASS_SAME -> activity.getString(R.string.validation_password_and_confirm_password_must_be_same)
            ValidationStatus.TERMS_CONDITION -> activity.getString(R.string.validation_terms_condition)
            ValidationStatus.PASSWORD_CONFIRM_PASS_NOT_SAME -> activity.getString(R.string.validation_password_and_confirm_password_does_not_match)
            ValidationStatus.NEW_PASSWORD_CONFIRM_PASS_NOT_SAME -> activity.getString(R.string.validation_new_password_and_confirm_password_does_not_match)
            ValidationStatus.CURRENT_PASSWORD_NEW_PASSWORD_NOT_SAME -> activity.getString(R.string.validation_new_password_and_current_password_must_be_different)
            ValidationStatus.EMPTY_NAME -> activity.getString(R.string.validation_empty_name)
            ValidationStatus.EMPTY_EMAIL -> activity.getString(R.string.validation_empty_email)
            ValidationStatus.INVALID_EMAIL -> activity.getString(R.string.validation_invalid_email)
            ValidationStatus.EMPTY_GENDER -> activity.getString(R.string.validation_empty_gender)
            ValidationStatus.EMPTY_OTP -> activity.getString(R.string.validation_empty_otp)
            ValidationStatus.INVALID_OTP -> activity.getString(R.string.validation_invalid_otp)
            ValidationStatus.EMPTY_DOB -> activity.getString(R.string.validation_empty_dob)
            ValidationStatus.EMPTY_PROFILE_IMAGE -> activity.getString(R.string.validation_empty_profile_image)
            ValidationStatus.EMPTY_STREET -> activity.getString(R.string.validation_empty_street)
            ValidationStatus.EMPTY_CITY -> activity.getString(R.string.validation_empty_city)
            ValidationStatus.EMPTY_STATE -> activity.getString(R.string.validation_empty_state)
            ValidationStatus.EMPTY_COUNTRY -> activity.getString(R.string.validation_empty_country)
            ValidationStatus.EMPTY_BUILDING -> activity.getString(R.string.validation_empty_unit_number)
            ValidationStatus.EMPTY_ZIP -> activity.getString(R.string.validation_empty_postal_code)
            ValidationStatus.EMPTY_CONFIRM_NEW_PASSWORD -> activity.getString(R.string.validation_empty_confirm_new_password)
            ValidationStatus.CONFIRM_NEW_PASSWORD_LENGTH -> activity.getString(R.string.validation_confirm_new_password_min)
            ValidationStatus.CONFIRM_NEW_PASSWORD_START_END_BLANK_SPACE -> activity.getString(R.string.validation_confirm_new_password_space)
            ValidationStatus.EMPTY_MESSAGE -> activity.getString(R.string.validation_empty_message)
            ValidationStatus.EMPTY_ISSUE_MESSAGE -> activity.getString(R.string.validation_empty_issue_message)
            ValidationStatus.UNKNOWN -> ""
        }
    }
}