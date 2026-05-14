package com.bussatriaapp.data.repository

import android.content.Context
import com.bussatriaapp.utils.PreferenceUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    suspend fun register(email: String, password: String, name: String, role: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                val user = hashMapOf(
                    "name" to name,
                    "role" to role,
                    "email" to email
                )
                firestore.collection("users").document(firebaseUser.uid)
                    .set(user).await()
            }
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
    suspend fun getUserData(): Result<Map<String, Any>> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                Result.success(document.data ?: emptyMap())
            } else {
                Result.failure(Exception("User data not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateUserData(updatedData: Map<String, Any>): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            firestore.collection("users").document(userId)
                .update(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not logged in")
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}