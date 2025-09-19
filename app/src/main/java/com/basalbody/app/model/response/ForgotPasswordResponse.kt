package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ForgotPasswordResponse(

	@field:SerializedName("is_otp_sent")
	val isOtpSent: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Serializable
