package com.example.fieldops.data.remote

import android.content.Context

class SessionManager(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            "fieldops_session",
            Context.MODE_PRIVATE
        )

    fun saveSession(
        token: String,
        email: String,
        name: String,
        role: String,
        companyId: String = ""
    ) {
        preferences.edit()
            .putString("token", token)
            .putString("email", email)
            .putString("name", name)
            .putString("role", role)
            .putString("companyId", companyId)
            .apply()
    }

    fun token(): String? =
        preferences.getString("token", null)

    fun email(): String = preferences.getString("email", "") ?: ""
    fun name(): String = preferences.getString("name", "") ?: ""
    fun role(): String = preferences.getString("role", "TECHNICIAN") ?: "TECHNICIAN"
    fun companyId(): String = preferences.getString("companyId", "") ?: ""

    fun isLoggedIn(): Boolean =
        !token().isNullOrBlank()

    fun clear() {
        preferences.edit().clear().apply()
    }
}
