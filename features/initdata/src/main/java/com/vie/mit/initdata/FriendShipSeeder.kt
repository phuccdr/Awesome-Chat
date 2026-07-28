package com.vie.mit.initdata

import com.google.cloud.Timestamp
import com.google.firebase.cloud.FirestoreClient

object FriendShipSeeder {
    private val db = FirestoreClient.getFirestore()

    fun seed() {
        println("Seeding friends subcollections...")
        val users = db.collection("users").get().get().documents

        users.forEach { userDoc ->
            val userId = userDoc.id
            val username = userDoc.getString("username") ?: "Unknown"
            
            val friendsSubcollection = db.collection("users").document(userId).collection("friends")
            val currentFriends = friendsSubcollection.get().get().documents
            
            println("User $username ($userId) has ${currentFriends.size} friends. Target: 8")
            
            if (currentFriends.size < 8) {
                val currentFriendIds = currentFriends.mapNotNull { it.getString("friendId") }.toSet()
                
                // Potential friends: all users except self and already friends
                val potentialFriends = users.filter { it.id != userId && !currentFriendIds.contains(it.id) }
                    .shuffled()
                
                val needed = 8 - currentFriends.size
                val toAdd = potentialFriends.take(needed)
                
                toAdd.forEach { friendDoc ->
                    val friendId = friendDoc.id
                    val friendName = friendDoc.getString("username") ?: "Unknown"
                    
                    val friendshipRef = friendsSubcollection.document()
                    val friendshipData: Map<String, Any?> = hashMapOf(
                        "id" to friendshipRef.id,
                        "friendId" to friendId,
                        "friendFirstName" to friendName,
                        "createdAt" to Timestamp.now(),
                        "status" to "ACTIVE",
                        "conversationId" to null
                    )
                    
                    friendshipRef.set(friendshipData).get()
                    println("  Added $friendName as friend for $username")
                }
            }
        }

        println("FriendShip seeding DONE")
    }
}
