package com.basalbody.app.model.request

import com.google.gson.annotations.SerializedName

data class ResetPasswordStep2Request(
    @SerializedName("reset_token")
    var resetToken: String? = null,

    @SerializedName("phone_number")
    var phoneNumber: String? = null,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("new_password")
    var newPassword: String? = null,

    @SerializedName("confirm_password")
    var confirmPassword: String? = null
)
