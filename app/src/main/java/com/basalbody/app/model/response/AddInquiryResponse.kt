package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddInquiryResponse(

    @field:SerializedName("inquiry")
    val inquiry: Inquiry? = null
) : Serializable

data class Inquiry(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("email")
    val email: String? = null
) : Serializable
