package com.basalbody.app.datastore

import com.basalbody.app.model.response.InitData
import com.basalbody.app.model.response.LoginResponse


interface LocalDataRepositoryImpl {

    fun saveUserSession(key: String?, value: String?)
    fun getUserSession(key: String, defaultValue: String = ""): String

    fun saveFcmToken(fcmToken: String)
    fun getFcmToken(): String
    fun clearFcmToken()

    fun saveBarrierToken(token: String)
    fun getBarrierToken(): String

    fun getCurrentLanguage(): String

    fun saveUserDetails(userData: LoginResponse)
    fun getUserDetails(): LoginResponse?

    fun resetUserData()

    fun saveInitData(initData: InitData?)
    fun getInitData(): InitData?
    fun getUserType(): String
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
}
