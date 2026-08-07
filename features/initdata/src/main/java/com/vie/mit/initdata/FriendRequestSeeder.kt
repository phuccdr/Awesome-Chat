package com.vie.mit.initdata

import com.google.cloud.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient

object FriendRequestSeeder {
    private val db = FirestoreClient.getFirestore()
    private val auth = FirebaseAuth.getInstance()

    private const val PASSWORD = "123456"
    private const val AVATAR =
        "https://tse4.mm.bing.net/th/id/OIP.3fov8CMqRz6Zp89_Pk3fDgHaEK?r=0&rs=1&pid=ImgDetMain&o=7&rm=3"

    fun createMockUsersAndRequests(targetUserId: String, count: Int = 1) {
        repeat(count) { index ->
            val suffix = System.nanoTime().toString().takeLast(6)
            val mockUsername = "Mock User $suffix"
            val mockEmail = "mockuser_$suffix@example.com"

            try {
                // 1. Create Mock User in Firebase Auth
                val authUser = auth.createUser(
                    UserRecord.CreateRequest()
                        .setEmail(mockEmail)
                        .setPassword(PASSWORD)
                        .setDisplayName(mockUsername)
                )

                val senderId = authUser.uid

                // 2. Create User document in Firestore
                val user = mapOf(
                    "email" to mockEmail,
                    "username" to mockUsername,
                    "avatar" to AVATAR,
                    "language" to "vi",
                    "uid" to senderId
                )

                db.collection("users")
                    .document(senderId)
                    .set(user)
                    .get()

                println("[${index + 1}/$count] Created Mock User: $mockUsername ($senderId)")

                // 3. Create Friend Request to targetUserId
                val requestRef = db.collection("friends_request").document()
                val requestData: Map<String, Any?> = hashMapOf(
                    "id" to requestRef.id,
                    "senderId" to senderId,
                    "receiverId" to targetUserId,
                    "status" to "PENDING",
                    "createdAt" to Timestamp.now(),
                    "acceptedAt" to null
                )

                requestRef.set(requestData).get()
                println("      -> Friend request sent to $targetUserId")

            } catch (e: Exception) {
                println("Failed to create mock user/request at index $index: ${e.message}")
            }
        }
    }

    fun seed() {
        println("Seeding friend requests...")
        val users = db.collection("users").get().get().documents
        
        // Load all existing friend requests to avoid duplicates
        val existingRequests = db.collection("friends_request").get().get().documents
        val requestPairs = existingRequests.map { doc ->
            val sId = doc.getString("senderId") ?: ""
            val rId = doc.getString("receiverId") ?: ""
            setOf(sId, rId)
        }.toSet()

        users.forEach { senderDoc ->
            val senderId = senderDoc.id
            val senderName = senderDoc.getString("username") ?: "Unknown"

            // Get sender's existing friends
            val friendsSubcollection = db.collection("users").document(senderId).collection("friends")
            val currentFriends = friendsSubcollection.get().get().documents
            val currentFriendIds = currentFriends.mapNotNull { it.getString("friendId") }.toSet()

            // Find users who are not friends and don't have a pending request
            val potentialReceivers = users.filter { receiverDoc ->
                val receiverId = receiverDoc.id
                receiverId != senderId && 
                !currentFriendIds.contains(receiverId) && 
                !requestPairs.contains(setOf(senderId, receiverId))
            }

            // Let's create a few requests for each user if they don't have many
            if (potentialReceivers.isNotEmpty()) {
                val toAdd = potentialReceivers.shuffled().take(2) // Create 2 requests per user for testing
                
                toAdd.forEach { receiverDoc ->
                    val receiverId = receiverDoc.id
                    val receiverName = receiverDoc.getString("username") ?: "Unknown"

                    val requestRef = db.collection("friends_request").document()
                    val requestData: Map<String, Any?> = hashMapOf(
                        "id" to requestRef.id,
                        "senderId" to senderId,
                        "receiverId" to receiverId,
                        "status" to "PENDING",
                        "createdAt" to Timestamp.now(),
                        "acceptedAt" to null
                    )

                    requestRef.set(requestData).get()
                    println("  Friend request sent from $senderName to $receiverName")
                }
            }
        }

        println("FriendRequest seeding DONE")
    }

    fun seedRequestsFromSender(senderId: String) {
        println("Seeding friend requests from sender: $senderId...")
        
        val senderDoc = db.collection("users").document(senderId).get().get()
        if (!senderDoc.exists()) {
            println("Error: Sender $senderId not found in users collection.")
            return
        }
        val senderName = senderDoc.getString("username") ?: "Unknown"

        val users = db.collection("users").get().get().documents
        
        // Get sender's existing friends
        val friendsSubcollection = db.collection("users").document(senderId).collection("friends")
        val currentFriends = friendsSubcollection.get().get().documents
        val currentFriendIds = currentFriends.mapNotNull { it.getString("friendId") }.toSet()

        val potentialReceivers = users.filter { receiverDoc ->
            val receiverId = receiverDoc.id
            receiverId != senderId && !currentFriendIds.contains(receiverId)
        }

        println("Found ${potentialReceivers.size} potential receivers.")

        potentialReceivers.forEach { receiverDoc ->
            val receiverId = receiverDoc.id
            val receiverName = receiverDoc.getString("username") ?: "Unknown"

            // Use a deterministic ID to avoid duplicates
            val requestId = "${senderId}_${receiverId}"
            val requestRef = db.collection("friends_request").document(requestId)
            
            val requestData: Map<String, Any?> = hashMapOf(
                "id" to requestId,
                "senderId" to senderId,
                "receiverId" to receiverId,
                "status" to "PENDING",
                "createdAt" to Timestamp.now(),
                "acceptedAt" to null
            )

            requestRef.set(requestData).get()
            println("  Friend request sent from $senderName to $receiverName ($receiverId)")
        }

        println("Seeding from $senderId DONE")
    }
}
