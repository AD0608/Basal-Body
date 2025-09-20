package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LogoutResponse(
	@field:SerializedName("isLoggedOut")
	val isLoggedOut: Boolean? = null
) : Serializable
