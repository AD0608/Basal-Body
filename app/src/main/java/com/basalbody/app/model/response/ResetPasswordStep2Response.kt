package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResetPasswordStep2Response(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("is_password_reset")
	val isPasswordReset: Boolean? = null
) : Serializable