package com.mxb.app.datastore

import com.mxb.app.model.response.InitData


interface LocalDataRepositoryImpl {

    fun saveUserSession(key: String?, value: String?)
    fun getUserSession(key: String, defaultValue: String = ""): String

    fun saveFcmToken(fcmToken: String)
    fun getFcmToken(): String
    fun clearFcmToken()

    fun saveBarrierToken(token: String)
    fun getBarrierToken(): String

    fun getCurrentLanguage(): String

    fun saveUserDetails(userData: String)
//    fun getUserDetails(): AuthData?

    fun resetUserData()

    fun saveInitData(initData: InitData?)
    fun getInitData(): InitData?
    fun getUserType(): String
}
