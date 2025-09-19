package com.basalbody.app.network


//-------API Packages-------//
const val PACKAGE_AUTH = "auth/"


//-------APIs-------//
const val API_INIT = "init/{version}/android"
const val API_LOGIN = "${PACKAGE_AUTH}login"
const val API_FORGOT_PASSWORD = "${PACKAGE_AUTH}forgot-password"
const val API_RESEND_OTP = "${PACKAGE_AUTH}resend-otp"
const val API_RESET_PASSWORD_STEP1 = "${PACKAGE_AUTH}reset-password/step1"
const val API_RESET_PASSWORD_STEP2 = "${PACKAGE_AUTH}reset-password/step2"


//---------- Api Params ----------//
const val PARAM_LOGIN = "login"