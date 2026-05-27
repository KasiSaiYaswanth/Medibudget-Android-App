package com.medibudget.app.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "medibudget_shared_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_BIOMETRICS_ENABLED = "biometrics_enabled"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_SAVED_PASSWORD = "saved_password" // Encrypted or plain for demo
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userEmail: String?
        get() = prefs.getString(KEY_USER_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var userToken: String?
        get() = prefs.getString(KEY_USER_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_USER_TOKEN, value).apply()

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var isBiometricsEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRICS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRICS_ENABLED, value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, true) // Dark theme by default
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var savedPassword: String?
        get() = prefs.getString(KEY_SAVED_PASSWORD, null)
        set(value) = prefs.edit().putString(KEY_SAVED_PASSWORD, value).apply()

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putString(KEY_USER_TOKEN, null)
            .putString(KEY_USER_ID, null)
            .putString(KEY_SAVED_PASSWORD, null)
            .apply()
    }
}
