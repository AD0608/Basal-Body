package com.basalbody.app.model.request

import com.google.gson.annotations.SerializedName

data class ResetPasswordStep1Request(
    @SerializedName("phone_number")
    var phoneNumber: String? = null,

    @SerializedName("otp")
    var otp: String? = null,

    @SerializedName("email")
    var email: String? = null
)