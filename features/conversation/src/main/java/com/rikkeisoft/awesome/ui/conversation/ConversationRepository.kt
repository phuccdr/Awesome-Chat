package com.rikkeisoft.awesome.ui.conversation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.core.model.firebase.Conversation
import com.project.core.model.firebase.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ConversationRepository @Inject constructor(
    private val auth: FirebaseAuth, private val db: FirebaseFirestore
) {
    private var lastDocument: DocumentSnapshot? = null
    private var currentUserUid: String = auth.currentUser?.uid ?: ""

    suspend fun getNextPage(): List<Conversation> {
        var query = db.collection("conversations").orderBy("lastUpdate", Query.Direction.DESCENDING)
            .limit(20)

        lastDocument?.let {
            query = query.startAfter(it)
        }
        val snapshot = query.get().await()

        Timber.d(snapshot.documents.toString())

        if (snapshot.documents.isNotEmpty()) {
            lastDocument = snapshot.documents.last()
        }
        return snapshot.documents.mapNotNull { document ->
            document.toObject(Conversation::class.java)
        }
    }

    suspend fun handleGetFriendByMembers(members: List<String>?): User? {
        if (members.isNullOrEmpty()) return User();
        val friendId = members.toMutableList().apply { remove(currentUserUid) }.first()
        return getUserByUid(friendId)
    }

    suspend fun getUserByUid(uid: String): User? {
        val query = db.collection("users").document(uid)
        val snapshot = query.get().await()
        return snapshot.toObject(User::class.java)
    }

    fun observeConversations(): Flow<List<Conversation>> = callbackFlow {
        val registration = db.collection("conversations").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val conversations = snapshot?.documents?.mapNotNull {
                it.toObject(Conversation::class.java)
            } ?: emptyList()

            trySend(conversations)
        }

        awaitClose {
            registration.remove()
        }
    }
}