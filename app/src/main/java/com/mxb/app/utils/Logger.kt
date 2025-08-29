package com.mxb.app.utils

import android.util.Log
import com.mxb.app.BuildConfig

/**Custom logger class, this class will print log only for [BuildConfig.DEBUG] build & when [BuildConfig.ENABLE_LOG] is [true] */
object Logger {
    fun v(tag: String, msg: String) {
        if (BuildConfig.DEBUG && BuildConfig.ENABLE_LOG) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG && BuildConfig.ENABLE_LOG) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (BuildConfig.DEBUG && BuildConfig.ENABLE_LOG) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String?, msg: String) {
        if (BuildConfig.DEBUG && BuildConfig.ENABLE_LOG) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (BuildConfig.DEBUG && BuildConfig.ENABLE_LOG) {
            Log.e(tag, msg)
        }
    }

    fun e(msg: String) {
        if (BuildConfig.DEBUG && BuildConfig.ENABLE_LOG) {
            Log.e("AjayEWW-->", msg)
        }
    }

}