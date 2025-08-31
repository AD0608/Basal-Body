package com.basalbody.app.utils.language

import com.google.gson.annotations.SerializedName

data class Language(
    @SerializedName("id")
    val id: Int,
    @SerializedName("language")
    val language: String,
    @SerializedName("display")
    val display: String,
    @SerializedName("code")
    val code: String
)