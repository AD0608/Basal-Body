package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetInsightsResponse(

    @field:SerializedName("fertileWindow")
    val fertileWindow: FertileWindow? = null,

    @field:SerializedName("monthlyInsights")
    val monthlyInsights: List<MonthlyInsight>? = null

) : Serializable

data class FertileWindow(

    @field:SerializedName("start")
    val start: String? = null,

    @field:SerializedName("end")
    val end: String? = null
) : Serializable


data class MonthlyInsight(

    @field:SerializedName("month")
    val month: String? = null,

    @field:SerializedName("year")
    val year: Int? = null,

    @field:SerializedName("averageTemperature")
    val averageTemperature: Double? = null,

    @field:SerializedName("days")
    val days: Int? = null
) : Serializable
