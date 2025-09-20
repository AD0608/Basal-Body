package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChangePasswordResponse(
	@field:SerializedName("is_password_changed")
	val isPasswordChanged: Boolean? = null
): Serializable
