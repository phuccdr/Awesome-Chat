package com.vie.mit.initdata

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient

object FirestoreSeeder {

    private val db = FirestoreClient.getFirestore()
    private val auth = FirebaseAuth.getInstance()

    private const val PASSWORD = "123456"
    private const val AVATAR =
        "https://tse1.mm.bing.net/th/id/OIP.GO6G_TdZ7S91ocRBcFh5kQHaHe?r=0&cb=thfvnextfalcon4&rs=1&pid=ImgDetMain&o=7&rm=3"

    fun seedUsers() {

        ('A'..'H').forEach { c ->

            val username = "User $c"
            val email = "nguyenvan${c.lowercaseChar()}@gmail.com"

            try {

                // B1. Tạo Firebase Auth
                val authUser = auth.createUser(
                    UserRecord.CreateRequest()
                        .setEmail(email)
                        .setPassword(PASSWORD)
                        .setDisplayName(username)
                )

                // B2. Tạo Firestore document với id = uid
                val user = mapOf(
                    "email" to email,
                    "username" to username,
                    "avatar" to AVATAR,
                    "language" to "vi"
                )

                db.collection("users")
                    .document(authUser.uid)
                    .set(user)
                    .get()

                println("Created $username (${authUser.uid})")

            } catch (e: Exception) {
                println("Create $username failed: ${e.message}")
            }
        }

        println("Seed completed")
    }

}