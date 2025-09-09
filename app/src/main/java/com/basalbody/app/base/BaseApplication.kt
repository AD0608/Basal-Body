package com.basalbody.app.base

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application(), LifecycleObserver {

    var isAppRunning = false


    init {
        instance = this
    }

    companion object {
        var instance: BaseApplication? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        //--------HERE INITIALIZE FIREBASE SDK---------//
        FirebaseApp.initializeApp(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}