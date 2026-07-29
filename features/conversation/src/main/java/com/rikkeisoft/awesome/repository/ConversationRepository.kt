package com.rikkeisoft.awesome.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.core.model.firebase.Conversation
import com.project.core.model.firebase.Message
import com.project.core.model.firebase.User
import com.rikkeisoft.awesome.model.SearchMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

const val PAGE_SIZE = 20L

class ConversationRepository @Inject constructor(
    auth: FirebaseAuth, private val db: FirebaseFirestore
) {
    private val currentUserUid: String = auth.currentUser?.uid ?: ""

    suspend fun handleGetFriendByMembers(members: List<String>?): User? {
        if (members.isNullOrEmpty()) return null
        val friendId = members.firstOrNull { it != currentUserUid } ?: return null
        return getUserByUid(friendId)
    }

    suspend fun getUserByUid(uid: String): User? = withContext(Dispatchers.IO) {
        val query = db.collection("users").document(uid)
        val snapshot = query.get().await()
        return@withContext snapshot.toObject(User::class.java)
    }

    fun observeConversations(limit: Long = PAGE_SIZE): Flow<List<Conversation>> = callbackFlow {
        Timber.d("Called: observeConversations limit: $limit")
        val query = db.collection("conversations").whereNotEqualTo("lastMessage", "")
            .whereArrayContains("members", currentUserUid)
            .orderBy("lastUpdate", Query.Direction.DESCENDING).limit(limit)
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Timber.e(error, "Error observing conversations")
                return@addSnapshotListener
            }
            val conversations = snapshot?.documents?.mapNotNull { document ->
                document.toObject(Conversation::class.java)?.copy(id = document.id)
            } ?: emptyList()

            trySend(conversations)
        }

        awaitClose {
            registration.remove()
        }
    }

    suspend fun searchConversation(keyword: String): List<SearchMessage> {
        val snapshot = db.collectionGroup("messages").orderBy("content").startAt(keyword)
            .endAt(keyword + "\uf8ff").limit(50).get().await()
        val sortedDocs = snapshot.documents.sortedByDescending {
            it.toObject(Message::class.java)?.createdAt
        }
        Timber.d(sortedDocs.toString())
        val conversationsGrouped = sortedDocs.groupBy { it.reference.parent.parent }
        val semaphore = Semaphore(20)

        return coroutineScope {
            conversationsGrouped.map { (conversationRef, docs) ->
                async {
                    semaphore.withPermit {
                        val messages = docs.mapNotNull { it.toObject(Message::class.java) }
                        val conversation =
                            conversationRef?.get()?.await()?.toObject(Conversation::class.java)
                                ?: return@withPermit null
                        if (!conversation.members.contains(currentUserUid)) return@withPermit null
                        val friend =
                            handleGetFriendByMembers(conversation.members) ?: return@withPermit null
                        SearchMessage(conversationRef, messages, friend)
                    }
                }
            }.awaitAll().filterNotNull()
        }
    }
}
