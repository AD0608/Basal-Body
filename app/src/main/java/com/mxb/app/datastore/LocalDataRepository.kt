package com.mxb.app.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.mxb.app.model.response.InitData
import com.mxb.app.extensions.withNotNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class LocalDataRepository @Inject constructor(
    private val context: Context,
    private val gson: Gson
) : LocalDataRepositoryImpl {

    // DataStore declarations
    private val Context.userDataStore by preferencesDataStore(name = "user_pref")
    private val Context.fcmDataStore by preferencesDataStore(name = "fcm_pref")

    companion object {
        val BARRIER_TOKEN_KEY = stringPreferencesKey("barrier_token")
        val USER_DETAILS_KEY = stringPreferencesKey("user_details")
        val INIT_DATA_KEY = stringPreferencesKey("init_data")
        val SELECTED_LNG_KEY = stringPreferencesKey("selected_lng")
        val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")
        val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("is_onboarding_completed")
    }

    override fun saveUserSession(key: String?, value: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            key?.let {
                context.userDataStore.edit { preferences ->
                    preferences[stringPreferencesKey(it)] = value.orEmpty()
                }
            }
        }
    }

    override fun getUserSession(key: String, defaultValue: String): String {
        return runBlocking {
            context.userDataStore.data.first()[stringPreferencesKey(key)].withNotNull {
                it
            } ?: run { return@run defaultValue }
        }
    }

    override fun saveFcmToken(fcmToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            context.fcmDataStore.edit { preferences ->
                preferences[FCM_TOKEN_KEY] = fcmToken
            }
        }
    }

    override fun getFcmToken(): String {
        return runBlocking {
            context.fcmDataStore.data.first()[FCM_TOKEN_KEY].withNotNull { it }
                ?: run { return@run "" }
        }
    }

    override fun clearFcmToken() {
        CoroutineScope(Dispatchers.IO).launch {
            context.fcmDataStore.edit { it.clear() }
        }
    }

    override fun saveBarrierToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            context.userDataStore.edit { preferences ->
                preferences[BARRIER_TOKEN_KEY] = token
            }
        }
    }

    override fun getBarrierToken(): String {
        return runBlocking {
            context.userDataStore.data.first()[BARRIER_TOKEN_KEY].withNotNull { it }
                ?: run { return@run "" }
        }
    }

    override fun getCurrentLanguage(): String {
        return runBlocking {
            context.userDataStore.data.first()[SELECTED_LNG_KEY].withNotNull { it }
                ?: run { return@run "en" }
        }
    }

    override fun saveUserDetails(userData: String) {
        CoroutineScope(Dispatchers.IO).launch {
            context.userDataStore.edit { preferences ->
                preferences[USER_DETAILS_KEY] = userData
            }
        }
    }

//    override fun getUserDetails(): AuthData? {
//        val userData = runBlocking {
//            context.userDataStore.data.first()[USER_DETAILS_KEY].withNotNull { it }
//                ?: run { return@run "" }
//        }
//        return if (userData.isEmpty()) null else gson.fromJson(userData, AuthData::class.java)
//    }

    override fun getUserType(): String {
        return runBlocking {
            "company"
        }
    }

    override fun resetUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            context.userDataStore.edit { preferences ->
                preferences[USER_DETAILS_KEY] = ""
                preferences[BARRIER_TOKEN_KEY] = ""
                preferences[FCM_TOKEN_KEY] = ""
            }
        }
    }

    override fun saveInitData(initData: InitData?) {
        CoroutineScope(Dispatchers.IO).launch {
            context.userDataStore.edit { preferences ->
                preferences[INIT_DATA_KEY] = gson.toJson(initData)
            }
        }
    }

    override fun getInitData(): InitData? {
        val initData = runBlocking {
            context.userDataStore.data.first()[INIT_DATA_KEY].withNotNull { it }
                ?: run { return@run "" }
        }
        return if (initData.isEmpty()) null else gson.fromJson(initData, InitData::class.java)
    }
}