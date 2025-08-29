package com.mxb.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InitData(
    @SerializedName("force_update")
    val forceUpdate: Boolean = false,
    @SerializedName("update")
    val update: Boolean? = false,
    @SerializedName("maintenance")
    val maintenance: Boolean? = false,
    @SerializedName("locale")
    val locale: String? = "",
) : Serializable