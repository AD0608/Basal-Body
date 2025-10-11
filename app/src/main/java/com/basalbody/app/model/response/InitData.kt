package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class InitData(
    @SerializedName("isForceUpdate")
    val isForceUpdate: Boolean = false,
    @SerializedName("isPartialUpdate")
    val isPartialUpdate: Boolean? = false,
    @SerializedName("isMaintenance")
    val isMaintenance: Boolean? = false,
    @SerializedName("latestVersion")
    val latestVersion: String? = "",
    @SerializedName("device")
    val device: String? = "",
    @SerializedName("webPageUrl")
    val webPageUrl: WebPageUrl
) : Serializable

data class WebPageUrl(
    val dataPrivacy: String? = "",
    val termsAndConditions: String? = ""
) : Serializable
