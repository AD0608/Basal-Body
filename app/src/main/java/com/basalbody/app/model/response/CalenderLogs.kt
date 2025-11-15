package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CalenderLogs(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("notes")
	val notes: String? = null,

	@field:SerializedName("temperature")
	val temperature: Double? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
) : Serializable
