package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HomeResponse(

    @field:SerializedName("selectedDate")
    val selectedDate: String? = null,

    @field:SerializedName("cycleInfo")
    val cycleInfo: CycleInfo? = null,

    @field:SerializedName("temperature")
    val temperature: Double? = null,

    @field:SerializedName("activityStatus")
    val activityStatus: ActivityStatus? = null

) : Serializable

data class CycleInfo(

    @field:SerializedName("isPeriodDay")
    val isPeriodDay: Boolean? = null,

    @field:SerializedName("cycleDay")
    val cycleDay: Int? = null,

    @field:SerializedName("cycleStatus")
    val cycleStatus: String? = null
) : Serializable


data class ActivityStatus(

    @field:SerializedName("menstruation")
    val menstruation: ActivityDetail? = null,

    @field:SerializedName("intercourse")
    val intercourse: ActivityDetail? = null
) : Serializable


data class ActivityDetail(

    @field:SerializedName("added")
    val added: Boolean? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
) : Serializable
