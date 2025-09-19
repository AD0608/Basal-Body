package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName

data class ResetPasswordStep1Response(

	@field:SerializedName("resetToken")
	val resetToken: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("is_otp_verified")
	val isOtpVerified: Boolean? = null
) : java.io.Serializable