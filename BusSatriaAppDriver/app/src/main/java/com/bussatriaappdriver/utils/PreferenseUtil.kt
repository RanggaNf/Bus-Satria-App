package com.bussatriaappdriver.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtil {
    private const val PREF_NAME = "com.exobin.prefs"
    private const val KEY_IS_DRIVER = "isDriver"
    private const val KEY_DRIVER_PASSCODE = "driverPasscode"

    fun setDriver(context: Context, isDriver: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(KEY_IS_DRIVER, isDriver)
            apply()
        }
    }

    fun isDriver(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_DRIVER, false)
    }

    fun setDriverPasscode(context: Context, passcode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(KEY_DRIVER_PASSCODE, passcode)
            apply()
        }
    }

    fun getDriverPasscode(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DRIVER_PASSCODE, null)
    }
    fun isDriverLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_DRIVER, false)
    }
}