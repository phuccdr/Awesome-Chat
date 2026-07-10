package com.vie.mit.initdata

import com.google.cloud.Timestamp
import com.google.firebase.cloud.FirestoreClient
import kotlin.random.Random

object ConversationSeeder {
    private val db = FirestoreClient.getFirestore()
    private const val MY_UID = "9XMtQAMCW1YZkN3gKVnCgYbBh7n2"
    private val lastMessages = listOf(
        "Hello bro", "Làm xong bài tập thầy Hoàng giao chưa ?"
    )
    private val messageSamples = listOf(
        "Hello bro",
        "Chào nhé",
        "Đang làm gì vậy?",
        "Ăn cơm chưa?",
        "Mai đi học không?",
        "Ok nhé",
        "Haha",
        "Được rồi",
        "Làm xong bài tập thầy Hoàng giao chưa ?",
        "Mai gặp nhé"
    )

    fun seed() {
        val users = db.collection("users").get().get().documents

        users.forEach { user ->
            val otherUid = user.id

            if (otherUid == MY_UID) return@forEach
            val conversationRef = db.collection("conversations").document()
            val lastSender = if (Random.nextBoolean()) MY_UID else otherUid
            val conversation = hashMapOf(
                "id" to conversationRef.id,
                "lastMessage" to lastMessages.random(),
                "lastSenderId" to lastSender,
                "lastUpdate" to Timestamp.now(),
                "members" to listOf(MY_UID, otherUid),
                "unreadMessage" to mapOf(
                    MY_UID to Random.nextInt(0, 12), otherUid to Random.nextInt(0, 12)
                )
            )

            conversationRef.set(conversation).get()

            seedMessages(
                conversationRef = conversationRef, userA = MY_UID, userB = otherUid
            )

            println("Conversation created : ${conversationRef.id}")
        }

        println("DONE")
    }

    private fun seedMessages(
        conversationRef: com.google.cloud.firestore.DocumentReference, userA: String, userB: String
    ) {
        val messageCount = Random.nextInt(8, 25)

        repeat(messageCount) {
            val sender = if (Random.nextBoolean()) userA else userB
            val message = hashMapOf(
                "content" to messageSamples.random(),
                "createdAt" to Timestamp.now(),
                "imageUrl" to null,
                "seen" to Random.nextBoolean(),
                "senderId" to sender,
                "type" to "TEXT"
            )

            conversationRef.collection("messages").document().set(message.toMap()).get()
        }
    }
}