package com.rikkeisoft.awesome.ui.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.core.model.firebase.Conversation
import com.project.core.model.firebase.Message
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.model.ConversationChat
import com.rikkeisoft.awesome.ui.conversation.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val db: FirebaseFirestore, private val auth: FirebaseAuth
) {
    suspend fun getConversation(conversationId: String): ConversationChat =
        withContext(Dispatchers.IO) {
            val conversationDoc =
                db.collection("conversations").document(conversationId).get().await()
            val conversation = conversationDoc.toObject(Conversation::class.java)
            val currentUserUid = auth.currentUser?.uid ?: ""
            val friendId = conversation?.members?.firstOrNull { it != currentUserUid } ?: ""
            val currentUserDeferred = async {
                if (currentUserUid.isNotEmpty()) {
                    db.collection("users").document(currentUserUid).get().await()
                        .toObject(User::class.java)
                } else null
            }
            val friendDeferred = async {
                if (friendId.isNotEmpty()) {
                    db.collection("users").document(friendId).get().await()
                        .toObject(User::class.java)
                } else null
            }

            ConversationChat(
                id = conversationId,
                friend = friendDeferred.await(),
                currentlyUser = currentUserDeferred.await()
            )
        }

    fun observeMessages(conversationId: String, limit: Long = PAGE_SIZE): Flow<List<Message>> = callbackFlow {
        val query = db.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val messages = snapshot?.toObjects(Message::class.java)?.reversed() ?: emptyList()
            trySend(messages)
        }
        awaitClose { registration.remove() }
    }
}
