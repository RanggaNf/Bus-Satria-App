package com.bussatriaapp.data.repository

import android.content.Context
import com.bussatriaapp.utils.PreferenceUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val auth: FirebaseAuth) {

    suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String, context: Context): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            PreferenceUtil.setLoggedIn(context, true) // Setelah berhasil login
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout(context: Context) {
        auth.signOut()
        PreferenceUtil.setLoggedIn(context, false) // Setelah berhasil logout
    }
}
