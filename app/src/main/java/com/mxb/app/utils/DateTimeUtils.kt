package com.mxb.app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

const val TZ_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
const val dd_mmmm_yyyy__hh_mm_aa = "dd-MMMM-yyyy, hh:mm aa"
const val MMM_yyyy_h_mm_a = "MMM yyyy, h:mm a"
const val MMM_yyyy = "MMM yyyy"
const val h_mm_a = "h:mm a"
val defaultLocaleForDate: Locale = Locale.ENGLISH

fun String.getDateTimeFromUTCDateTime(
    inputFormat: String = TZ_ISO_FORMAT,
    outputFormat: String
): String? {
    val parser = SimpleDateFormat(inputFormat, defaultLocaleForDate)
    parser.timeZone = TimeZone.getTimeZone("UTC")
    val date = parser.parse(this)
    val formatter = SimpleDateFormat(outputFormat, defaultLocaleForDate)
    formatter.timeZone = TimeZone.getDefault()
    return date?.let { formatter.format(it) }
}

fun String?.getLocationDateTime(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", defaultLocaleForDate)
    parser.timeZone = TimeZone.getTimeZone("UTC") // input is in UTC

    val date = this?.let { parser.parse(it) } ?: return ""

    val calendar = Calendar.getInstance()
    calendar.time = date

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val daySuffix = getDayOfMonthSuffix(day)

    val outputFormat = SimpleDateFormat(MMM_yyyy_h_mm_a, defaultLocaleForDate)
    outputFormat.timeZone = TimeZone.getDefault() // convert to device local time

    return "$day$daySuffix ${outputFormat.format(date)}"
}

fun String?.getReviewDateTime(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", defaultLocaleForDate)
    parser.timeZone = TimeZone.getTimeZone("UTC") // input is in UTC

    val date = this?.let { parser.parse(it) } ?: return ""

    val calendar = Calendar.getInstance()
    calendar.time = date

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val daySuffix = getDayOfMonthSuffix(day)

    val outputFormat = SimpleDateFormat(MMM_yyyy, defaultLocaleForDate)
    outputFormat.timeZone = TimeZone.getDefault() // convert to device local time

    return "$day$daySuffix ${outputFormat.format(date)}"
}

fun getDayOfMonthSuffix(day: Int): String {
    return if (day in 11..13) {
        "th"
    } else when (day % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

fun String?.chatDateTime(): String {
    if (this.isNullOrEmpty()) return ""

    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", defaultLocaleForDate)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val parsedDate = try {
        inputFormat.parse(this)
    } catch (e: Exception) {
        return ""
    } ?: return ""

    val localCalendar = Calendar.getInstance()
    localCalendar.time = parsedDate
    val now = Calendar.getInstance()

    val isToday = localCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            localCalendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

    val isYesterday = localCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            localCalendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) - 1

    return when {
        isToday -> {
            val timeFormat = SimpleDateFormat("h:mm a", defaultLocaleForDate)
            timeFormat.timeZone = TimeZone.getDefault()
            timeFormat.format(parsedDate)
        }

        isYesterday -> "Yesterday"
        else -> {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", defaultLocaleForDate)
            dateFormat.timeZone = TimeZone.getDefault()
            dateFormat.format(parsedDate)
        }
    }
}

fun String?.chatHeaderDateTime(): String {
    if (this.isNullOrEmpty()) return ""

    val inputFormat = SimpleDateFormat(TZ_ISO_FORMAT, defaultLocaleForDate)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val parsedDate = try {
        inputFormat.parse(this)
    } catch (e: Exception) {
        return ""
    } ?: return ""

    val localCalendar = Calendar.getInstance()
    localCalendar.time = parsedDate
    val now = Calendar.getInstance()

    val isToday = localCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            localCalendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

    val isYesterday = localCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            localCalendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) - 1

    return when {
        isToday -> "Today"
        isYesterday -> "Yesterday"
        else -> {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", defaultLocaleForDate)
            dateFormat.timeZone = TimeZone.getDefault()
            dateFormat.format(parsedDate)
        }
    }
}