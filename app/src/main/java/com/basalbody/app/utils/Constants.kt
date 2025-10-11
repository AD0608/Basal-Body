package com.basalbody.app.utils

object Constants {
    const val STATUS_SUCCESS = "Success"
    const val STATUS_FAILED = "Failed"
    const val STATUS_ERROR = 0
    const val STATUS_SUCCESSFUL = 1
    const val STATUS_REQUIRE = "Required"
    const val USER_TYPE_COMPANY_DRIVER = "company"
    const val USER_TYPE_FREELANCER = "freelancer"

    const val ISSUE_TYPE_BLUETOOTH = "BLUETOOTH"
    const val ISSUE_TYPE_CONTACT = "CONTACT"

    const val LANG_AR = "ar"
    const val LANG_EN = "en"
    const val PRIVACY_POLICY = "privacy_policy"
    const val TERMS_AND_CONDITIONS = "terms_and_conditions"
    const val ABOUT_US = "about_us"

    var URL_TERM_CONDITION="https://www.google.com"
    const val URL_PRIVACY_POLICY="https://www.google.com"
    const val URL_ABOUT_US="https://www.google.com"
    const val URL_TEST="https://www.google.com"
    var URL_DATA_PRIVACY="https://www.google.com"

    var latitude = 0.0
    var longitude = 0.0
    const val GOOGLE_MAP_ZOOM_LEVEL = 14f
    const val VALUE_ANIMATOR_DURATION = 3_000L

    const val EMPTY_STRING = ""
    const val DEFAULT_ONE = 1
    const val DEFAULT_ZERO = 0
    const val DEFAULT_WIDTH_DIALOG = 90
    const val NO_INTERNET_WIDTH_DIALOG = 80
    const val SPLASH_DELAY_TIME = 3000L
    const val DEBOUNCE_DELAY_TIME = 500L
    const val GIF_MIME_TYPE = "image/gif"
    const val WEBP_MIME_TYPE = "image/webp"

    const val MALE = "Male"
    const val FEMALE = "Female"
    const val SETTINGS = "Settings"
    const val PICKUP = "pickup"
    const val DROPOFF = "dropoff"
    var UNREAD_NOTIFICATION_COUNT = 0
    var CHAT_SHIPMENT_ID = 0

    //-----------Otp verification--------//
    const val EXPIRE_OTP_TIMER = 30 * 1000L
    const val OTP_TIMER_INTERVAL =  30 * 1000L

    //-----------Validation--------//
    const val EMAIL_PATTERN =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    //    const val REGEX_VALID_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{4,}\$" //TODO: Not allow blank space between Password
    const val REGEX_VALID_PASSWORD =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=]).{8,}\$" //TODO: Allow Blank Splace between Password

    val REGEX_VALID_PASSWORD_NEW =
        "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$" //TODO: Regex: at least 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special symbol
    const val PASSWORD_LIMIT = 8

    const val MAX_AMOUNT = "^\\d{0,5}(\\.\\d{0,2})?$"

    /*PAGINATION*/
    const val PAGE_SIZE = 6

    //--------BUNDLE KEYS------//
    const val BUNDLE_KEY_IS_FROM_LOGIN = "is_from_login"
    const val BUNDLE_KEY_WHICH_WEB_VIEW = "bundle_key_which_web_view"
    const val BUNDLE_KEY_USER_EMAIL_OR_PHONE = "bundle_key_user_email_or_phone"
    const val BUNDLE_KEY_IS_EMAIL = "isEmail"
    const val BUNDLE_KEY_RESET_TOKEN = "reset_token"

    //---------------Button Tags---------------//


    //---------------Broadcast Action---------------//

}