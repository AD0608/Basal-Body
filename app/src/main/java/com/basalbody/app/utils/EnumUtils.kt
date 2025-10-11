package com.basalbody.app.utils

object EnumUtils {
    enum class TextChangeState {
        BeforeTextChange,
        AfterTextChange,
        OnTextChange
    }

    enum class NotificationType(var type: String) {
        NEW_SHIPMENT_REQUEST("new_shipment_request"),
        SHIPMENT_REQUESTED("shipment_requested"),
        SHIPMENT_ASSIGNED("shipment_assigned"),
        SHIPMENT_CANCELLED("shipment_cancelled"),
        ACCOUNT_STATUS_UPDATE("account_status_update"),
        PAYMENT_REQUEST_APPROVED("payment_request_approved"),
        PAYMENT_REQUEST_REJECTED("payment_request_rejected"),
        LICENSE_EXPIRY_REMINDER("license_expiry_reminder"),
        GENERAL("general"),
        LOGOUT("logout"),
        CHAT("chat");
    }
    enum class WebView{
        PRIVACY_POLICY,
        TERMS_AND_CONDITIONS,
        ABOUT_US,
        DATA_PRIVACY,
        NONE
    }
    enum class DialogType{
        LOG_OUT,
        APP_UPDATE,
        APP_FORCE_UPDATE,
        APP_UNDER_MAINTENANCE,
        DELETE_ACCOUNT,
        NONE
    }

}