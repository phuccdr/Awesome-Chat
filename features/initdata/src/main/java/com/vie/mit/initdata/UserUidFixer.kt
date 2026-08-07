package com.vie.mit.initdata

import com.google.firebase.cloud.FirestoreClient

object UserUidFixer {

    private val db by lazy { FirestoreClient.getFirestore() }

    /**
     * Iterates through the 'users' collection and adds a 'uid' field
     * set to the document ID if it's missing.
     */
    fun fixMissingUids() {
        println(">>> Starting UID fix migration for 'users' collection...")

        try {
            val usersCollection = db.collection("users")
            val documents = usersCollection.get().get().documents
            
            var processedCount = 0
            var updatedCount = 0

            for (document in documents) {
                processedCount++
                val docId = document.id
                val uid = document.getString("uid")

                if (uid.isNullOrEmpty()) {
                    // Update document with its own ID as the uid
                    usersCollection.document(docId)
                        .update("uid", docId)
                        .get()
                    
                    println("    [FIXED] Document ID: $docId -> uid: $docId")
                    updatedCount++
                }
            }

            println(">>> Migration finished.")
            println("    Total processed: $processedCount")
            println("    Total updated:   $updatedCount")

        } catch (e: Exception) {
            println(">>> Migration failed: ${e.message}")
            e.printStackTrace()
        }
    }
}
