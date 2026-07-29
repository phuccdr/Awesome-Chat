package com.rikkeisoft.awesome.ui

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth, private val db: FirebaseFirestore
) {
    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(
        email: String, password: String, username: String
    ) {
        var user: FirebaseUser? = null
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            user = result.user
            val firstName = username.split(Regex("\\s+")).last()
            val data = hashMapOf(
                "avatar" to "",
                "username" to username,
                "email" to email,
                "language" to "vi",
                "firstName" to firstName
            )
            db.collection("users").document(user!!.uid).set(data).await()
        } catch (e: Exception) {
            user?.delete()?.await()
            throw e
        }
    }
}