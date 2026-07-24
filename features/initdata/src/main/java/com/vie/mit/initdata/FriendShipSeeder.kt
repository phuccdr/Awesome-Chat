package com.vie.mit.initdata

import com.google.cloud.Timestamp
import com.google.firebase.cloud.FirestoreClient

object FriendShipSeeder {
    private val db = FirestoreClient.getFirestore()
    private const val MY_UID = "9XMtQAMCW1YZkN3gKVnCgYbBh7n2"

    fun seed() {
        println("Seeding friend_ships...")
        val users = db.collection("users").get().get().documents
        val conversations = db.collection("conversations").get().get().documents

        users.forEach { userDoc ->
            val otherUid = userDoc.id
            if (otherUid == MY_UID) return@forEach

            // Tìm conversation giữa MY_UID và otherUid
            val conversation = conversations.find { doc ->
                val members = doc.get("members") as? List<*>
                members?.contains(MY_UID) == true && members.contains(otherUid)
            }

            val friendShipRef = db.collection("friend_ships").document()
            val friendShip = hashMapOf<String, Any?>(
                "id" to friendShipRef.id,
                "userId1" to MY_UID,
                "userId2" to otherUid,
                "conversationId" to conversation?.id,
                "createdAt" to Timestamp.now(),
                "status" to "ACTIVE"
            )

            friendShipRef.set(friendShip).get()
            println("FriendShip created between $MY_UID and $otherUid (Conversation: ${conversation?.id ?: "None"})")
        }

        println("FriendShip seeding DONE")
    }
}
