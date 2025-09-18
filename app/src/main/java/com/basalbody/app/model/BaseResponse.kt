package com.basalbody.app.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BaseResponse<T : Serializable>(
    @SerializedName("success")
    var status: Boolean = false,

    @SerializedName("message")
    var message: String = "",

    @field:SerializedName("meta")
    val meta: Meta? = null,

    @field:SerializedName("links")
    val links: Links? = null,

    @SerializedName("bearer_token")
    val bearerToken: String? = null,

    @field:SerializedName("data")
    val data: T? = null,

    @field:SerializedName("rating_counts")
    val ratingCounts: RatingCounts? = null,

    @field:SerializedName("average_rating")
    val averageRating: Double? = null,

    @field:SerializedName("total_rating")
    val totalRatings: Int? = null,

    @field:SerializedName("total_earning")
    val totalEarning: String? = null,
)

data class RatingCounts(
    @field:SerializedName("1")
    val one: Int? = null,
    @field:SerializedName("2")
    val two: Int? = null,
    @field:SerializedName("3")
    val three: Int? = null,
    @field:SerializedName("4")
    val four: Int? = null,
    @field:SerializedName("5")
    val five: Int? = null,
)

data class Links(

    @field:SerializedName("next")
    val next: String? = null,

    @field:SerializedName("last")
    val last: String? = null,

    @field:SerializedName("prev")
    val prev: String? = null,

    @field:SerializedName("first")
    val first: String? = null,
)

data class LinksItem(

    @field:SerializedName("active")
    val active: Boolean? = null,

    @field:SerializedName("label")
    val label: String? = null,

    @field:SerializedName("url")
    val url: Any? = null,
)

data class Meta(

    @field:SerializedName("path")
    val path: String? = null,

    @field:SerializedName("per_page")
    val perPage: String? = null,

    @field:SerializedName("total")
    val total: Int? = null,

    @field:SerializedName("last_page")
    val lastPage: Int? = null,

    @field:SerializedName("from")
    val from: Int? = null,

    @field:SerializedName("links")
    val links: List<LinksItem?>? = null,

    @field:SerializedName("to")
    val to: Int? = null,

    @field:SerializedName("current_page")
    val currentPage: Int? = null,
)

