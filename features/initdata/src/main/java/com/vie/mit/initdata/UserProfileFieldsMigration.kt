package com.vie.mit.initdata

import com.google.cloud.Timestamp
import com.google.firebase.cloud.FirestoreClient

object UserProfileFieldsMigration {

    private val db = FirestoreClient.getFirestore()

    /**
     * Duyệt qua collection users và cập nhật phoneNumber, birthOfDay với các giá trị truyền vào.
     */
    fun migrateUserProfileFields(phoneNumber: String, birthOfDay: Timestamp?) {
        println("Starting User profile fields migration...")
        println("Target values: phoneNumber=$phoneNumber, birthOfDay=$birthOfDay")
        
        try {
            val usersCollection = db.collection("users")
            val documents = usersCollection.get().get().documents
            
            var updatedCount = 0
            
            for (document in documents) {
                val docId = document.id
                val updates = mutableMapOf<String, Any?>()
                
                updates["phoneNumber"] = phoneNumber
                updates["birthOfDay"] = birthOfDay
                
                // Cập nhật document
                usersCollection.document(docId)
                    .update(updates)
                    .get()
                
                println("Updated document $docId")
                updatedCount++
            }
            
            println("Migration completed. Total documents updated: $updatedCount")
            
        } catch (e: Exception) {
            println("Migration failed: ${e.message}")
            e.printStackTrace()
        }
    }
}
