package com.basalbody.app.model.request

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
	@field:SerializedName("old_password")
    var oldPassword: String? = null,

	@field:SerializedName("new_password")
	var newPassword: String? = null,

	@field:SerializedName("confirm_password")
	var confirmPassword: String? = null
)
