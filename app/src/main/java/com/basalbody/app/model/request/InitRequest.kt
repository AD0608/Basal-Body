package com.basalbody.app.model.request

import com.google.gson.annotations.SerializedName

data class InitRequest(
    @SerializedName("device")
    val device: String
)
