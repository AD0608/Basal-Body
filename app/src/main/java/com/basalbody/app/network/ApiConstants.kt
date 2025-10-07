package com.basalbody.app.network


//-------API Packages-------//
const val PACKAGE_AUTH = "auth/"


//-------APIs-------//
const val API_INIT = "init/{version}/android"
const val API_LOGIN = "${PACKAGE_AUTH}login"
const val API_REGISTER = "${PACKAGE_AUTH}register"
const val API_FORGOT_PASSWORD = "${PACKAGE_AUTH}forgot-password"
const val API_RESEND_OTP = "${PACKAGE_AUTH}resend-otp"
const val API_RESET_PASSWORD_STEP1 = "${PACKAGE_AUTH}reset-password/step1"
const val API_RESET_PASSWORD_STEP2 = "${PACKAGE_AUTH}reset-password/step2"
const val API_LOGOUT = "${PACKAGE_AUTH}logout"
const val API_USER_DELETE = "${PACKAGE_AUTH}user-delete"
const val API_CHANGE_PASSWORD = "${PACKAGE_AUTH}change-password/{userId}"
const val API_GET_USER_PROFILE = "user"
const val API_UPDATE_USER_PROFILE = "user/{userId}"
const val API_UPDATE_USER_PROFILE_PICTURE = "user/upload-profile"
const val API_ADD_INQUIRY = "user/add-inquiry"


//---------- Api Params ----------//
const val PARAM_LOGIN = "login"