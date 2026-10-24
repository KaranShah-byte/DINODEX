package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)

    companion object {
        private const val TAG = "AuthManager"
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Reactive stream of current Firebase user
     */
    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /**
     * Interactive Google Sign-In via Jetpack CredentialManager
     */
    suspend fun signInWithGoogle(): Result<FirebaseUser> {
        return try {
            val serverClientId = context.getString(R.string.default_web_client_id)
            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user

                if (user != null) {
                    // Record user profile in Firestore
                    saveUserProfile(user)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Authentication completed but user profile is null"))
                }
            } else {
                Result.failure(Exception("Unexpected credential type received: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-In was cancelled by user: ${e.message}")
            Result.failure(e)
        } catch (e: Throwable) {
            Log.e(TAG, "Google Sign-In failed", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private suspend fun saveUserProfile(user: FirebaseUser) {
        try {
            val databaseId = context.getString(R.string.firestore_database_id)
            val firestore = FirebaseFirestore.getInstance(databaseId)

            val userData = hashMapOf<String, Any>(
                "userId" to user.uid,
                "email" to (user.email ?: ""),
                "displayName" to (user.displayName ?: "Paleontologist"),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "lastLoginAt" to FieldValue.serverTimestamp()
            )

            firestore.collection("users").document(user.uid)
                .set(userData, SetOptions.merge())
                .await()
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to persist user profile to Firestore: ${e.message}")
        }
    }

    suspend fun syncFavoriteToCloud(dinoId: String, name: String, era: String, diet: String, isFavorite: Boolean) {
        val user = auth.currentUser ?: return
        try {
            val databaseId = context.getString(R.string.firestore_database_id)
            val firestore = FirebaseFirestore.getInstance(databaseId)
            val favRef = firestore.collection("users").document(user.uid)
                .collection("favorites").document(dinoId)

            if (isFavorite) {
                val data = hashMapOf<String, Any>(
                    "dinoId" to dinoId,
                    "name" to name,
                    "era" to era,
                    "diet" to diet,
                    "savedAt" to FieldValue.serverTimestamp()
                )
                favRef.set(data).await()
            } else {
                favRef.delete().await()
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to sync favorite to cloud: ${e.message}")
        }
    }

    suspend fun fetchCloudFavorites(): Set<String> {
        val user = auth.currentUser ?: return emptySet()
        return try {
            val databaseId = context.getString(R.string.firestore_database_id)
            val firestore = FirebaseFirestore.getInstance(databaseId)
            val snapshot = firestore.collection("users").document(user.uid)
                .collection("favorites")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.getString("dinoId") ?: it.id }.toSet()
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to fetch cloud favorites: ${e.message}")
            emptySet()
        }
    }
}
