package com.bussatriaappdriver.data.repository

import android.content.Context
import android.util.Log
import com.bussatriaappdriver.utils.PreferenceUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class DriverAuthRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun verifyDriverPasscode(passcode: String, context: Context): Result<Unit> {
        return try {
            val snapshot = firestore.collection("drivers")
                .whereEqualTo("passcode", passcode)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.failure(Exception("Invalid passcode"))
            }

            val docId = snapshot.documents[0].id
            val isUsed = snapshot.documents[0].getBoolean("isUsed") ?: false

            if (isUsed) {
                return Result.failure(Exception("Passcode already used"))
            }

            firestore.collection("drivers").document(docId)
                .update("isUsed", true)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logoutDriver(context: Context): Result<Unit> {
        return try {
            val passcode = PreferenceUtil.getDriverPasscode(context) ?: return Result.failure(Exception("Passcode not found"))
            val snapshot = firestore.collection("drivers")
                .whereEqualTo("passcode", passcode)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val docId = snapshot.documents[0].id
                firestore.collection("drivers").document(docId)
                    .update("isUsed", false)
                    .await()
            }

            // Sign out from FirebaseAuth
            auth.signOut()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "DriverAuthRepository"
    }
}

