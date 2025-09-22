package com.basalbody.app.model.request

import com.google.gson.annotations.SerializedName

data class AddInquiryRequest(
    @SerializedName("type")
    val type: String,

    @SerializedName("message")
    val message: String,
)
