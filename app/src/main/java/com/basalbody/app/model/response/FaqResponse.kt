package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FaqResponse(
    @field:SerializedName("data")
    val data: List<FaqItem>
) : Serializable

data class FaqItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("question")
    val question: String? = "",

    @field:SerializedName("answer")
    val answer: String? = ""
) : Serializable
