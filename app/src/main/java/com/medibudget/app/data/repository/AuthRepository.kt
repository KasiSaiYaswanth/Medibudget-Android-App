package com.medibudget.app.data.repository

import android.content.Context
import com.medibudget.app.data.remote.NetworkClient
import com.medibudget.app.utils.SessionManager
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(context: Context) {

    private val sessionManager = SessionManager(context)
    private val authClient = NetworkClient.supabase.gotrue

    suspend fun login(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val session = authClient.loginWith(Email) {
                this.email = email
                this.password = password
            }
            sessionManager.isLoggedIn = true
            sessionManager.userEmail = email
            sessionManager.savedPassword = password // Cache for biometrics bypass
            sessionManager.userId = sessionManager.userEmail // Placeholder or extract from JWT
            Result.success("Login Successful")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            authClient.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success("Registration Successful. Please check your inbox for confirmation!")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            authClient.resetPasswordForEmail(email)
            Result.success("Password reset email sent successfully!")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn
    }

    fun logout() {
        sessionManager.logout()
    }

    fun getUserEmail(): String? {
        return sessionManager.userEmail
    }

    fun enableBiometrics(enable: Boolean) {
        sessionManager.isBiometricsEnabled = enable
    }

    fun isBiometricsEnabled(): Boolean {
        return sessionManager.isBiometricsEnabled
    }

    fun getSavedPassword(): String? {
        return sessionManager.savedPassword
    }
}
