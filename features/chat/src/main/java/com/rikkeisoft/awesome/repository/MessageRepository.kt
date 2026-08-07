package com.rikkeisoft.awesome.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.core.model.firebase.Conversation
import com.project.core.model.firebase.Message
import com.project.core.model.firebase.MessageType
import com.project.core.model.firebase.User
import com.project.core.utils.resource.ResourceUtils
import com.rikkeisoft.awesome.chat.R
import com.rikkeisoft.awesome.model.ConversationChat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val db: FirebaseFirestore, private val auth: FirebaseAuth
) {
    val PAGE_SIZE = 12L
    private var lastMessage: DocumentSnapshot? = null
    private var firstMessage: DocumentSnapshot? = null
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

    suspend fun firstLoadMessages(conversationId: String): List<Message> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot =
                    db.collection("conversations").document(conversationId).collection("messages")
                        .orderBy("createdAt", Query.Direction.ASCENDING).limitToLast(PAGE_SIZE)
                        .get().await()
                lastMessage = snapshot.documents.lastOrNull()
                firstMessage = snapshot.documents.firstOrNull()
                Timber.tag("ChatMessage").d("MessageRepository firstLoadMessages $snapshot")
                snapshot.toObjects(Message::class.java)
            } catch (e: Exception) {
                Timber.e(e)
                emptyList()
            }
        }
    }

    fun observeLatestMessages(
        conversationId: String
    ): Flow<Message> = callbackFlow {
        Timber.tag("ChatMessage").d("MessageRepository: observerLastestMessage $lastMessage")
        var query = db.collection("conversations").document(conversationId).collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
        if (lastMessage != null) {
            query = query.startAfter(lastMessage!!)
        }
        val observer = query.addSnapshotListener { snapshot, error ->
            Timber.d("snapshot = ${snapshot?.size()} error = $error")
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            snapshot?.documentChanges?.forEach { change ->
                if (change.type == DocumentChange.Type.ADDED) {
                    val message = change.document.toObject(Message::class.java).copy(
                        conversationId = conversationId
                    )
                    Timber.tag("ChatMessage")
                        .d("MessageRepository observeLatestMessages add: $message")
                    trySend(message)
                }
            }
        }
        awaitClose {
            observer.remove()
        }
    }

    suspend fun loadNextPage(conversationId: String?): List<Message> {
        if (conversationId == null) return emptyList()
        return withContext(Dispatchers.IO) {
            try {
                val query =
                    db.collection("conversations").document(conversationId).collection("messages")
                        .orderBy("createdAt", Query.Direction.ASCENDING)
                val finalQuery = if (firstMessage != null) {
                    query.endBefore(firstMessage!!)
                } else {
                    query
                }
                val snapshot = finalQuery.limitToLast(PAGE_SIZE).get().await()

                if (snapshot.documents.isNotEmpty()) {
                    firstMessage = snapshot.documents.firstOrNull()
                }
                Timber.tag("Chat Message")
                    .d("MessageRepository loadNextPage add: ${snapshot.toObjects(Message::class.java)}")
                snapshot.toObjects(Message::class.java)
            } catch (e: Exception) {
                Timber.e(e)
                emptyList()
            }
        }
    }

    suspend fun updateUnread(conversationId: String) {
        withContext(Dispatchers.IO) {
            db.collection("conversations").document(conversationId)
                .update("unreadMessage.${auth.uid}", 0).await()
        }
    }

    suspend fun sendMessage(conversationId: String, message: Message) {
        withContext(Dispatchers.IO) {
            val conversationRef = db.collection("conversations").document(conversationId)
            val messageRef = conversationRef.collection("messages").document()
            val lastMessage = when (message.type) {
                MessageType.TEXT -> {
                    message.content
                }

                MessageType.IMAGE -> {
                    ResourceUtils.getString(R.string.sent_image)
                }

                MessageType.STICKER -> {
                    ResourceUtils.getString(R.string.sent_sticker)
                }

                else -> {
                    ResourceUtils.getString(R.string.sent_message)
                }
            }
            val updates = hashMapOf<String, Any>(
                "lastMessage" to lastMessage,
                "lastSenderId" to message.senderId,
                "lastUpdate" to (message.createdAt ?: Timestamp.now())
            )

            message.receiverId?.let {
                updates["unreadMessage.$it"] = FieldValue.increment(1)
            }

            db.batch().apply {
                set(messageRef, message.copy(id = messageRef.id))
                update(conversationRef, updates)
            }.commit().await()
        }
    }
}