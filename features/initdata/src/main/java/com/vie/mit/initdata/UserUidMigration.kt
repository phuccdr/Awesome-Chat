package com.vie.mit.initdata

import com.google.firebase.cloud.FirestoreClient

object UserUidMigration {

    private val db = FirestoreClient.getFirestore()

    /**
     * Duyệt qua collection users, nếu document nào chưa có uid thì thêm uid = documentId.
     */
    fun migrateUserUids() {
        println("Starting User UID migration...")
        
        try {
            val usersCollection = db.collection("users")
            val documents = usersCollection.get().get().documents
            
            var updatedCount = 0
            
            for (document in documents) {
                val docId = document.id
                val uid = document.getString("uid")
                
                if (uid == null || uid.isEmpty()) {
                    // Cập nhật document với uid là docId
                    usersCollection.document(docId)
                        .update("uid", docId)
                        .get()
                    
                    println("Updated document $docId with uid: $docId")
                    updatedCount++
                }
            }
            
            println("Migration completed. Total documents updated: $updatedCount")
            
        } catch (e: Exception) {
            println("Migration failed: ${e.message}")
            e.printStackTrace()
        }
    }
}
