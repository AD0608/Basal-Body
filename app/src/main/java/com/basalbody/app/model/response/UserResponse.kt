package com.basalbody.app.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserResponse(

	@field:SerializedName("is_firebase_auth")
	val isFirebaseAuth: Boolean? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("is_authenticate")
	val isAuthenticate: Boolean? = null,

	@field:SerializedName("token")
	val token: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null
) : Serializable

data class User(

	@field:SerializedName("is_firebase_auth")
	val isFirebaseAuth: Boolean? = null,

	@field:SerializedName("role")
	val role: Role? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("profile_image_id")
	val profileImageId: Any? = null,

	@field:SerializedName("firebase_uid")
	val firebaseUid: Any? = null,

	@field:SerializedName("profileImage")
	val profileImage: Any? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class Role(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
