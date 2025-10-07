package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeleteUserResponse(
    @field:SerializedName("isDeleted")
    val isDeleted: Boolean? = null
) : Serializable
