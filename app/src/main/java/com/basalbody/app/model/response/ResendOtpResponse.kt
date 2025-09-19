package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResendOtpResponse(

	@field:SerializedName("is_mail_sent")
	val isMailSent: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Serializable