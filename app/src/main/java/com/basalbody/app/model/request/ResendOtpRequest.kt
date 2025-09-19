package com.basalbody.app.model.request

import com.google.gson.annotations.SerializedName

data class ResendOtpRequest(
    @SerializedName("type")
    var type: String? = null,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("phone_number")
    var phoneNumber: String? = null
)
