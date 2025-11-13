package com.basalbody.app.model.request

import com.google.gson.annotations.SerializedName

data class AddDailyLogRequest(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("notes")
	val notes: String? = null,

	@field:SerializedName("temperature")
	val temperature: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
